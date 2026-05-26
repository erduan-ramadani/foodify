package com.eddiapps.foodify.presentation.dashboard.daytab

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
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.unit.dp
import com.eddiapps.foodify.presentation.dashboard.DashboardViewModel
import com.eddiapps.foodify.presentation.dashboard.daytab.components.BalanceCard
import com.eddiapps.foodify.presentation.dashboard.daytab.components.CalorieRing
import com.eddiapps.foodify.presentation.dashboard.daytab.components.EntriesCard
import com.eddiapps.foodify.presentation.dashboard.daytab.components.WeekDaySelector
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
    val context = LocalResources.current


    LaunchedEffect(Unit) {
        vm.messageEvents.collect { apiResponse ->
            snackbarHostState.showSnackbar(
                message = context.getString(apiResponse),
                duration = SnackbarDuration.Long
            )
        }
    }

    LaunchedEffect(dailyEntries.size) {
        if (dailyEntries.isNotEmpty()) {
            listState.animateScrollToItem(
                index = listState.layoutInfo.totalItemsCount - 1,
                scrollOffset = Int.MAX_VALUE
            )
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
            WeekDaySelector(vm)
        }
        item {
            CalorieRing(vm)
        }

        item {
            BalanceCard(vm)
            Spacer(modifier = Modifier.height(8.dp))
        }

//        item {
//            RecentSuggestions(vm.recentEntries) { entry ->
//                vm.addNutritionFromSuggestion(entry)
//            }
//            Spacer(modifier = Modifier.height(4.dp))
//        }

        item {
            EntriesCard(
                entries = dailyEntries,
                viewModel = vm
            )
        }
    }
}