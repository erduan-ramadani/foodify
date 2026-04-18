package com.ercoding.foodify.presentation.dashboard

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ercoding.foodify.domain.NutritionEntry

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DailyMealsList(
    dailyEntries: List<NutritionEntry>,
    onDismiss: (NutritionEntry) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
    ) {
        Spacer(modifier = Modifier.padding(3.dp))
        val listState = rememberLazyListState()
        LazyColumn(
            state = listState,
            modifier = Modifier.weight(1f)
        ) {
            items(dailyEntries.size, key = { dailyEntries[it].id }) { index ->
                val entry = dailyEntries[index]
                MealEntryItem(
                    entry,
                    onDismiss = { onDismiss(entry) }
                )
            }
        }
    }
}