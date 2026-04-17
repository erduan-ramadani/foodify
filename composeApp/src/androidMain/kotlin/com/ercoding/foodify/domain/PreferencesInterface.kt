package com.ercoding.foodify.domain

import kotlinx.coroutines.flow.Flow

interface PreferencesInterface {
    val darkMode: Flow<Boolean>
    val proteinGoal: Flow<Int?>
    val dailyReached: Flow<Int?>
    val proteinEntries: Flow<String?>
    suspend fun setDarkMode(enabled: Boolean)
    suspend fun setProteinGoal(proteinGoal: Int)
    suspend fun setDailyReached(dailyReached: Int)
    suspend fun setProteinEntries(entries: List<ProteinEntry>)
    suspend fun getProteinEntries(): List<ProteinEntry>
}