package com.eddiapps.foodify.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eddiapps.foodify.domain.PreferencesInterface
import com.eddiapps.foodify.domain.model.onboarding.OnboardingData
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed class StartState {
    data object Loading : StartState()
    data object NeedsOnboarding : StartState()
    data object Ready : StartState()
}

class MainViewModel(private val prefRepo: PreferencesInterface) : ViewModel() {

    val isDarkMode = prefRepo.darkMode.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        false
    )

    val startState = prefRepo.onboardingData
        .map { if (it == null) StartState.NeedsOnboarding else StartState.Ready }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            StartState.Loading
        )

    fun saveOnboardingData(onboardingData: OnboardingData) {
        viewModelScope.launch {
            prefRepo.setOnboardingData(onboardingData)
        }
    }
}