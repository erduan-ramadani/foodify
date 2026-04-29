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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.ercoding.foodify.data.local.Scheduling
import com.ercoding.foodify.presentation.MainViewModel
import com.ercoding.foodify.presentation.dashboard.DashboardScreen
import com.ercoding.foodify.presentation.onboarding.OnboardingScreen
import com.ercoding.foodify.presentation.settings.SettingsScreen
import com.ercoding.foodify.presentation.theme.FoodifyTheme
import org.koin.androidx.compose.koinViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Navigation() {

    val mainViewModel: MainViewModel = koinViewModel()
    val navController = rememberNavController()
    var startDestination: String
    val isDarkMode by mainViewModel.isDarkMode.collectAsState()
    val onboardingData by mainViewModel.onboardingData.collectAsState()

    FoodifyTheme(darkTheme = isDarkMode) {
        startDestination =
            onboardingData?.let { Routes.dashboard } ?: Routes.onboarding
        if (mainViewModel.isLoading.value) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(100.dp),
                )
            }
        } else {
            NavHost(navController, startDestination) {
                composable(Routes.onboarding) {
                    val context = LocalContext.current
                    OnboardingScreen(
                        onComplete = { onboardingData ->
                            mainViewModel.saveOnboardingData(onboardingData)
                            Scheduling(context).schedule()
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