package com.ercoding.foodify.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
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
        val PROTEIN_GOAL = intPreferencesKey("protein_goal")
        val DAILY_REACHED = intPreferencesKey("daily_reached")
        val PROTEIN_ENTRIES = stringPreferencesKey("protein_entries")
    }

    override val darkMode: Flow<Boolean> = dataStore.data.map { it[DARK_MODE_KEY] ?: false }
    override val proteinGoal: Flow<Int?> = dataStore.data.map { it[PROTEIN_GOAL] }
    override val dailyReached: Flow<Int?> = dataStore.data.map { it[DAILY_REACHED] }
    override val proteinEntries: Flow<String?> = dataStore.data.map { it[PROTEIN_ENTRIES] }

    override suspend fun setDarkMode(enabled: Boolean) {
        dataStore.edit { it[DARK_MODE_KEY] = enabled }
    }

    override suspend fun setProteinGoal(proteinGoal: Int) {
        dataStore.edit { it[PROTEIN_GOAL] = proteinGoal }
    }

    override suspend fun setDailyReached(dailyReached: Int) {
        dataStore.edit { it[DAILY_REACHED] = dailyReached }
    }

    override suspend fun setProteinEntries(entries: List<ProteinEntry>) {
        val encodedEntries = Json.encodeToString(entries)
        dataStore.edit { it[PROTEIN_ENTRIES] = encodedEntries }
    }

    override suspend fun getProteinEntries(): List<ProteinEntry> {
        val json = proteinEntries.first() ?: return emptyList()
        return Json.decodeFromString<List<ProteinEntry>>(json)
    }
}