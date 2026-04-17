package com.ercoding.foodify.presentation.dashboard

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ercoding.foodify.domain.AnthropicInterface
import com.ercoding.foodify.domain.NutritionEntry
import com.ercoding.foodify.domain.PreferencesInterface
import io.ktor.client.plugins.HttpRequestTimeoutException
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.net.UnknownHostException
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import kotlin.math.absoluteValue

@RequiresApi(Build.VERSION_CODES.O)
class DashboardViewModel(
    private val anthropicRepo: AnthropicInterface,
    private val prefRepository: PreferencesInterface
) : ViewModel() {

    var nutritionEntries = mutableStateListOf<NutritionEntry>()
    val dailyThreshold by mutableIntStateOf(3000)
    val dailyCalories: Int get() = getDailyCalories(selectedDate)
    val nutritionEntriesByDate: Map<LocalDate, List<NutritionEntry>>
        @RequiresApi(Build.VERSION_CODES.O)
        get() = nutritionEntries.groupBy { entry ->
            Instant.ofEpochMilli(entry.createdAt)
                .atZone(ZoneId.systemDefault())
                .toLocalDate()
        }.toSortedMap()

    @RequiresApi(Build.VERSION_CODES.O)
    val last7Days: List<LocalDate> = (0..6).map {
        LocalDate.now().minusDays(it.toLong())
    }.reversed()
    var selectedDate: LocalDate? by mutableStateOf(LocalDate.now())

    var isLoading by mutableStateOf(false)

    private val _events = Channel<String>()
    val events = _events.receiveAsFlow()

    init {
        viewModelScope.launch {
            nutritionEntries.addAll(prefRepository.getNutritionEntries())
        }
    }

    fun addNutritionValues(query: String) {
        viewModelScope.launch {
            isLoading = true
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
            nutritionEntries.add(response)
            }
            isLoading = false
        }
    }

    fun reset() {
        viewModelScope.launch {
            nutritionEntries.clear()
            prefRepository.setNutritionEntries(nutritionEntries)
        }
    }

    fun removeNutritionEntry(entry: NutritionEntry) {
        viewModelScope.launch {
            nutritionEntries.removeIf { it.id == entry.id }
            prefRepository.setNutritionEntries(nutritionEntries)
        }
    }

    fun getProgress(): Float {
        return dailyCalories.toFloat() / dailyThreshold
    }

    fun getProgressColor(): Color {
        val progress = getProgress()
        return if (progress > 0.75f){
            Color.Red
        } else if (progress in 0.5f..0.8f){
            Color(0xFFFF7E19)
        } else {
            Color(0xFF004D02)
        }
    }

    fun getRemainingDailyCalories(): Int {
        val remaining = dailyThreshold - dailyCalories
        return remaining.absoluteValue
    }

    fun getCalorieLimitText(): String{
        val calorieDeficit = dailyThreshold - dailyCalories
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

    @RequiresApi(Build.VERSION_CODES.O)
    fun getDailyCarbs(): Int {
        return (nutritionEntriesByDate[selectedDate]?.sumOf { it.carbohydrates } ?: 0).toInt()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getDailyFat(): Int {
        return (nutritionEntriesByDate[selectedDate]?.sumOf { it.fat } ?: 0).toInt()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getDailyProtein(): Int {
        return (nutritionEntriesByDate[selectedDate]?.sumOf { it.protein } ?: 0).toInt()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getDailySugar(): Int {
        return (nutritionEntriesByDate[selectedDate]?.sumOf { it.sugar } ?: 0).toInt()
    }
}