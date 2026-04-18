package com.ercoding.foodify.presentation.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun DailySummary(
    viewModel: DashboardViewModel
) {

    Box(contentAlignment = Alignment.Center) {
        CircularProgressIndicator(
            strokeWidth = 10.dp,
            modifier = Modifier.size(120.dp),
            progress = { viewModel.getProgress() },
            color = viewModel.getProgressColor()
        )
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "${viewModel.getRemainingDailyCalories()}",
                color = Color.Black,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = viewModel.getCalorieLimitText(),
                color = viewModel.getProgressColor(),
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Bold
            )
        }
    }
    Spacer(modifier = Modifier.padding(8.dp))
    Row {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(50))
                .background(MaterialTheme.colorScheme.primaryContainer)
                .padding(horizontal = 16.dp)
        ) {
            Text(
                text = "${viewModel.dailyCalories}kcal gegessen",
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }
        Text(
            text = "/ ${viewModel.dailyThreshold}kcal",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}