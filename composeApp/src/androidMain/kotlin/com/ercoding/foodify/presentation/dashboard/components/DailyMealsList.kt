package com.ercoding.foodify.presentation.dashboard.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ercoding.foodify.domain.model.sheet.NutritionEntry

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DailyMealsList(
    dailyEntries: List<NutritionEntry>,
    onDismiss: (NutritionEntry) -> Unit
) {
    val listState = rememberLazyListState()

    Column(
        modifier = Modifier.fillMaxSize(),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Einträge",
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.weight(1f))
            ColorLegend(
                "Carbs",
                textProtein = "Protein",
                textFat = "Fat",
                textSugar = "Sugar"
            )
        }
        Spacer(modifier = Modifier.padding(3.dp))
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