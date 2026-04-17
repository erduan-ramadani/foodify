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

@RequiresApi(Build.VERSION_CODES.O)
class DashboardViewModel(
    private val anthropicRepo: AnthropicInterface,
    private val prefRepository: PreferencesInterface
) : ViewModel() {

    var nutritionEntries = mutableStateListOf<NutritionEntry>()
    val dailyThreshold by mutableIntStateOf(3000)
    val dailyCalories: Int get() = getDailyCalories(selectedDate)
    val progress by mutableIntStateOf(0)
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

    @RequiresApi(Build.VERSION_CODES.O)
    fun getDailyCalories(date: LocalDate?): Int {
        return (nutritionEntriesByDate[date]?.sumOf { it.calories } ?: 0).toInt()
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
//        val percentage: Int = (progress * 100).toInt()
//        return if (percentage >= 100) 100
//        else percentage
    }

    fun getRemainingDailyCalories(): Int {
        return dailyThreshold - dailyCalories
    }
}