package com.ercoding.foodify.presentation

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ercoding.foodify.domain.PreferencesInterface
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel(prefRepo: PreferencesInterface) : ViewModel() {

    val isDarkMode =
        prefRepo.darkMode.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            false
        )

    val isLoading = mutableStateOf(true)

    init {
        viewModelScope.launch {
            isLoading.value = false
        }
    }
}