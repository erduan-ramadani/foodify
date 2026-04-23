package com.ercoding.foodify.presentation.dashboard

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.getValue
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

    val dailyCalories: Int get() = getDailyCalories(selectedDate)
    val dailyCarbs get() = getDailyTotal { it.carbohydrates }
    val dailyFat get() = getDailyTotal { it.fat }
    val dailyProtein get() = getDailyTotal { it.protein }
    val dailySugar get() = getDailyTotal { it.sugar }
    var nutritionEntries = mutableStateListOf<NutritionEntry>()
    val recentEntries: List<NutritionEntry>
        get() = nutritionEntries.distinctBy { it.query }

    val nutritionEntriesByDate: Map<LocalDate, List<NutritionEntry>>
        @RequiresApi(Build.VERSION_CODES.O)
        get() = nutritionEntries.groupBy { entry ->
            Instant.ofEpochMilli(entry.createdAt)
                .atZone(ZoneId.systemDefault())
                .toLocalDate()
        }.toSortedMap()
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

    val progress: Float
        get() = dailyCalories.toFloat() / dailyCalorieLimit

    val remainingDailyCaloriesLimit: Int
        get() = (dailyCalorieLimit - dailyCalories).absoluteValue

    val calorieLimitText: String
        get() = if (dailyCalorieLimit - dailyCalories >= 0) "kcal übrig" else "kcal Überschuss"

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
                println("Exception: $exception")
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

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getDailyCalories(date: LocalDate?): Int {
        return (nutritionEntriesByDate[date]?.sumOf { it.calories } ?: 0).toInt()
    }

    private fun getDailyTotal(selector: (NutritionEntry) -> Double): Int {
        return (nutritionEntriesByDate[selectedDate]?.sumOf(selector) ?: 0.0).toInt()
    }
}