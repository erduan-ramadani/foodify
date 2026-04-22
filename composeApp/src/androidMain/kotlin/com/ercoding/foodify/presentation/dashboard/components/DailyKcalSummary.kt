package com.ercoding.foodify.presentation.dashboard.components

import android.os.Build
import androidx.annotation.RequiresApi
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ercoding.foodify.presentation.dashboard.DashboardViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DailyKcalSummary(
    viewModel: DashboardViewModel
) {
    val onboardingData by viewModel.onboardingData.collectAsState()
    val progressColor = when {
        viewModel.progress > 0.75f -> Color.Red
        viewModel.progress in 0.5f..0.8f -> Color(0xFFFF7E19)
        else -> Color(0xFF004D02)
    }

    Box(contentAlignment = Alignment.Center) {
        CircularProgressIndicator(
            strokeWidth = 10.dp,
            modifier = Modifier.size(120.dp),
            progress = { viewModel.progress },
            color = progressColor
        )
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "${viewModel.remainingDailyCaloriesLimit}",
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = viewModel.calorieLimitText,
                color = progressColor,
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
                text = "${viewModel.dailyCalories}kcal",
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }
        Text(
            text = "/ ${onboardingData?.dailyCalorieLimit} kcal ",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}