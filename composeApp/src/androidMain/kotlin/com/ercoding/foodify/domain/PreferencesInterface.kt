package com.ercoding.foodify.domain

import kotlinx.coroutines.flow.Flow

interface PreferencesInterface {
    val darkMode: Flow<Boolean>
    val nutritionEntries: Flow<String?>
    suspend fun setDarkMode(enabled: Boolean)
    suspend fun setNutritionEntries(entries: List<NutritionEntry>)
    suspend fun getNutritionEntries(): List<NutritionEntry>
}