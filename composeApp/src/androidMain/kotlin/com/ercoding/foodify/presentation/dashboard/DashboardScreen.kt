package com.ercoding.foodify.presentation.dashboard

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ercoding.foodify.presentation.dashboard.analysistab.AnalysisScreen
import com.ercoding.foodify.presentation.dashboard.daytab.DayScreen
import com.ercoding.foodify.presentation.dashboard.daytab.components.FoodifyTopAppBar
import org.koin.androidx.compose.koinViewModel

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    onSettingsClick: () -> Unit
) {
    val vm: DashboardViewModel = koinViewModel()
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = { FoodifyTopAppBar(onSettingsClick, vm) }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            SegmentedTabs(
                selectedTab = vm.selectedTab,
                onTabSelected = { vm.selectedTab = it }
            )
            Spacer(modifier = Modifier.height(12.dp))

            if (vm.selectedTab == 0) {
                DayScreen(snackbarHostState)
            } else {
                AnalysisScreen()
            }
        }
    }
}