package com.eddiapps.foodify.di

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.eddiapps.foodify.data.local.NutritionRepository
import com.eddiapps.foodify.data.local.PreferencesRepository
import com.eddiapps.foodify.data.remote.anthropic.AnthropicRepository
import com.eddiapps.foodify.data.remote.firebase.FirebaseRepository
import com.eddiapps.foodify.domain.AnthropicInterface
import com.eddiapps.foodify.domain.PreferencesInterface
import com.eddiapps.foodify.domain.model.NutritionInterface
import com.eddiapps.foodify.presentation.MainViewModel
import com.eddiapps.foodify.presentation.dashboard.analysistab.AnalysisViewModel
import com.eddiapps.foodify.presentation.dashboard.daytab.DayViewModel
import com.eddiapps.foodify.presentation.onboarding.OnboardingViewModel
import com.eddiapps.foodify.presentation.settings.SettingsViewModel
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
    single<NutritionInterface> { NutritionRepository(get(), get()) }
    single { FirebaseRepository() }
    single<AnthropicInterface> { AnthropicRepository(get()) }
}

@RequiresApi(Build.VERSION_CODES.O)
val viewModelModule = module {
    viewModel { MainViewModel(get()) }
    viewModel { OnboardingViewModel() }
    viewModel { DayViewModel(get(), get(), get()) }
    viewModel { AnalysisViewModel(get(), get()) }
    viewModel { SettingsViewModel(get()) }
}