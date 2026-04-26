package com.ercoding.foodify.presentation

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ercoding.foodify.domain.PreferencesInterface
import com.ercoding.foodify.domain.model.onboarding.OnboardingData
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel(private val prefRepo: PreferencesInterface) : ViewModel() {

    val isDarkMode =
        prefRepo.darkMode.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            false
        )

    val onboardingData =
        prefRepo.onboardingData.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            null
        )

    val isLoading = mutableStateOf(true)

    init {
        viewModelScope.launch {
            // warten bis die Daten geladen haben, bevor der Ladekreis verschwindet
            prefRepo.onboardingData.first()
            isLoading.value = false
        }
    }

    fun saveOnboardingData(onboardingData: OnboardingData) {
        viewModelScope.launch {
            prefRepo.setOnboardingData(onboardingData)
        }
    }
}