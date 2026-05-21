package com.eddiapps.foodify.presentation.dashboard

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.eddiapps.foodify.R
import com.eddiapps.foodify.presentation.dashboard.analysistab.AnalysisScreen
import com.eddiapps.foodify.presentation.dashboard.daytab.DayScreen
import com.eddiapps.foodify.presentation.dashboard.daytab.components.FoodifyTopAppBar
import org.koin.androidx.compose.koinViewModel

@SuppressLint("LocalContextGetResourceValueCall")
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DashboardScreen(
    onSettingsClick: () -> Unit
) {
    val vm: DashboardViewModel = koinViewModel()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                vm.onResume()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    LaunchedEffect(Unit) {
        vm.connectionEvents.collect { event ->
            val message = when (event) {
                UiConnectionEvent.NoInternet -> context.getString(R.string.error_no_internet)
                UiConnectionEvent.Timeout -> context.getString(R.string.error_timeout)
                UiConnectionEvent.UnknownError -> context.getString(R.string.error_unknown)
            }
            snackbarHostState.showSnackbar(message, duration = SnackbarDuration.Long)
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = { FoodifyTopAppBar(onSettingsClick, vm) },
        bottomBar = {
            FoodifyBottomBar(
                onButtonClick = { vm.requestNutritionValues(it) },
                isLoading = vm.isLoading
            )
        }
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