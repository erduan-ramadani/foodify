package com.ercoding.foodify.data.local

import com.ercoding.foodify.domain.NutritionEntry
import com.ercoding.foodify.domain.PreferencesInterface
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class FakePreferencesRepository() : PreferencesInterface {
    override val darkMode: Flow<Boolean>
        get() = flowOf(false)
    override val nutritionEntries: Flow<String?>
        get() = flowOf(null)

    override suspend fun setDarkMode(enabled: Boolean) {}

    override suspend fun setNutritionEntries(entries: List<NutritionEntry>) {}

    override suspend fun getNutritionEntries(): List<NutritionEntry> {
        return listOf()
    }
}