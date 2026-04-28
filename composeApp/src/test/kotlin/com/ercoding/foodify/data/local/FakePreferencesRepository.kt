package com.ercoding.foodify.data.local

import com.ercoding.foodify.domain.PreferencesInterface
import com.ercoding.foodify.domain.model.onboarding.OnboardingData
import com.ercoding.foodify.domain.model.sheet.NutritionEntry
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class FakePreferencesRepository(
    override val reminder: Flow<Boolean>,
    override val onboardingData: Flow<OnboardingData?>
) : PreferencesInterface {
    override val darkMode: Flow<Boolean>
        get() = flowOf(false)
    override val nutritionEntries: Flow<String?>
        get() = flowOf(null)

    override suspend fun setDarkMode(enabled: Boolean) {}
    override suspend fun setReminder(enabled: Boolean) {
        TODO("Not yet implemented")
    }

    override suspend fun setOnboardingData(onboardingData: OnboardingData?) {
        TODO("Not yet implemented")
    }

    override suspend fun setNutritionEntries(entries: List<NutritionEntry>) {}

    override suspend fun getNutritionEntries(): List<NutritionEntry> {
        return listOf()
    }
}