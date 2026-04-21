package com.ercoding.foodify.domain

import com.ercoding.foodify.domain.model.sheet.NutritionEntry
import kotlinx.coroutines.flow.Flow

interface PreferencesInterface {
    val darkMode: Flow<Boolean>
    val dailyThreshold: Flow<Int>
    val nutritionEntries: Flow<String?>
    suspend fun setDarkMode(enabled: Boolean)
    suspend fun setNutritionEntries(entries: List<NutritionEntry>)
    suspend fun getNutritionEntries(): List<NutritionEntry>
    suspend fun setDailyThreshold(threshold: Int)
}