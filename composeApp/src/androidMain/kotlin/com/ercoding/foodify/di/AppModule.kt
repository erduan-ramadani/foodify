package com.ercoding.foodify.di

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.ercoding.foodify.data.local.PreferencesRepository
import com.ercoding.foodify.data.remote.anthropic.AnthropicRepository
import com.ercoding.foodify.data.remote.firebase.FirebaseRepository
import com.ercoding.foodify.domain.AnthropicInterface
import com.ercoding.foodify.domain.PreferencesInterface
import com.ercoding.foodify.presentation.MainViewModel
import com.ercoding.foodify.presentation.dashboard.DashboardViewModel
import com.ercoding.foodify.presentation.dashboard.analysistab.AnalysisViewModel
import com.ercoding.foodify.presentation.onboarding.OnboardingViewModel
import com.ercoding.foodify.presentation.settings.SettingsViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
val appModule = module {
    single { CoroutineScope(SupervisorJob() + Dispatchers.Default) }
    single { androidContext().dataStore }
    single<PreferencesInterface> { PreferencesRepository(get()) }
    single { FirebaseRepository() }
    single<AnthropicInterface> { AnthropicRepository(get()) }
}

@RequiresApi(Build.VERSION_CODES.O)
val viewModelModule = module {
    viewModel { MainViewModel(get()) }
    viewModel { OnboardingViewModel() }
    viewModel { DashboardViewModel(get(), get()) }
    viewModel { SettingsViewModel(get()) }
    viewModel { AnalysisViewModel() }
}