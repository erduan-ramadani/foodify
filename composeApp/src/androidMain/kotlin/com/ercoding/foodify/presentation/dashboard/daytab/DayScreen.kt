package com.ercoding.foodify.presentation.dashboard.daytab

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ercoding.foodify.presentation.dashboard.DashboardViewModel
import com.ercoding.foodify.presentation.dashboard.daytab.components.BalanceCard
import com.ercoding.foodify.presentation.dashboard.daytab.components.CalorieRing
import com.ercoding.foodify.presentation.dashboard.daytab.components.EntriesCard
import com.ercoding.foodify.presentation.dashboard.daytab.components.RecentSuggestions
import org.koin.androidx.compose.koinViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DayScreen(
    snackbarHostState: SnackbarHostState,
) {
    val vm: DashboardViewModel = koinViewModel()
    val dailyEntries =
        vm.nutritionEntriesByDate[vm.selectedDate] ?: emptyList()
    val listState = rememberLazyListState()


    LaunchedEffect(Unit) {
        vm.events.collect { apiResponse ->
            snackbarHostState.showSnackbar(
                message = apiResponse,
                duration = SnackbarDuration.Long
            )
        }
    }

    LaunchedEffect(dailyEntries.size) {
        if (dailyEntries.isNotEmpty()) {
            listState.animateScrollToItem(listState.layoutInfo.totalItemsCount - 1)
        }
    }

    LazyColumn(
        state = listState,
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            CalorieRing(vm)
        }

        item {
            BalanceCard(vm)
            Spacer(modifier = Modifier.height(8.dp))
        }

        item {
            RecentSuggestions(vm.recentEntries) { entry ->
                vm.addNutritionFromSuggestion(entry)
            }
            Spacer(modifier = Modifier.height(4.dp))
        }

        item {
            EntriesCard(
                entries = dailyEntries,
                viewModel = vm
            )
        }
    }
}