package com.eddiapps.foodify.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eddiapps.foodify.domain.PreferencesInterface
import com.eddiapps.foodify.domain.calculation.UnitConverter
import com.eddiapps.foodify.domain.calculation.calculateDailyCalorieLimit
import com.eddiapps.foodify.domain.model.UnitSystem
import com.eddiapps.foodify.domain.model.onboarding.WeightGoal
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Locale
import kotlin.math.roundToInt

class SettingsViewModel(private val prefRepo: PreferencesInterface) : ViewModel() {

    val isDarkMode: Flow<Boolean> = prefRepo.darkMode
    val isReminding: Flow<Boolean> = prefRepo.reminder
    val onboardingData = prefRepo.onboardingData.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        null
    )
    val unitSystem = onboardingData
        .map { it?.unitSystem ?: UnitSystem.METRIC }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            UnitSystem.METRIC
        )
    val pickerState = onboardingData.map { it?.pickerState }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000), null
    )
    val weightGoal: WeightGoal = onboardingData.value?.weightGoal ?: WeightGoal.NORMAL
    val weightUnit: String
        get() = if (unitSystem.value == UnitSystem.METRIC) "kg" else "lb"
    val heightUnit: String
        get() = if (unitSystem.value == UnitSystem.METRIC) "cm" else "ft/in"
    val heightDisplay: String
        get() {
            return if (unitSystem.value == UnitSystem.METRIC) {
                pickerState.value?.heightCm.toString()
            } else {
                "${pickerState.value?.heightFt}'${pickerState.value?.heightIn} "
            }
        }
    val weightDisplay: String
        get() {
            return if (unitSystem.value == UnitSystem.METRIC) {
                String.format(Locale.getDefault(), "%.1f", pickerState.value?.weightKg)
            } else {
                "${pickerState.value?.weightLb}"
            }
        }
    val unitDisplay: String
        get() {
            return if (unitSystem.value == UnitSystem.METRIC) {
                "kg / cm"
            } else {
                "lb / feet"
            }
        }

    val weeklyGoal: Double
        get() = onboardingData.value?.weightGoal?.kgPerWeek ?: 0.5
    val weeklyGoalDisplay: Double
        get() {
            return if (unitSystem.value == UnitSystem.METRIC) {
                weeklyGoal
            } else {
                val weeklyGoalLbs = weeklyGoal * 2
                (weeklyGoalLbs * 10).roundToInt() / 10.0
            }
        }
//    private val _pickerState = MutableStateFlow(
//        PickerStateData(36, 180, 80.0, 5, 11, 176)
//    )
//    val pickerState = _pickerState.asStateFlow()

    init {
//        viewModelScope.launch {
//            onboardingData.filterNotNull().first().let { onboardingData ->
//                _pickerState.update { onboardingData.pickerStateData }
//            }
//        }
    }

    fun toggleDarkMode() {
        viewModelScope.launch {
            prefRepo.setDarkMode(!isDarkMode.first())
        }
    }

    fun toggleReminder() {
        viewModelScope.launch {
            prefRepo.setReminder(!isReminding.first())
        }
    }

    fun toggleGender() {
        viewModelScope.launch {
            val isMale: Boolean = onboardingData.value?.isMale ?: false
            prefRepo.setOnboardingData(onboardingData.value?.copy(isMale = !isMale))
        }
    }

    fun toggleUnitSystem() {
        viewModelScope.launch {
            if (onboardingData.value?.unitSystem == UnitSystem.METRIC)
                prefRepo.setOnboardingData(onboardingData.value?.copy(unitSystem = UnitSystem.IMPERIAL))
            else
                prefRepo.setOnboardingData(onboardingData.value?.copy(unitSystem = UnitSystem.METRIC))
        }
    }

//    private fun convertImperialToMetric(
//        field: SettingsField,
//        firstPickerValue: Int,
//        secondPickerValue: Int
//    ): Double {
//        return when (field) {
//            SettingsField.HEIGHT -> UnitConverter.convertFeetInchesToCm(
//                firstPickerValue,
//                secondPickerValue
//            )
//
//            SettingsField.WEIGHT -> UnitConverter.convertLbToKg(firstPickerValue)
//            SettingsField.AGE -> firstPickerValue.toDouble()
//            SettingsField.WEIGHT_GOAL -> TODO()
//        }
//    }

    fun savePickerChange(fieldName: SettingsField, firstValue: Int, secondValue: Int) {
        when (fieldName) {
            SettingsField.AGE -> onAgeChanged(firstValue)
            SettingsField.HEIGHT,
            SettingsField.WEIGHT -> onHeightOrWeightChanged(fieldName, firstValue, secondValue)

            SettingsField.WEIGHT_GOAL -> {}// handled by setWeightGoal
        }
    }

    private fun onAgeChanged(firstValue: Int) {
        val current = onboardingData.value ?: return
        viewModelScope.launch {
            prefRepo.setOnboardingData(
                current.copy(
                    pickerState = current.pickerState.copy(age = firstValue)
                )
            )
        }
    }

    private fun onHeightOrWeightChanged(
        fieldName: SettingsField,
        firstValue: Int,
        secondValue: Int
    ) {
        val onboardingData = this@SettingsViewModel.onboardingData.value ?: return

        val updatedPickerState = if (unitSystem.value == UnitSystem.IMPERIAL) {
            when (fieldName) {
                SettingsField.HEIGHT -> onboardingData.pickerState.copy(
                    heightFt = firstValue,
                    heightIn = secondValue,
                    heightCm = UnitConverter.convertFeetInchesToCm(firstValue, secondValue)
                )

                SettingsField.WEIGHT -> onboardingData.pickerState.copy(
                    weightLb = firstValue,
                    weightKg = UnitConverter.convertLbToKg(firstValue)
                )

                else -> return
            }
        } else {
            when (fieldName) {
                SettingsField.HEIGHT -> onboardingData.pickerState.copy(
                    heightCm = firstValue,
                    heightFt = UnitConverter.convertCmToFeetInches(firstValue).first,
                    heightIn = UnitConverter.convertCmToFeetInches(firstValue).second
                )

                SettingsField.WEIGHT ->
                    onboardingData.pickerState.copy(
                        weightKg = UnitConverter.toDecimal(firstValue, secondValue),
                        weightLb = UnitConverter.convertKgToLb(
                            UnitConverter.toDecimal(
                                firstValue,
                                secondValue
                            )
                        )
                    )

                else -> return
            }
        }

        viewModelScope.launch {
            prefRepo.setOnboardingData(onboardingData.copy(pickerState = updatedPickerState))
        }
    }

    fun setWeightGoal(goal: WeightGoal) {
        val current = onboardingData.value ?: return
        val newLimit = calculateDailyCalorieLimit(
            isMale = current.isMale,
            age = current.pickerState.age,
            weight = current.pickerState.weightKg,
            height = current.pickerState.heightCm,
            weightGoal = goal
        )
        viewModelScope.launch {
            prefRepo.setOnboardingData(
                current.copy(
                    weightGoal = goal,
                    dailyCalorieLimit = newLimit
                )
            )
        }
    }

    fun deleteAllData() {
        viewModelScope.launch {
            prefRepo.clearAll()
        }
    }

    fun getWeeklyWeightGoalDisplayValue(): Double {
        return if (unitSystem.value == UnitSystem.METRIC) {
            weightGoal.kgPerWeek
        } else {
            weightGoal.kgPerWeek.times(2.20462)
        }
    }
}

enum class SettingsField { AGE, HEIGHT, WEIGHT, WEIGHT_GOAL }