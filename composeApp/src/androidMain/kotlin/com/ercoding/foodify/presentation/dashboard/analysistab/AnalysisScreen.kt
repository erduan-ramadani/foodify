package com.ercoding.foodify.presentation.dashboard.analysistab

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.ercoding.foodify.presentation.dashboard.analysistab.components.BestDayCard
import com.ercoding.foodify.presentation.dashboard.analysistab.components.ConsistencyCard
import com.ercoding.foodify.presentation.dashboard.analysistab.components.GoalProgressCard
import com.ercoding.foodify.presentation.dashboard.analysistab.components.HeroCard
import com.ercoding.foodify.presentation.dashboard.analysistab.components.RangePicker
import com.ercoding.foodify.presentation.dashboard.analysistab.components.StatCard
import com.ercoding.foodify.presentation.dashboard.analysistab.components.WeeklyBarChart
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalysisScreen() {
    val viewModel: AnalysisViewModel = koinViewModel()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Text(
            text = "Dein Fortschritt im Überblick",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        RangePicker(
            selected = viewModel.range,
            onRangeChange = { viewModel.range = it }
        )

        Spacer(modifier = Modifier.height(16.dp))

        HeroCard(
            estimatedKg = viewModel.estimatedKg,
            netDeficit = viewModel.netDeficit
        )

        Spacer(modifier = Modifier.height(12.dp))

        GoalProgressCard(
            progress = viewModel.goalProgress,
            goalDescription = "Ziel: 0,5 kg/Woche abnehmen (3.500 kcal Defizit)"
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            StatCard(
                modifier = Modifier.weight(1f),
                label = "Ø gegessen",
                value = viewModel.avgConsumed,
                unit = "kcal/Tag",
                color = Color(0xFFE8652E),
                icon = "🍽️"
            )
            StatCard(
                modifier = Modifier.weight(1f),
                label = "Ø verbrannt",
                value = viewModel.avgBurned,
                unit = "kcal/Tag",
                color = Color(0xFF4CAF50),
                icon = "🔥"
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            StatCard(
                modifier = Modifier.weight(1f),
                label = "Gesamt gegessen",
                value = viewModel.totalConsumed,
                unit = "kcal · ${viewModel.range} Tage",
                color = Color(0xFFF5A623),
                icon = "📊"
            )
            StatCard(
                modifier = Modifier.weight(1f),
                label = "Gesamt verbrannt",
                value = viewModel.totalBurned,
                unit = "kcal · ${viewModel.range} Tage",
                color = Color(0xFF3A7CA5),
                icon = "💪"
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        BestDayCard(
            dayLabel = viewModel.bestDay?.label ?: "—",
            calories = viewModel.bestDay?.calories ?: 0
        )

        Spacer(modifier = Modifier.height(12.dp))

        ConsistencyCard(
            trackedDays = viewModel.trackedDays,
            totalDays = viewModel.range
        )

        Spacer(modifier = Modifier.height(12.dp))

        WeeklyBarChart(
            data = viewModel.weekData,
            dailyLimit = viewModel.dailyLimit
        )

        Spacer(modifier = Modifier.height(40.dp))
    }
}