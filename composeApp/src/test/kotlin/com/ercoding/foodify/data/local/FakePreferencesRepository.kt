package com.ercoding.foodify.data.local

import com.ercoding.foodify.domain.PreferencesInterface
import com.ercoding.foodify.domain.ProteinEntry
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class FakePreferencesRepository() : PreferencesInterface {
    override val darkMode: Flow<Boolean>
        get() = flowOf(false)
    override val nutritionEntries: Flow<String?>
        get() = flowOf(null)

    override suspend fun setDarkMode(enabled: Boolean) {}

    override suspend fun setNutritionEntries(entries: List<ProteinEntry>) {}

    override suspend fun getNutritionEntries(): List<ProteinEntry> {
        return listOf()
    }
}