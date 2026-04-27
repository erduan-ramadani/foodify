package com.ercoding.foodify.presentation.dashboard.daytab

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ercoding.foodify.presentation.dashboard.DashboardViewModel
import com.ercoding.foodify.presentation.dashboard.daytab.components.CalorieRing
import com.ercoding.foodify.presentation.dashboard.daytab.components.MealEntryItem
import com.ercoding.foodify.presentation.dashboard.daytab.components.NutritionCards
import org.koin.androidx.compose.koinViewModel

@Composable
fun DayScreen(
    snackbarHostState: SnackbarHostState,
) {
    val vm: DashboardViewModel = koinViewModel()
    val dailyEntries =
        vm.nutritionEntriesByDate[vm.selectedDate] ?: emptyList()

    LaunchedEffect(Unit) {
        vm.events.collect { apiResponse ->
            snackbarHostState.showSnackbar(
                message = apiResponse,
                duration = SnackbarDuration.Long
            )
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            CalorieRing(viewModel = vm)
            Spacer(modifier = Modifier.height(4.dp))
        }

        item {
            NutritionCards(vm)
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
    }
}