package com.ercoding.foodify.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.ercoding.foodify.domain.PreferencesInterface
import com.ercoding.foodify.domain.ProteinEntry
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json

class PreferencesRepository(private val dataStore: DataStore<Preferences>) :
    PreferencesInterface {
    companion object {
        val DARK_MODE_KEY = booleanPreferencesKey("dark_mode")
        val NUTRITION_ENTRIES = stringPreferencesKey("nutrition_entries")
    }

    override val darkMode: Flow<Boolean> = dataStore.data.map { it[DARK_MODE_KEY] ?: false }
    override val nutritionEntries: Flow<String?> = dataStore.data.map { it[NUTRITION_ENTRIES] }

    override suspend fun setDarkMode(enabled: Boolean) {
        dataStore.edit { it[DARK_MODE_KEY] = enabled }
    }

    override suspend fun setNutritionEntries(entries: List<ProteinEntry>) {
        val encodedEntries = Json.encodeToString(entries)
        dataStore.edit { it[NUTRITION_ENTRIES] = encodedEntries }
    }

    override suspend fun getNutritionEntries(): List<ProteinEntry> {
        val json = nutritionEntries.first() ?: return emptyList()
        return Json.decodeFromString<List<ProteinEntry>>(json)
    }
}