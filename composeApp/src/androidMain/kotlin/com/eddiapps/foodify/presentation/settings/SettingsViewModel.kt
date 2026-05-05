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

    fun saveSettingsBottomSheetChange(fieldName: Settingsfield, value: Int) {
        val updated = when (fieldName) {
            Settingsfield.AGE -> onboardingData.value?.copy(age = value)
            Settingsfield.HEIGHT -> onboardingData.value?.copy(height = value)
            Settingsfield.WEIGHT -> onboardingData.value?.copy(weight = value)
            Settingsfield.WEIGHT_GOAL -> null // wird über setWeightGoal gehandelt
            Settingsfield.DAILY_CALORIE_LIMIT -> onboardingData.value?.copy(dailyCalorieLimit = value)
        }
        updated?.let {
            viewModelScope.launch { prefRepo.setOnboardingData(it) }
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