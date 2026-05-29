package com.eddiapps.foodify.presentation.dashboard

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.eddiapps.foodify.presentation.dashboard.analysistab.AnalysisScreen
import com.eddiapps.foodify.presentation.dashboard.daytab.DayScreen
import com.eddiapps.foodify.presentation.dashboard.daytab.components.FoodifyTopAppBar

@SuppressLint("LocalContextGetResourceValueCall")
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DashboardScreen(
    onSettingsClick: () -> Unit
) {
    var selectedTab by rememberSaveable { mutableIntStateOf(0) }
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            FoodifyTopAppBar(
                onSettingsClick,
                selectedTab,
                onTabSelected = { selectedTab = it }
            )
        },
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            if (selectedTab == 0) {
                DayScreen(snackbarHostState)
            } else {
                AnalysisScreen()
            }
        }
    }
}