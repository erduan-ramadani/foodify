package com.eddiapps.foodify.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.eddiapps.foodify.domain.PreferencesInterface
import com.eddiapps.foodify.domain.model.onboarding.OnboardingData
import com.eddiapps.foodify.domain.model.sheet.NutritionEntry
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json

class PreferencesRepository(private val dataStore: DataStore<Preferences>) :
    PreferencesInterface {
    companion object {
        val DARK_MODE_KEY = booleanPreferencesKey("dark_mode")
        val REMINDER_KEY = booleanPreferencesKey("reminder")
        val ONBOARDING_KEY = stringPreferencesKey("onboarded")
        val NUTRITION_ENTRIES = stringPreferencesKey("nutrition_entries")
    }

    override val darkMode: Flow<Boolean> = dataStore.data.map { it[DARK_MODE_KEY] ?: false }
    override val reminder: Flow<Boolean> = dataStore.data.map { it[REMINDER_KEY] ?: true }
    override val nutritionEntries: Flow<String?> = dataStore.data.map { it[NUTRITION_ENTRIES] }
    override val onboardingData: Flow<OnboardingData?> = dataStore.data
        .map { prefs ->
            prefs[ONBOARDING_KEY]?.let {
                Json.decodeFromString<OnboardingData>(it)
            }
        }

    override suspend fun setDarkMode(enabled: Boolean) {
        dataStore.edit { it[DARK_MODE_KEY] = enabled }
    }

    override suspend fun setReminder(enabled: Boolean) {
        dataStore.edit { it[REMINDER_KEY] = enabled }
    }

    override suspend fun setNutritionEntries(entries: List<NutritionEntry>) {
        val encodedEntries = Json.encodeToString(entries)
        dataStore.edit { it[NUTRITION_ENTRIES] = encodedEntries }
    }

    override suspend fun getNutritionEntries(): List<NutritionEntry> {
        val json = nutritionEntries.first() ?: return emptyList()
        return Json.decodeFromString<List<NutritionEntry>>(json)
    }

    override suspend fun clearAll() {
        dataStore.edit { it.clear() }
    }

    override suspend fun setOnboardingData(onboardingData: OnboardingData?) {
        val encodedOnboardingData = Json.encodeToString(onboardingData)
        dataStore.edit { it[ONBOARDING_KEY] = encodedOnboardingData }
    }
}