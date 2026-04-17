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
import com.ercoding.foodify.domain.ProteinEntry
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

    var nutritionEntries = mutableStateListOf<ProteinEntry>()

    val nutritionEntriesByDate: Map<LocalDate, List<ProteinEntry>>
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

    fun removeNutritionEntry(entry: ProteinEntry) {
        viewModelScope.launch {
            nutritionEntries.removeIf { it.id == entry.id }
            prefRepository.setNutritionEntries(nutritionEntries)
        }
    }

    fun getProgress(progress: Float): Int {
        val percentage: Int = (progress * 100).toInt()
        return if (percentage >= 100) 100
        else percentage

    }

    fun getEntryAmountOfDay(date: LocalDate): Int {
        val currentEntriesCount = nutritionEntriesByDate[date]?.size ?: 0
        return currentEntriesCount
    }
}