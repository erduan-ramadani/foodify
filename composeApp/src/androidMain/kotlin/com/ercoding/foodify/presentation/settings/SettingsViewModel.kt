package com.ercoding.foodify.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ercoding.foodify.domain.PreferencesInterface
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

    fun setDailyCalorieLimit(limit: Int) {
        viewModelScope.launch {
            prefRepo.setOnboardingData(onboardingData.value?.copy(dailyCalorieLimit = limit))
        }
    }

    fun saveSettingsBottomSheetChange(fieldName: Settingsfield, value: Int) {
        val updated = when (fieldName) {
            Settingsfield.AGE -> onboardingData.value?.copy(age = value)
            Settingsfield.HEIGHT -> onboardingData.value?.copy(height = value)
            Settingsfield.WEIGHT -> onboardingData.value?.copy(weight = value)
            Settingsfield.WEIGHT_GOAL -> onboardingData.value?.copy(weightGoal = value)
            Settingsfield.DAILY_CALORIE_LIMIT -> onboardingData.value?.copy(dailyCalorieLimit = value)
        }
        updated?.let {
            viewModelScope.launch { prefRepo.setOnboardingData(it) }
        }
    }
}

enum class Settingsfield { AGE, HEIGHT, WEIGHT, WEIGHT_GOAL, DAILY_CALORIE_LIMIT }