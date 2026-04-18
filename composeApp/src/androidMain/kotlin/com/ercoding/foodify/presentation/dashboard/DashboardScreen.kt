package com.ercoding.foodify.presentation.dashboard

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.AssistChip
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.koinViewModel
import java.time.LocalDate


@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    onSettingsClick: () -> Unit
) {
    val vm: DashboardViewModel = koinViewModel()
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
        topBar = { FoodifyTopAppBar(onSettingsClick) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp, 0.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                FilledTonalIconButton(
                    onClick = {
                        vm.selectedDate = vm.selectedDate?.minusDays(1)
                    }
                ) {
                    Icon(
                        Icons.Default.ChevronLeft,
                        "Zurück"
                    )
                }
                AssistChip(
                    onClick = {
                        vm.selectedDate = LocalDate.now()
                    },
                    label = {
                        Text("${vm.selectedDate}")
                    },
                    leadingIcon = {
                        Icon(
                            Icons.Default.DateRange,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                )
                FilledTonalIconButton(
                    onClick = {
                        vm.selectedDate = vm.selectedDate?.plusDays(1)
                    },
                    enabled = vm.selectedDate != LocalDate.now()
                ) {
                    Icon(
                        Icons.Default.ChevronRight,
                        "Vor"
                    )
                }
            }
            DailyKcalSummary(vm)
            Spacer(modifier = Modifier.padding(8.dp))
            NutritionCards(vm)
            Spacer(modifier = Modifier.padding(8.dp))
            MealInputSection(
                onClick = { query ->
                    vm.addNutritionValues(query, vm.selectedDate)
                },
                isLoading = vm.isLoading
            )
            Spacer(modifier = Modifier.padding(8.dp))
            DailyMealsList(
                dailyEntries = dailyEntries,
                onDismiss = { entry -> vm.removeNutritionEntry(entry) },
            )
        }
    }
}