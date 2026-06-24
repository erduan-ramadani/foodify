package com.eddiapps.foodify.presentation.dashboard.daytab

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eddiapps.foodify.R
import com.eddiapps.foodify.data.remote.openfoodfacts.OpenFoodFactsProduct
import com.eddiapps.foodify.data.remote.openfoodfacts.toNutritionEntry
import com.eddiapps.foodify.domain.AnthropicInterface
import com.eddiapps.foodify.domain.OpenFoodFactsInterface
import com.eddiapps.foodify.domain.PreferencesInterface
import com.eddiapps.foodify.domain.model.NutritionInterface
import com.eddiapps.foodify.domain.model.sheet.NutritionEntry
import com.eddiapps.foodify.presentation.dashboard.UiConnectionEvent
import com.eddiapps.foodify.presentation.util.imageFileToBase64
import io.ktor.client.plugins.HttpRequestTimeoutException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
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

@RequiresApi(Build.VERSION_CODES.O)
class DayViewModel(
    private val anthropicRepo: AnthropicInterface,
    prefRepository: PreferencesInterface,
    private val nutritionRepository: NutritionInterface,
    private val openFoodFactsRepository: OpenFoodFactsInterface
) : ViewModel() {
    private val nutritionEntries = nutritionRepository.entries  // StateFlow<List<NutritionEntry>>
    val nutritionEntriesByDate: StateFlow<Map<LocalDate, List<NutritionEntry>>> =
        nutritionEntries.map { list ->
            list.groupBy {
                Instant.ofEpochMilli(it.createdAt)
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate()
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyMap())
    val onboardingData = prefRepository.onboardingData.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        null
    )
    var isLoading by mutableStateOf(false)
    private val _messageEvents = Channel<Int>()
    val messageEvents = _messageEvents.receiveAsFlow()
    private val _connectionEvents = Channel<UiConnectionEvent>()
    val connectionEvents = _connectionEvents.receiveAsFlow()
    val dailyCalorieLimit: Int
        get() = onboardingData.value?.dailyCalorieLimit ?: 0
    var selectedDate: LocalDate by mutableStateOf(LocalDate.now())
    val visibleDays: List<LocalDate>
        get() {
            val today = LocalDate.now()
            val start = today.minusDays(21).with(DayOfWeek.MONDAY)
            val days = mutableListOf<LocalDate>()
            var date = start
            while (!date.isAfter(today)) {
                days.add(date)
                date = date.plusDays(1)
            }
            return days
        }

    // Merkt sich was "heute" war, als die App zuletzt aktiv war
    private var lastKnownToday: LocalDate = LocalDate.now()
    val dailyCalories: Int
        get() {
            return dailyCaloriesEaten - dailyCaloriesBurned
        }
    val dailyCaloriesEaten: Int
        get() {
            return (nutritionEntriesByDate.value[selectedDate]?.filter { it.isMeal }
                ?.sumOf { it.calories }
                ?: 0).toInt()
        }
    val dailyCaloriesBurned: Int
        get() {
            return (nutritionEntriesByDate.value[selectedDate]?.filter { !it.isMeal }
                ?.sumOf { it.calories }
                ?: 0).toInt()
        }
    val recentEntries: List<NutritionEntry>
        get() = nutritionEntries.value.distinctBy { it.query }.take(8)
    val progress: Float
        get() = if (dailyCalorieLimit > 0) dailyCalories.toFloat() / dailyCalorieLimit else 0f

    private val _scannedProduct = MutableStateFlow<OpenFoodFactsProduct?>(null)
    val scannedProduct: StateFlow<OpenFoodFactsProduct?> = _scannedProduct

    fun progressForDate(date: LocalDate): Float {
        val entries = nutritionEntriesByDate.value[date] ?: return 0f
        val eaten = entries.filter { it.isMeal }.sumOf { it.calories }
        val burned = entries.filter { !it.isMeal }.sumOf { it.calories }
        val net = eaten - burned
        return if (dailyCalorieLimit > 0)
            (net / dailyCalorieLimit).toFloat().coerceIn(0f, 1f)
        else 0f
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
                if (!nutritionEntry.isMealDetected) {
                    _messageEvents.send(R.string.meal_not_detected)
                } else {
                    addNutritionEntry(nutritionEntry, imagePath)
                }
            }
            isLoading = false
        }
    }

    fun addNutritionEntry(entry: NutritionEntry, imagePath: String?) {
        val timestamp = if (selectedDate == LocalDate.now()) {
            System.currentTimeMillis()
        } else {
            selectedDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        }
        val entry = entry.copy(
            imagePath = imagePath,
            createdAt = timestamp
        )
        nutritionRepository.addEntry(entry)
    }

    fun removeNutritionEntry(entry: NutritionEntry) {
        nutritionRepository.removeEntry(entry)
    }

    fun updateEntry(
        updatedEntry: NutritionEntry
    ) {
        val index = nutritionEntries.value.indexOfFirst { it.id == updatedEntry.id }
        if (index == -1) return
        nutritionRepository.updateEntry(updatedEntry)
    }

    fun fetchBarcode(barcode: String) {
        viewModelScope.launch {
            val result = openFoodFactsRepository.getProductByBarcode(barcode)
            result.onSuccess { response ->
                if (response.status == 1 && response.product != null) {
                    _scannedProduct.value = response.product
                } else {
                    _messageEvents.send(R.string.product_not_found)
                }
            }
            result.onFailure {
                _messageEvents.send(R.string.error_unknown)
            }
        }
    }

    fun saveBarcodeProduct(grams: Int) {
        val product = _scannedProduct.value ?: return
        val timestamp = selectedDate
            .atTime(LocalTime.now())
            .atZone(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()
        val entry = product.toNutritionEntry(grams, timestamp)
        viewModelScope.launch {
            nutritionRepository.addEntry(entry)
            _scannedProduct.value = null
        }
    }

    fun dismissBarcodeSheet() {
        _scannedProduct.value = null
    }
}