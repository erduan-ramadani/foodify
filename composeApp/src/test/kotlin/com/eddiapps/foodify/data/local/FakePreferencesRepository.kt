package com.eddiapps.foodify.data.local

import com.eddiapps.foodify.domain.PreferencesInterface
import com.eddiapps.foodify.domain.model.onboarding.OnboardingData
import com.eddiapps.foodify.domain.model.sheet.NutritionEntry
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class FakePreferencesRepository(
    initialOnboardingData: OnboardingData? = null,
    initialReminder: Boolean = false,
    initialDarkMode: Boolean = false,
) : PreferencesInterface {

    private val _darkMode = MutableStateFlow(initialDarkMode)
    override val darkMode: Flow<Boolean> = _darkMode

    private val _reminder = MutableStateFlow(initialReminder)
    override val reminder: Flow<Boolean> = _reminder

    private val _onboardingData = MutableStateFlow(initialOnboardingData)
    override val onboardingData: Flow<OnboardingData?> = _onboardingData

    private val _nutritionEntries = MutableStateFlow<String?>(null)
    override val nutritionEntries: Flow<String?> = _nutritionEntries

    var storedEntries: List<NutritionEntry> = emptyList()
        private set

    var clearAllCalled = false
        private set

    override suspend fun setDarkMode(enabled: Boolean) {
        _darkMode.value = enabled
    }

    override suspend fun setReminder(enabled: Boolean) {
        _reminder.value = enabled
    }

    override suspend fun setOnboardingData(onboardingData: OnboardingData?) {
        _onboardingData.value = onboardingData
    }

    override suspend fun setNutritionEntries(entries: List<NutritionEntry>) {
        storedEntries = entries
    }

    override suspend fun getNutritionEntries(): List<NutritionEntry> = storedEntries

    override suspend fun clearAll() {
        storedEntries = emptyList()
        _onboardingData.value = null
        clearAllCalled = true
    }
}