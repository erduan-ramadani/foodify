package com.ercoding.foodify.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ercoding.foodify.domain.PreferencesInterface
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class SettingsViewModel(private val prefRepo: PreferencesInterface) : ViewModel() {

    val proteinGoal: Flow<Int?> = prefRepo.proteinGoal
    val isDarkMode: Flow<Boolean> = prefRepo.darkMode

    fun toggleDarkMode() {
        viewModelScope.launch {
            prefRepo.setDarkMode(!isDarkMode.first())
        }
    }

    fun setProteinGoal(goal: Int) {
        viewModelScope.launch {
            prefRepo.setProteinGoal(goal)
        }
    }
}