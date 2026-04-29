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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.ercoding.foodify.R
import com.ercoding.foodify.presentation.dashboard.DashboardViewModel
import com.ercoding.foodify.presentation.dashboard.analysistab.components.BestDayCard
import com.ercoding.foodify.presentation.dashboard.analysistab.components.ConsistencyCard
import com.ercoding.foodify.presentation.dashboard.analysistab.components.GoalProgressCard
import com.ercoding.foodify.presentation.dashboard.analysistab.components.HeroCard
import com.ercoding.foodify.presentation.dashboard.analysistab.components.RangePicker
import com.ercoding.foodify.presentation.dashboard.analysistab.components.StatCard
import com.ercoding.foodify.presentation.util.toDisplayString
import org.koin.androidx.compose.koinViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AnalysisScreen() {
    val vm: DashboardViewModel = koinViewModel()
    // Triggers recomposition when onboarding data changes (e.g. weeklyGoal in settings)
    val onboarding by vm.onboardingData.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 8.dp)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Text(
                    text = stringResource(R.string.analysis_title),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            item {
                RangePicker(
                    selected = vm.range,
                    onRangeChange = { vm.range = it },
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
                    goalDescription = stringResource(
                        R.string.goal,
                        vm.weeklyGoal,
                        vm.weeklyLimit.toInt()
                    )
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
                        label = stringResource(R.string.average_eaten),
                        value = vm.avgEaten,
                        unit = stringResource(R.string.kcal_per_day),
                        icon = "🍽️"
                    )
                    StatCard(
                        modifier = Modifier.weight(1f),
                        label = stringResource(R.string.average_burned),
                        value = vm.avgBurned,
                        unit = stringResource(R.string.kcal_per_day),
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
                        label = stringResource(R.string.total_eaten),
                        value = vm.totalConsumed,
                        unit = stringResource(R.string.kcal_x_days, vm.range),
                        icon = "📊"
                    )
                    StatCard(
                        modifier = Modifier.weight(1f),
                        label = stringResource(R.string.total_burned),
                        value = vm.totalBurned,
                        unit = stringResource(R.string.kcal_x_days, vm.range),
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