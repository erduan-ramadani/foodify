package com.eddiapps.foodify.presentation.dashboard.analysistab

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eddiapps.foodify.domain.PreferencesInterface
import com.eddiapps.foodify.domain.calculation.calculateSaltLimit
import com.eddiapps.foodify.domain.calculation.calculateSaturatedFatLimit
import com.eddiapps.foodify.domain.calculation.calculateSugarLimit
import com.eddiapps.foodify.domain.model.NutritionInterface
import com.eddiapps.foodify.domain.model.onboarding.WeightGoal
import com.eddiapps.foodify.domain.model.sheet.NutritionEntry
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import kotlin.math.roundToInt

@RequiresApi(Build.VERSION_CODES.O)
class AnalysisViewModel(
    prefRepository: PreferencesInterface,
    nutritionRepository: NutritionInterface
) : ViewModel() {
    val onboardingData = prefRepository.onboardingData.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        null
    )
    private val entries = nutritionRepository.entries  // StateFlow<List<NutritionEntry>>
    val nutritionEntriesByDate: Map<LocalDate, List<NutritionEntry>>
        get() = entries.value.groupBy { entry ->
            Instant.ofEpochMilli(entry.createdAt)
                .atZone(ZoneId.systemDefault())
                .toLocalDate()
        }.toSortedMap()
    val dailyCalorieLimit: Int
        get() = onboardingData.value?.dailyCalorieLimit ?: 0
    var range by mutableIntStateOf(7)   // 7, 30, oder 90
    private val entriesInRange: List<NutritionEntry>
        get() {
            val today = LocalDate.now()
            return (0 until range).flatMap { day ->
                nutritionEntriesByDate[today.minusDays(day.toLong())] ?: emptyList()
            }
        }

    val totalBurned: Int
        get() {
            return entriesInRange.filter { !it.isMeal }
                .sumOf { it.calories }.toInt()
        }
    val totalEaten: Int
        get() {
            return entriesInRange
                .filter { it.isMeal }
                .sumOf { it.calories }.toInt()
        }

    val totalCalories: Int
        get() {
            return totalEaten - totalBurned
        }

    val trackedDays: Int
        get() {
            val today = LocalDate.now()
            return (0 until range).count { day ->
                nutritionEntriesByDate[today.minusDays(day.toLong())]?.isNotEmpty() == true
            }
        }

    val weeklyGoal: Double
        get() = onboardingData.value?.weightGoal?.kgPerWeek ?: WeightGoal.NORMAL.kgPerWeek

    val weeklyLimit: Double
        get() = 7000 * weeklyGoal
    val weeklyProgress: Double
        get() {
            val progress = calorieDeficit / weeklyLimit * 100
            return if (progress > 0) progress else 0.0
        }
    val avgEaten: Int
        get() = if (trackedDays > 0) totalEaten / trackedDays else 0
    val avgBurned: Int
        get() = if (trackedDays > 0) totalBurned / trackedDays else 0

    val totalSaturatedFatLimit: Double
        get() = calculateSaturatedFatLimit(dailyCalorieLimit, range)

    val totalSugarLimit: Double
        get() = calculateSugarLimit(dailyCalorieLimit, range)

    val totalSaltLimit: Double
        get() = calculateSaltLimit(range)

    val bestDay: Pair<LocalDate, Double>?
        get() {
            val today = LocalDate.now()
            val dayDeficits = (0 until range)
                .mapNotNull { day ->
                    val date = today.minusDays(day.toLong())
                    val entries = nutritionEntriesByDate[date] ?: return@mapNotNull null
                    if (entries.isEmpty()) return@mapNotNull null
                    val net = entries.sumOf { if (it.isMeal) it.calories else -it.calories }
                    date to (dailyCalorieLimit - net)
                }
                .filter { it.second > 0 }  // nur Tage mit echtem Defizit
            return dayDeficits.maxByOrNull { it.second }
        }

    val weightChange: Double
        get() = (totalCalories - (dailyCalorieLimit * trackedDays)) / 7700.0

    val calorieDeficit: Int
        get() = ((dailyCalorieLimit * trackedDays) - totalCalories)

    val nutrientList: List<Pair<String, String>>
        get() = listOf(
            "Protein" to "$totalProtein g",
            "Kohlenhydrate" to "$totalCarbs g",
            "Cholesterin" to "$totalCholesterol mg",
            "Magnesium" to "$totalMagnesium mg",
            "Eisen" to "$totalIron mg",
            "Vitamin C" to "$totalVitaminC mg",
        )

    val totalSugar: Int get() = getTotalNutritionValue { it.sugar }
    val totalSaturatedFat: Int get() = getTotalNutritionValue { it.saturatedFat }
    val totalSalt: Int get() = getTotalNutritionValue { it.salt }
    val totalProtein: Int get() = getTotalNutritionValue { it.protein }
    val totalCarbs: Int get() = getTotalNutritionValue { it.carbohydrates }
    val totalCholesterol: Int get() = getTotalNutritionValue { it.cholesterol }
    val totalMagnesium: Int get() = getTotalNutritionValue { it.magnesium }
    val totalIron: Int get() = getTotalNutritionValue { it.iron }
    val totalVitaminC: Int get() = getTotalNutritionValue { it.vitaminC }

    private fun getTotalNutritionValue(selector: (NutritionEntry) -> Double): Int {
        return (0 until range).sumOf { day ->
            val today = LocalDate.now()
            nutritionEntriesByDate[today.minusDays(day.toLong())]
                ?.sumOf { selector(it) } ?: 0.0
        }.roundToInt()
    }
}