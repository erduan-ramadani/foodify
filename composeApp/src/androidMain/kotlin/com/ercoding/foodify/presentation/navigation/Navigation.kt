package com.ercoding.foodify.presentation.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.ercoding.foodify.presentation.MainViewModel
import com.ercoding.foodify.presentation.StartState
import com.ercoding.foodify.presentation.dashboard.DashboardScreen
import com.ercoding.foodify.presentation.onboarding.OnboardingScreen
import com.ercoding.foodify.presentation.settings.SettingsScreen
import com.ercoding.foodify.presentation.theme.FoodifyTheme
import com.ercoding.foodify.presentation.util.rememberReminderScheduler
import org.koin.androidx.compose.koinViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Navigation() {

    val mainViewModel: MainViewModel = koinViewModel()
    val navController = rememberNavController()
    val startState by mainViewModel.startState.collectAsState()
    val isDarkMode by mainViewModel.isDarkMode.collectAsState()

    FoodifyTheme(darkTheme = isDarkMode) {
        when (startState) {
            StartState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(modifier = Modifier.size(100.dp))
                }
            }

            else -> {
                val startDestination = if (startState == StartState.NeedsOnboarding)
                    Routes.onboarding else Routes.dashboard

                NavHost(navController, startDestination) {
                    composable(Routes.onboarding) {
                        val scheduleReminder = rememberReminderScheduler()

                        OnboardingScreen(
                            onComplete = { onboardingData ->
                                mainViewModel.saveOnboardingData(onboardingData)
                                scheduleReminder()
                                navController.navigate(Routes.dashboard) {
                                    popUpTo(Routes.onboarding) { inclusive = true }
                                }
                            }
                        )
                    }
                    composable(Routes.dashboard) {
                        DashboardScreen(
                            onSettingsClick = { navController.navigate(Routes.settings) }
                        )
                    }
                    composable(Routes.settings) {
                        SettingsScreen(
                            onBackClick = { navController.popBackStack() },
                            onDeleteAllData = {
                                navController.navigate(Routes.onboarding) {
                                    popUpTo(0) { inclusive = true }
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}