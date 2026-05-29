package com.eddiapps.foodify.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eddiapps.foodify.domain.PreferencesInterface
import com.eddiapps.foodify.domain.calculation.UnitConverter
import com.eddiapps.foodify.domain.calculation.calculateDailyCalorieLimit
import com.eddiapps.foodify.domain.model.NutritionInterface
import com.eddiapps.foodify.domain.model.UnitSystem
import com.eddiapps.foodify.domain.model.onboarding.OnboardingData
import com.eddiapps.foodify.domain.model.onboarding.WeightGoal
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Locale
import kotlin.math.roundToInt

class SettingsViewModel(
    private val preferencesRepository: PreferencesInterface,
    private val nutritionRepository: NutritionInterface
) : ViewModel() {

    val isDarkMode: Flow<Boolean> = preferencesRepository.darkMode
    val isReminding: Flow<Boolean> = preferencesRepository.reminder
    val onboardingData = preferencesRepository.onboardingData.stateIn(
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

    fun toggleDarkMode() {
        viewModelScope.launch {
            preferencesRepository.setDarkMode(!isDarkMode.first())
        }
    }

    fun toggleReminder() {
        viewModelScope.launch {
            preferencesRepository.setReminder(!isReminding.first())
        }
    }

    fun toggleGender() {
        val isMale: Boolean = onboardingData.value?.isMale ?: false
        updateLimit { it.copy(isMale = !isMale) }
    }

    fun toggleUnitSystem() {
        if (onboardingData.value?.unitSystem == UnitSystem.METRIC)
            updateLimit { it.copy(unitSystem = UnitSystem.IMPERIAL) }
        else
            updateLimit { it.copy(unitSystem = UnitSystem.METRIC) }
    }

    fun savePickerChange(fieldName: SettingsField, firstValue: Int, secondValue: Int) {
        when (fieldName) {
            SettingsField.AGE -> onAgeChanged(firstValue)
            SettingsField.HEIGHT,
            SettingsField.WEIGHT -> onHeightOrWeightChanged(fieldName, firstValue, secondValue)

            SettingsField.WEIGHT_GOAL -> {}// handled by setWeightGoal
        }
    }

    private fun onAgeChanged(firstValue: Int) {
        updateLimit { it.copy(pickerState = it.pickerState.copy(age = firstValue)) }
    }

    private fun onHeightOrWeightChanged(
        fieldName: SettingsField,
        firstValue: Int,
        secondValue: Int
    ) {
        if (unitSystem.value == UnitSystem.IMPERIAL) {
            when (fieldName) {
                SettingsField.HEIGHT ->
                    updateLimit {
                        it.copy(
                            pickerState = it.pickerState.copy(
                                heightFt = firstValue,
                                heightIn = secondValue,
                                heightCm = UnitConverter.convertFeetInchesToCm(
                                    firstValue,
                                    secondValue
                                )
                            )
                        )
                    }

                SettingsField.WEIGHT ->
                    updateLimit {
                        it.copy(
                            pickerState = it.pickerState.copy(
                                weightLb = firstValue,
                                weightKg = UnitConverter.convertLbToKg(firstValue)
                            )
                        )
                    }

                else -> return
            }
        } else {
            when (fieldName) {
                SettingsField.HEIGHT ->
                    updateLimit {
                        it.copy(
                            pickerState = it.pickerState.copy(
                                heightCm = firstValue,
                                heightFt = UnitConverter.convertCmToFeetInches(firstValue).first,
                                heightIn = UnitConverter.convertCmToFeetInches(firstValue).second
                            )
                        )
                    }

                SettingsField.WEIGHT ->
                    updateLimit {
                        it.copy(
                            pickerState = it.pickerState.copy(
                                weightKg = UnitConverter.toDecimal(firstValue, secondValue),
                                weightLb = UnitConverter.convertKgToLb(
                                    UnitConverter.toDecimal(
                                        firstValue,
                                        secondValue
                                    )
                                )
                            )
                        )
                    }

                else -> return
            }
        }
    }

    fun setWeightGoal(goal: WeightGoal) {
        updateLimit { it.copy(weightGoal = goal) }
    }

    private fun updateLimit(transform: (OnboardingData) -> OnboardingData) {
        val current = onboardingData.value ?: return
        val updated = transform(current)
        val newLimit = calculateDailyCalorieLimit(
            updated.isMale,
            updated.pickerState.age,
            updated.pickerState.weightKg,
            updated.pickerState.heightCm,
            updated.weightGoal ?: WeightGoal.NORMAL
        )
        viewModelScope.launch {
            preferencesRepository.setOnboardingData(
                updated.copy(dailyCalorieLimit = newLimit)
            )
        }
    }

    fun deleteAllData() {
        viewModelScope.launch {
            preferencesRepository.clearAll()
            nutritionRepository.clearAll()
        }
    }
}

enum class SettingsField { AGE, HEIGHT, WEIGHT, WEIGHT_GOAL }