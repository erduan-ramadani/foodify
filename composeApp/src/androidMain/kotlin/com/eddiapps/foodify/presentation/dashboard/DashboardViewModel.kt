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
import com.eddiapps.foodify.R
import com.eddiapps.foodify.domain.AnthropicInterface
import com.eddiapps.foodify.domain.PreferencesInterface
import com.eddiapps.foodify.domain.calculation.calculateSaltLimit
import com.eddiapps.foodify.domain.calculation.calculateSaturatedFatLimit
import com.eddiapps.foodify.domain.calculation.calculateSugarLimit
import com.eddiapps.foodify.domain.model.onboarding.WeightGoal
import com.eddiapps.foodify.domain.model.sheet.NutritionEntry
import com.eddiapps.foodify.presentation.util.imageFileToBase64
import io.ktor.client.plugins.HttpRequestTimeoutException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.UnknownHostException
import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.util.UUID
import kotlin.math.absoluteValue
import kotlin.math.roundToInt

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
    val visibleDays: List<LocalDate>
        get() {
            val today = LocalDate.now()
            val start = today.minusDays(21).with(DayOfWeek.MONDAY)
            val end = today.with(DayOfWeek.SUNDAY)
            val days = mutableListOf<LocalDate>()
            var date = start
            while (!date.isAfter(end)) {
                days.add(date)
                date = date.plusDays(1)
            }
            return days
        }
    var isLoading by mutableStateOf(false)
    private val _messageEvents = Channel<Int>()
    val messageEvents = _messageEvents.receiveAsFlow()
    private val _connectionEvents = Channel<UiConnectionEvent>()
    val connectionEvents = _connectionEvents.receiveAsFlow()

    // === DAY TAB ===
    // Merkt sich was "heute" war, als die App zuletzt aktiv war
    private var lastKnownToday: LocalDate = LocalDate.now()
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

    fun progressForDate(date: LocalDate): Float {
        val entries = nutritionEntriesByDate[date] ?: return 0f
        val eaten = entries.filter { it.isMeal }.sumOf { it.calories }
        val burned = entries.filter { !it.isMeal }.sumOf { it.calories }
        val net = eaten - burned
        return if (dailyCalorieLimit > 0)
            (net / dailyCalorieLimit).toFloat().coerceIn(0f, 1f)
        else 0f
    }

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

    init {
        viewModelScope.launch {
            val loaded = prefRepository.getNutritionEntries()
            nutritionEntries.addAll(loaded)
        }

        viewModelScope.launch {
            prefRepository.onboardingData.collect { data ->
                dailyCalorieLimit = data?.dailyCalorieLimit ?: 0
            }
        }
    }

    fun onResume() {
        val today = LocalDate.now()
        // Tag hat sich geändert UND User stand auf der "Heute"-Seite
        if (today != lastKnownToday && selectedDate == lastKnownToday) {
            selectedDate = today
        }
        lastKnownToday = today
    }

    fun requestNutritionValues(textQuery: String, imagePath: String? = null) {
        viewModelScope.launch {
            isLoading = true
            val result = if (imagePath != null) {
                val base64 = withContext(Dispatchers.IO) {
                    imageFileToBase64(imagePath)
                }
                anthropicRepo.requestNutritionValuesFromImage(base64)
            } else {
                anthropicRepo.requestNutritionValues(
                    textQuery,
                    onboardingData.value?.pickerState?.weightKg ?: 75.0
                )
            }
            result.onFailure { exception ->
                Log.d("Foodify", "Antwort failure: $exception")
                val event = when (exception) {
                    is UnknownHostException -> UiConnectionEvent.NoInternet
                    is HttpRequestTimeoutException -> UiConnectionEvent.Timeout
                    else -> UiConnectionEvent.UnknownError
                }
                _connectionEvents.send(event)
            }
            result.onSuccess { nutritionEntry ->
                Log.d("Foodify", "Antwort success: $nutritionEntry")
                if (!nutritionEntry.isMealDetected) { // from image
                    _messageEvents.send(R.string.meal_not_detected)
                } else {
                    val nutritionEntry = nutritionEntry.copy(imagePath = imagePath)
                    addEntry(nutritionEntry)
                }
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
            title = newName,
            createdAt = newTimestamp,
            calories = newCalories
        )

        viewModelScope.launch {
            prefRepository.setNutritionEntries(nutritionEntries)
        }
    }

    private fun getTotalNutritionValue(selector: (NutritionEntry) -> Double): Int {
        return (0 until range).sumOf { day ->
            val today = LocalDate.now()
            nutritionEntriesByDate[today.minusDays(day.toLong())]
                ?.sumOf { selector(it) } ?: 0.0
        }.roundToInt()
    }
}