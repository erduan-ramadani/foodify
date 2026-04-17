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
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.net.UnknownHostException
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.util.UUID

@RequiresApi(Build.VERSION_CODES.O)
class DashboardViewModel(
    private val anthropicRepo: AnthropicInterface,
    private val prefRepository: PreferencesInterface
) : ViewModel() {

    val dailyReached: Int get() = getDailyReached(selectedDate)
    val dailyGoal = prefRepository.proteinGoal.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        0
    )
    var proteinEntries = mutableStateListOf<ProteinEntry>()

    val proteinEntriesByDate: Map<LocalDate, List<ProteinEntry>>
        @RequiresApi(Build.VERSION_CODES.O)
        get() = proteinEntries.groupBy { entry ->
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
    val progress: Float
        get() =
            if ((dailyGoal.value ?: 0) == 0) 0f
            else dailyReached.toFloat() / (dailyGoal.value ?: 0)

    private val _events = Channel<String>()
    val events = _events.receiveAsFlow()

    init {
        viewModelScope.launch {
            proteinEntries.addAll(prefRepository.getProteinEntries())
        }
    }

    fun addProteins(query: String) {
        viewModelScope.launch {
            isLoading = true
            val result = anthropicRepo.requestProteinAmount(query)
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
                val parts = response.split("|")
                val proteinAmount = parts[0].toIntOrNull() ?: 0
                val emoji = parts.getOrNull(1) ?: "🍽️"
                if (proteinAmount == 0) {
                    _events.send("Proteingehalt nicht gefunden")
                } else {
                    val createdAt = selectedDate?.atStartOfDay(ZoneId.systemDefault())?.toInstant()
                        ?.toEpochMilli()
                        ?: System.currentTimeMillis()

                    proteinEntries += ProteinEntry(
                        UUID.randomUUID().toString(),
                        query,
                        proteinAmount,
                        emoji,
                        createdAt
                    )
                    prefRepository.setDailyReached(dailyReached)
                    prefRepository.setProteinEntries(proteinEntries)
                }
            }
            isLoading = false
        }
    }

    fun reset() {
        viewModelScope.launch {
            prefRepository.setDailyReached(dailyReached)
            proteinEntries.clear()
            prefRepository.setProteinEntries(proteinEntries)
        }
    }

    fun removeProteinEntry(entry: ProteinEntry) {
        viewModelScope.launch {
            prefRepository.setDailyReached(dailyReached)

            proteinEntries.removeIf { it.id == entry.id }
            prefRepository.setProteinEntries(proteinEntries)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getDailyReached(date: LocalDate?): Int {
        return proteinEntriesByDate[date]?.sumOf { it.proteinAmount } ?: 0
    }

    fun getProgress(progress: Float): Int {
        val percentage: Int = (progress * 100).toInt()
        return if (percentage >= 100) 100
        else percentage

    }

    fun getDailyProteinAmountRemaining(): Int? {
        val remaining = dailyGoal.value?.minus(dailyReached)
        return if (remaining == null || remaining <= 0) 0
        else remaining
    }

    fun getEntryAmountOfDay(date: LocalDate): Int {
        val currentEntriesCount = proteinEntriesByDate[date]?.size ?: 0
        return currentEntriesCount
    }
}