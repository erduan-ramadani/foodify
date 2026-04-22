package com.ercoding.foodify.domain

import com.ercoding.foodify.domain.model.onboarding.OnboardingData
import com.ercoding.foodify.domain.model.sheet.NutritionEntry
import kotlinx.coroutines.flow.Flow

interface PreferencesInterface {
    val darkMode: Flow<Boolean>
    val nutritionEntries: Flow<String?>
    val onboardingData: Flow<OnboardingData?>
    suspend fun setDarkMode(enabled: Boolean)
    suspend fun setOnboardingData(onboardingData: OnboardingData?)
    suspend fun setNutritionEntries(entries: List<NutritionEntry>)
    suspend fun getNutritionEntries(): List<NutritionEntry>
}