package com.ercoding.foodify.presentation.dashboard

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ercoding.foodify.domain.AnthropicInterface
import com.ercoding.foodify.domain.PreferencesInterface
import com.ercoding.foodify.domain.model.sheet.NutritionEntry
import io.ktor.client.plugins.HttpRequestTimeoutException
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.net.UnknownHostException
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.math.absoluteValue

@RequiresApi(Build.VERSION_CODES.O)
class DashboardViewModel(
    private val anthropicRepo: AnthropicInterface,
    private val prefRepository: PreferencesInterface
) : ViewModel() {

    val dailyCalories: Int get() = getDailyCalories(selectedDate)
    val dailyCarbs get() = getDailyTotal { it.carbohydrates }
    val dailyFat get() = getDailyTotal { it.fat }
    val dailyProtein get() = getDailyTotal { it.protein }
    val dailySugar get() = getDailyTotal { it.sugar }
    var nutritionEntries = mutableStateListOf<NutritionEntry>()
    val nutritionEntriesByDate: Map<LocalDate, List<NutritionEntry>>
        @RequiresApi(Build.VERSION_CODES.O)
        get() = nutritionEntries.groupBy { entry ->
            Instant.ofEpochMilli(entry.createdAt)
                .atZone(ZoneId.systemDefault())
                .toLocalDate()
        }.toSortedMap()
    val dailyThreshold = prefRepository.dailyThreshold.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        1234
    )
    var selectedDate: LocalDate by mutableStateOf(LocalDate.now())
    val isToday: Boolean
        get() = selectedDate == LocalDate.now()
    val isYesterday: Boolean
        get() = selectedDate == LocalDate.now().minusDays(1)
    var isLoading by mutableStateOf(false)
    private val _events = Channel<String>()
    val events = _events.receiveAsFlow()

    init {
        viewModelScope.launch {
            println("Threshold: ${prefRepository.dailyThreshold.first()}")
            nutritionEntries.addAll(prefRepository.getNutritionEntries())
        }
    }

    fun addNutritionValues(query: String, date: LocalDate?) {
        viewModelScope.launch {
            isLoading = true
            val query = query.replaceFirstChar { it.uppercase() }
            val result = anthropicRepo.requestNutritionValues(query)
            result.onFailure { exception ->
                val errorMessage = when (exception) {
                    is UnknownHostException -> "Kein Internet"
                    is HttpRequestTimeoutException -> "Timeout"
                    else -> "Unbekannter Fehler"
                }
                println("Exception: $exception")
                _events.send(errorMessage)
            }
            result.onSuccess { response ->
                println("Antwort: $response")
                val timestamp = date?.atStartOfDay(ZoneId.systemDefault())?.toInstant()
                    ?.toEpochMilli() ?: System.currentTimeMillis()
                nutritionEntries.add(response.copy(createdAt = timestamp))
                prefRepository.setNutritionEntries(nutritionEntries)
            }
            isLoading = false
        }
    }

    fun removeNutritionEntry(entry: NutritionEntry) {
        viewModelScope.launch {
            nutritionEntries.removeIf { it.id == entry.id }
            prefRepository.setNutritionEntries(nutritionEntries)
        }
    }

    fun getProgress(): Float {
        return dailyCalories.toFloat() / dailyThreshold.value
    }

    fun getProgressColor(): Color {
        val progress = getProgress()
        return if (progress > 0.75f) {
            Color.Red
        } else if (progress in 0.5f..0.8f) {
            Color(0xFFFF7E19)
        } else {
            Color(0xFF004D02)
        }
    }

    fun getRemainingDailyCalories(): Int {
        val remaining = dailyThreshold.value - dailyCalories
        return remaining.absoluteValue
    }

    fun getCalorieLimitText(): String {
        val calorieDeficit = dailyThreshold.value - dailyCalories
        return if (calorieDeficit >= 0) {
            "kcal übrig"
        } else {
            "kcal Überschuss"
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getDailyCalories(date: LocalDate?): Int {
        return (nutritionEntriesByDate[date]?.sumOf { it.calories } ?: 0).toInt()
    }

    fun getDailyTotal(selector: (NutritionEntry) -> Double): Int {
        return (nutritionEntriesByDate[selectedDate]?.sumOf(selector) ?: 0.0).toInt()
    }
}

fun LocalDate.toDisplayString(): String {
    val today = LocalDate.now()
    val formatter = DateTimeFormatter.ofPattern("d. MMMM", Locale.GERMAN)
    val fullFormatter = DateTimeFormatter.ofPattern("EEEE, d. MMMM", Locale.GERMAN)

    return when (this) {
        today -> "Heute, ${format(formatter)}"
        today.minusDays(1) -> "Gestern, ${format(formatter)}"
        else -> format(fullFormatter)
    }
}