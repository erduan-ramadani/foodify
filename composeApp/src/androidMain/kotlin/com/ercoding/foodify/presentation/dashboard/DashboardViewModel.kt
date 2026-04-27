package com.ercoding.foodify.presentation.dashboard

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ercoding.foodify.domain.AnthropicInterface
import com.ercoding.foodify.domain.PreferencesInterface
import com.ercoding.foodify.domain.model.sheet.NutritionEntry
import io.ktor.client.plugins.HttpRequestTimeoutException
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.net.UnknownHostException
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.util.UUID
import kotlin.math.absoluteValue

@RequiresApi(Build.VERSION_CODES.O)
class DashboardViewModel(
    private val anthropicRepo: AnthropicInterface,
    private val prefRepository: PreferencesInterface
) : ViewModel() {

    // === SHARED STATE ===
    var selectedTab by mutableIntStateOf(0)
    var nutritionEntries = mutableStateListOf<NutritionEntry>()
    val onboardingData = prefRepository.onboardingData.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        null
    )
    val dailyCalorieLimit: Int
        get() = onboardingData.value?.dailyCalorieLimit ?: 1

    var selectedDate: LocalDate by mutableStateOf(LocalDate.now())
    val isToday: Boolean
        get() = selectedDate == LocalDate.now()
    var isLoading by mutableStateOf(false)
    private val _events = Channel<String>()
    val events = _events.receiveAsFlow()

    // === DAY TAB ===
    val dailyCalories: Int
        get() {
            return (nutritionEntriesByDate[selectedDate]?.sumOf { it.calories } ?: 0).toInt()
        }

    val dailyFat get() = getDailyTotal { it.fat }
    val dailyProtein get() = getDailyTotal { it.protein }
    val dailySugar get() = getDailyTotal { it.sugar }

    val recentEntries: List<NutritionEntry>
        get() = nutritionEntries.distinctBy { it.query }

    val nutritionEntriesByDate: Map<LocalDate, List<NutritionEntry>>
        get() = nutritionEntries.groupBy { entry ->
            Instant.ofEpochMilli(entry.createdAt)
                .atZone(ZoneId.systemDefault())
                .toLocalDate()
        }.toSortedMap()
    val dailyCarbs get() = getDailyTotal { it.carbohydrates }
    val progress: Float
        get() = dailyCalories.toFloat() / dailyCalorieLimit

    val remainingDailyCaloriesLimit: Int
        get() = (dailyCalorieLimit - dailyCalories).absoluteValue

    val calorieLimitText: String
        get() = if (dailyCalorieLimit - dailyCalories >= 0) "kcal übrig" else "kcal über"


    // === ANALYSIS TAB ===
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
                .sumOf { it.calories }.toInt().absoluteValue
        }
    val totalConsumed: Int
        get() {
            return entriesInRange
                .filter { it.isMeal }
                .sumOf { it.calories }.toInt()
        }
    val trackedDays: Int
        get() {
            val today = LocalDate.now()
            return (0 until range).count { day ->
                nutritionEntriesByDate[today.minusDays(day.toLong())]?.isNotEmpty() == true
            }
        }

    val totalCalories: Double
        get() {
            val today = LocalDate.now()
            val calorieRangeList = (0 until range).map { day ->
                nutritionEntriesByDate[today.minusDays(day.toLong())]?.sumOf { it.calories } ?: 0.0
            }
            return calorieRangeList.sum()
        }

    val estimatedKg: Double = 0.0   // positiv = abgenommen
    val goalProgress: Float = 0f    // 0f..100f
    val avgConsumed: Int
        get() = if (trackedDays > 0) totalConsumed / trackedDays else 0
    val avgBurned: Int
        get() = if (trackedDays > 0) totalBurned / trackedDays else 0

    val bestDay: Pair<LocalDate, Double>?
        get() {
            val dayDeficits: Map<LocalDate, Double> = (0 until range).associate { day ->
                val today = LocalDate.now()

                val date = today.minusDays(day.toLong())
                val entries = nutritionEntriesByDate[date] ?: emptyList()
                val net =
                    entries.sumOf { it.calories }  // gegessen - verbrannt (weil Aktivitäten negativ sind)
                val deficit = dailyCalorieLimit - net
                date to deficit
            }
            return dayDeficits.entries.maxByOrNull { it.value }?.let { it.key to it.value }
        }

    val weightChange: Double
        get() = (totalCalories - (dailyCalorieLimit * trackedDays)) / 7700.0

    val calorieDeficit: Int
        get() = ((dailyCalorieLimit * trackedDays) - totalCalories).toInt()

    init {
        viewModelScope.launch {
            nutritionEntries.addAll(prefRepository.getNutritionEntries())
        }
    }

    fun requestNutritionValues(query: String) {
        viewModelScope.launch {
            isLoading = true
            val formattedQuery = query.replaceFirstChar { it.uppercase() }
            val result = anthropicRepo.requestNutritionValues(formattedQuery)
            result.onFailure { exception ->
                val errorMessage = when (exception) {
                    is UnknownHostException -> "Kein Internet"
                    is HttpRequestTimeoutException -> "Timeout"
                    else -> "Unbekannter Fehler"
                }
                _events.send(errorMessage)
            }
            result.onSuccess { response ->
                println("Antwort: $response")
                addEntry(response)
            }
            isLoading = false
        }
    }

    fun addNutritionFromSuggestion(nutritionEntry: NutritionEntry) {
        val newEntry = nutritionEntry.copy(
            id = UUID.randomUUID().toString(),
        )
        addEntry(newEntry)
    }

    private fun addEntry(nutritionEntry: NutritionEntry) {
        val timestamp = selectedDate.atStartOfDay(ZoneId.systemDefault())?.toInstant()
            ?.toEpochMilli() ?: System.currentTimeMillis()
        nutritionEntries.add(nutritionEntry.copy(createdAt = timestamp))
        viewModelScope.launch {
            prefRepository.setNutritionEntries(nutritionEntries)
        }
    }

    fun removeNutritionEntry(entry: NutritionEntry) {
        viewModelScope.launch {
            nutritionEntries.removeIf { it.id == entry.id }
            prefRepository.setNutritionEntries(nutritionEntries)
        }
    }

    private fun getDailyTotal(selector: (NutritionEntry) -> Double): Int {
        return (nutritionEntriesByDate[selectedDate]?.sumOf(selector) ?: 0.0).toInt()
    }
}