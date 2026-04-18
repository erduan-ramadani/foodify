package com.ercoding.foodify.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ercoding.foodify.domain.PreferencesInterface
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class SettingsViewModel(private val prefRepo: PreferencesInterface) : ViewModel() {

    val isDarkMode: Flow<Boolean> = prefRepo.darkMode
    val dailyThreshold: Flow<Int> = prefRepo.dailyThreshold

    fun toggleDarkMode() {
        viewModelScope.launch {
            prefRepo.setDarkMode(!isDarkMode.first())
        }
    }

    fun setDailyThreshold(threshold: Int) {
        viewModelScope.launch {
            prefRepo.setDailyThreshold(threshold)
        }
    }

}