package com.ercoding.foodify.presentation.dashboard

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ercoding.foodify.presentation.dashboard.analysistab.AnalysisScreen
import com.ercoding.foodify.presentation.dashboard.analysistab.AnalysisViewModel
import com.ercoding.foodify.presentation.dashboard.daytab.components.DailyKcalSummary
import com.ercoding.foodify.presentation.dashboard.daytab.components.FoodifyTopAppBar
import com.ercoding.foodify.presentation.dashboard.daytab.components.MealEntryItem
import com.ercoding.foodify.presentation.dashboard.daytab.components.MealInputSection
import com.ercoding.foodify.presentation.dashboard.daytab.components.NutritionCards
import org.koin.androidx.compose.koinViewModel


@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    onSettingsClick: () -> Unit
) {
    val vm: DashboardViewModel = koinViewModel()
    val vmAnalysis: AnalysisViewModel = koinViewModel()
    val snackbarHostState = remember { SnackbarHostState() }
    val dailyEntries = vm.nutritionEntriesByDate[vm.selectedDate] ?: emptyList()

    LaunchedEffect(Unit) {
        vm.events.collect { apiResponse ->
            snackbarHostState.showSnackbar(
                message = apiResponse,
                duration = SnackbarDuration.Long
            )
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = { FoodifyTopAppBar(onSettingsClick, vm) }
    ) { paddingValues ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            item {
                SegmentedTabs(
                    selectedTab = vm.selectedTab,
                    onTabSelected = { vm.selectedTab = it }
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            if (vm.selectedTab == 0) {
                // DayTab
                item {
                    DailyKcalSummary(vm)
                    Spacer(modifier = Modifier.height(4.dp))
                }

                item {
                    NutritionCards(vm)
                    Spacer(modifier = Modifier.height(4.dp))
                }

                item {
                    MealInputSection(
                        recentEntries = vm.recentEntries,
                        onButtonClick = { vm.requestNutritionValues(it) },
                        onSuggestionChipClick = { vm.addNutritionFromSuggestion(it) },
                        isLoading = vm.isLoading
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                }

                items(
                    dailyEntries,
                    key = { it.id }
                ) { entry ->
                    MealEntryItem(
                        entry,
                        onDismiss = { vm.removeNutritionEntry(entry) }
                    )
                }
            } else {
                // AnalaysisTab
                item {
                    AnalysisScreen()
                    Spacer(modifier = Modifier.height(4.dp))
                }
//                item {
//                    HeroCard(4.0, 2130)
//                    Spacer(modifier = Modifier.height(4.dp))
//                }
//                item {
//                    WeeklyBarChart(vmAnalysis.weekData, 5)
//                    Spacer(modifier = Modifier.height(4.dp))
//                }
//                item {
//                    StatCard(
//                        modifier = Modifier.
//                        "gegessen",
//                        2136,
//                        "Kg",
//                        Color.Red,
//                        "\uD83C\uDF7D\uFE0F"
//                    )
//                    Spacer(modifier = Modifier.height(4.dp))
//                }
            }
        }
    }
}