package com.eddiapps.foodify.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eddiapps.foodify.domain.PreferencesInterface
import com.eddiapps.foodify.domain.calculation.calculateDailyCalorieLimit
import com.eddiapps.foodify.domain.model.onboarding.WeightGoal
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(private val prefRepo: PreferencesInterface) : ViewModel() {

    val isDarkMode: Flow<Boolean> = prefRepo.darkMode
    val isReminding: Flow<Boolean> = prefRepo.reminder
    val onboardingData = prefRepo.onboardingData.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        null
    )

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
                    pickerStateData = current.pickerStateData.copy(age = firstValue)
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
                SettingsField.HEIGHT -> onboardingData.pickerStateData.copy(
                    heightFt = firstValue,
                    heightIn = secondValue,
                    heightCm = UnitConverter.convertFeetInchesToCm(firstValue, secondValue)
                )

                SettingsField.WEIGHT -> onboardingData.pickerStateData.copy(
                    weightLb = firstValue,
                    weightKg = UnitConverter.convertLbToKg(firstValue)
                )

                else -> return
            }
        } else {
            when (fieldName) {
                SettingsField.HEIGHT -> onboardingData.pickerStateData.copy(
                    heightCm = firstValue,
                    heightFt = UnitConverter.convertCmToFeetInches(firstValue).first,
                    heightIn = UnitConverter.convertCmToFeetInches(firstValue).second
                )

                SettingsField.WEIGHT ->
                    onboardingData.pickerStateData.copy(
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
            prefRepo.setOnboardingData(onboardingData.copy(pickerStateData = updatedPickerState))
        }
    }

    fun setWeightGoal(goal: WeightGoal) {
        val current = onboardingData.value ?: return
        val newLimit = calculateDailyCalorieLimit(
            isMale = current.isMale,
            weight = current.weight,
            height = current.height,
            age = current.age,
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
}

enum class Settingsfield { AGE, HEIGHT, WEIGHT, WEIGHT_GOAL, DAILY_CALORIE_LIMIT }