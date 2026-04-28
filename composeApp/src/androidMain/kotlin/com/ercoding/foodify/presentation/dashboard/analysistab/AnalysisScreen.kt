package com.ercoding.foodify.presentation.dashboard.analysistab

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ercoding.foodify.domain.extension.toDisplayString
import com.ercoding.foodify.presentation.dashboard.DashboardViewModel
import com.ercoding.foodify.presentation.dashboard.analysistab.components.BestDayCard
import com.ercoding.foodify.presentation.dashboard.analysistab.components.ConsistencyCard
import com.ercoding.foodify.presentation.dashboard.analysistab.components.GoalProgressCard
import com.ercoding.foodify.presentation.dashboard.analysistab.components.HeroCard
import com.ercoding.foodify.presentation.dashboard.analysistab.components.RangePicker
import com.ercoding.foodify.presentation.dashboard.analysistab.components.StatCard
import org.koin.androidx.compose.koinViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AnalysisScreen() {
    val vm: DashboardViewModel = koinViewModel()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Text(
                    text = "Dein Fortschritt im Überblick",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            item {
                RangePicker(
                    selected = vm.range,
                    onRangeChange = { vm.range = it },
                    vm
                )

                Spacer(modifier = Modifier.height(16.dp))
            }
            item {
                HeroCard(vm)

                Spacer(modifier = Modifier.height(12.dp))
            }
            item {
                GoalProgressCard(
                    progress = vm.weeklyProgress.toInt(),
                    goalDescription = "Ziel: ${vm.weeklyGoal} kg/Woche abnehmen (${vm.weeklyLimit.toInt()} kcal Defizit)"
                )

                Spacer(modifier = Modifier.height(12.dp))

            }
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    StatCard(
                        modifier = Modifier.weight(1f),
                        label = "Ø gegessen",
                        value = vm.avgConsumed,
                        unit = "kcal/Tag",
                        icon = "🍽️"
                    )
                    StatCard(
                        modifier = Modifier.weight(1f),
                        label = "Ø verbrannt",
                        value = vm.avgBurned,
                        unit = "kcal/Tag",
                        icon = "🔥"
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
            }
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    StatCard(
                        modifier = Modifier.weight(1f),
                        label = "Gesamt gegessen",
                        value = vm.totalConsumed,
                        unit = "kcal · ${vm.range} Tage",
                        icon = "📊"
                    )
                    StatCard(
                        modifier = Modifier.weight(1f),
                        label = "Gesamt verbrannt",
                        value = vm.totalBurned,
                        unit = "kcal · ${vm.range} Tage",
                        icon = "💪"
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
            }
            item {
                BestDayCard(
                    dayLabel = vm.bestDay?.first?.toDisplayString() ?: "—",
                    calories = vm.bestDay?.second?.toInt() ?: 0
                )
                Spacer(modifier = Modifier.height(12.dp))
            }
            item {
                ConsistencyCard(
                    trackedDays = vm.trackedDays,
                    totalDays = vm.range
                )
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}