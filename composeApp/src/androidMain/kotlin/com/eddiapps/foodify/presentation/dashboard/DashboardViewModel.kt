package com.eddiapps.foodify.presentation.dashboard

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eddiapps.foodify.domain.AnthropicInterface
import com.eddiapps.foodify.domain.PreferencesInterface
import com.eddiapps.foodify.domain.model.onboarding.WeightGoal
import com.eddiapps.foodify.domain.model.sheet.NutritionEntry
import io.ktor.client.plugins.HttpRequestTimeoutException
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.net.UnknownHostException
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
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
    var dailyCalorieLimit by mutableIntStateOf(0)

    var selectedDate: LocalDate by mutableStateOf(LocalDate.now())
    val isToday: Boolean
        get() = selectedDate == LocalDate.now()
    var isLoading by mutableStateOf(false)
    private val _stringEvents = Channel<String>()
    private val _connectionEvents = Channel<UiConnectionEvent>()
    val stringEvents = _stringEvents.receiveAsFlow()
    val connectionEvents = _connectionEvents.receiveAsFlow()

    // === DAY TAB ===
    val dailyCalories: Int
        get() {
            return dailyCaloriesEaten - dailyCaloriesBurned
        }
    val dailyCaloriesEaten: Int
        get() {
            return (nutritionEntriesByDate[selectedDate]?.filter { it.isMeal }
                ?.sumOf { it.calories }
                ?: 0).toInt()
        }
    val dailyCaloriesBurned: Int
        get() {
            return (nutritionEntriesByDate[selectedDate]?.filter { !it.isMeal }
                ?.sumOf { it.calories }
                ?: 0).toInt()
        }

    val dailyFat get() = getDailyTotal { it.fat }
    val dailyProtein get() = getDailyTotal { it.protein }
    val dailySugar get() = getDailyTotal { it.sugar }
    val dailyCarbs get() = getDailyTotal { it.carbohydrates }

    val recentEntries: List<NutritionEntry>
        get() = nutritionEntries.distinctBy { it.query }.take(8)

    val nutritionEntriesByDate: Map<LocalDate, List<NutritionEntry>>
        get() = nutritionEntries.groupBy { entry ->
            Instant.ofEpochMilli(entry.createdAt)
                .atZone(ZoneId.systemDefault())
                .toLocalDate()
        }.toSortedMap()
    val progress: Float
        get() = if (dailyCalorieLimit > 0) dailyCalories.toFloat() / dailyCalorieLimit else 0f

    val remainingDailyCaloriesLimit: Int
        get() = (dailyCalorieLimit - dailyCalories).absoluteValue


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

    private val entriesLastWeek: List<NutritionEntry>
        get() {
            val weekAgoMillis = System.currentTimeMillis() - 7L * 24 * 60 * 60 * 1000
            return nutritionEntries.filter { it.createdAt >= weekAgoMillis }
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
            return dayDeficits.maxByOrNull { it.second }
        }

    val weightChange: Double
        get() = (totalCalories - (dailyCalorieLimit * trackedDays)) / 7700.0

    val calorieDeficit: Int
        get() = ((dailyCalorieLimit * trackedDays) - totalCalories)

    init {
        viewModelScope.launch {
            val loaded = prefRepository.getNutritionEntries()
            Log.d("Foodify", "Loaded entries: ${loaded.size}")
            loaded.forEach { println("  - ${it.query} at ${it.createdAt}") }
            nutritionEntries.addAll(loaded)
        }

        viewModelScope.launch {
            prefRepository.onboardingData.collect { data ->
                dailyCalorieLimit = data?.dailyCalorieLimit ?: 0
            }
        }
    }

    fun requestNutritionValues(query: String) {
        viewModelScope.launch {
            isLoading = true
            val formattedQuery = query.replaceFirstChar { it.uppercase() }
            val result = anthropicRepo.requestNutritionValues(
                formattedQuery,
                onboardingData.value?.weight ?: 75
            )
            result.onFailure { exception ->
                val event = when (exception) {
                    is UnknownHostException -> UiConnectionEvent.NoInternet
                    is HttpRequestTimeoutException -> UiConnectionEvent.Timeout
                    else -> UiConnectionEvent.UnknownError
                }
                _connectionEvents.send(event)
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
        val timestamp = if (selectedDate == LocalDate.now()) {
            System.currentTimeMillis()
        } else {
            selectedDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        }
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

    fun updateEntry(
        entry: NutritionEntry,
        newName: String,
        newTime: LocalTime,
        newCalories: Double
    ) {
        val index = nutritionEntries.indexOfFirst { it.id == entry.id }
        if (index == -1) return

        // Datum vom alten Eintrag behalten, Uhrzeit ersetzen
        val oldDate = Instant.ofEpochMilli(entry.createdAt)
            .atZone(ZoneId.systemDefault())
            .toLocalDate()
        val newTimestamp = oldDate.atTime(newTime)
            .atZone(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()

        nutritionEntries[index] = entry.copy(
            query = newName,
            createdAt = newTimestamp,
            calories = newCalories
        )

        viewModelScope.launch {
            prefRepository.setNutritionEntries(nutritionEntries)
        }
    }
}