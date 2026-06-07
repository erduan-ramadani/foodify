package com.eddiapps.foodify.presentation.dashboard.analysistab

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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.eddiapps.foodify.R
import com.eddiapps.foodify.domain.calculation.calculateSaltLimit
import com.eddiapps.foodify.domain.calculation.calculateSaturatedFatLimit
import com.eddiapps.foodify.domain.calculation.calculateSugarLimit
import com.eddiapps.foodify.presentation.dashboard.analysistab.components.AllNutrientsCard
import com.eddiapps.foodify.presentation.dashboard.analysistab.components.BestDayCard
import com.eddiapps.foodify.presentation.dashboard.analysistab.components.GoalProgressCard
import com.eddiapps.foodify.presentation.dashboard.analysistab.components.HeroCard
import com.eddiapps.foodify.presentation.dashboard.analysistab.components.NutrientProgressCard
import com.eddiapps.foodify.presentation.dashboard.analysistab.components.RangePicker
import com.eddiapps.foodify.presentation.dashboard.analysistab.components.StatCard
import com.eddiapps.foodify.presentation.util.toDisplayString
import org.koin.androidx.compose.koinViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AnalysisScreen() {
    val vm: AnalysisViewModel = koinViewModel()
    val onboarding by vm.onboardingData.collectAsStateWithLifecycle()
    val tdee = onboarding?.tdee ?: 2000
    val calorieDeficit = (tdee * vm.trackedDays) - vm.totalCalories
    val weightChange = -calorieDeficit.toDouble() / 7700.0
    val dailyCalorieLimit = onboarding?.dailyCalorieLimit ?: 0
    val weeklyLimit = 7700 * vm.weeklyGoal
    val weeklyProgress = if (calorieDeficit > 0) {
        (calorieDeficit / weeklyLimit * 100).toInt()
    } else 0
    val nutrients = listOf(
        // Macros
        stringResource(R.string.nutrient_protein) to "${vm.totalProtein} g",
        stringResource(R.string.nutrient_carbs) to "${vm.totalCarbs} g",
        stringResource(R.string.nutrient_fiber) to "${vm.totalFiber} g",
        stringResource(R.string.nutrient_fat) to "${vm.totalFat} g",
        stringResource(R.string.nutrient_cholesterol) to "${vm.totalCholesterol} mg",

        // Fatty acids
        stringResource(R.string.nutrient_omega3) to "${vm.totalOmega3} g",
        stringResource(R.string.nutrient_omega6) to "${vm.totalOmega6} g",

        // Vitamins
        stringResource(R.string.nutrient_vitamin_a) to "${vm.totalVitaminA} µg",
        stringResource(R.string.nutrient_vitamin_b6) to "${vm.totalVitaminB6} mg",
        stringResource(R.string.nutrient_vitamin_b12) to "${vm.totalVitaminB12} µg",
        stringResource(R.string.nutrient_vitamin_c) to "${vm.totalVitaminC} mg",
        stringResource(R.string.nutrient_vitamin_d) to "${vm.totalVitaminD} µg",
        stringResource(R.string.nutrient_vitamin_e) to "${vm.totalVitaminE} mg",
        stringResource(R.string.nutrient_vitamin_k) to "${vm.totalVitaminK} µg",
        stringResource(R.string.nutrient_folic_acid) to "${vm.totalFolicAcid} µg",

        // Minerals
        stringResource(R.string.nutrient_calcium) to "${vm.totalCalcium} mg",
        stringResource(R.string.nutrient_iron) to "${vm.totalIron} mg",
        stringResource(R.string.nutrient_magnesium) to "${vm.totalMagnesium} mg",
        stringResource(R.string.nutrient_zinc) to "${vm.totalZinc} mg",
        stringResource(R.string.nutrient_potassium) to "${vm.totalPotassium} mg",
        stringResource(R.string.nutrient_phosphorus) to "${vm.totalPhosphorus} mg",
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
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
                HeroCard(
                    weightChange,
                    calorieDeficit
                )
                Spacer(modifier = Modifier.height(12.dp))
            }
            item {
                GoalProgressCard(
                    progress = weeklyProgress,
                    goalDescription = stringResource(
                        R.string.goal,
                        vm.weeklyGoal,
                        weeklyLimit.toInt()
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
                        value = vm.totalEaten,
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
//            item {
//                ConsistencyCard(
//                    trackedDays = vm.trackedDays,
//                    totalDays = vm.range
//                )
//                Spacer(modifier = Modifier.height(12.dp))
//            }
            item {
                Text(stringResource(R.string.nutrient_critical))
                Spacer(modifier = Modifier.height(12.dp))
            }
            item {
                NutrientProgressCard(
                    label = stringResource(R.string.nutrient_sugar),
                    current = vm.totalSugar.toFloat(),
                    limit = calculateSugarLimit(dailyCalorieLimit, vm.range).toFloat(),
                    range = vm.range,
                    icon = "\uD83C\uDF6C"
                )
                Spacer(modifier = Modifier.height(12.dp))

                NutrientProgressCard(
                    label = stringResource(R.string.nutrient_salt),
                    current = vm.totalSalt.toFloat(),
                    limit = calculateSaltLimit(vm.range).toFloat(),
                    range = vm.range,
                    icon = "\uD83E\uDDC2",
                )
                Spacer(modifier = Modifier.height(12.dp))

                NutrientProgressCard(
                    label = stringResource(R.string.nutrient_saturated_fat),
                    current = vm.totalSaturatedFat.toFloat(),
                    limit = calculateSaturatedFatLimit(
                        dailyCalorieLimit, vm.range
                    ).toFloat(),
                    range = vm.range,
                    icon = "\uD83E\uDD69",
                )
                Spacer(modifier = Modifier.height(12.dp))
            }
            item {
                Text(stringResource(R.string.nutrient_days_balance, vm.range))
                Spacer(modifier = Modifier.height(12.dp))
            }
            item {
                AllNutrientsCard(nutrients)
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}