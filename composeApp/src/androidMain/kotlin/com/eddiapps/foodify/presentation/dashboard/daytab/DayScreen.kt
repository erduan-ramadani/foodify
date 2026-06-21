package com.eddiapps.foodify.presentation.dashboard.daytab

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.eddiapps.foodify.R
import com.eddiapps.foodify.presentation.dashboard.AddEntryFab
import com.eddiapps.foodify.presentation.dashboard.UiConnectionEvent
import com.eddiapps.foodify.presentation.dashboard.daytab.components.BalanceCard
import com.eddiapps.foodify.presentation.dashboard.daytab.components.CalorieRing
import com.eddiapps.foodify.presentation.dashboard.daytab.components.EntriesCard
import com.eddiapps.foodify.presentation.dashboard.daytab.components.WeekDaySelector
import org.koin.androidx.compose.koinViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DayScreen(
    snackbarHostState: SnackbarHostState,
) {
    val vm: DayViewModel = koinViewModel()
    val onboarding by vm.onboardingData.collectAsStateWithLifecycle()
    val entriesByDate by vm.nutritionEntriesByDate.collectAsStateWithLifecycle()
    val dailyEntries = entriesByDate[vm.selectedDate] ?: emptyList()
    val dailyEaten = dailyEntries.filter { it.isMeal }.sumOf { it.calories }.toInt()
    val dailyBurned = dailyEntries.filter { !it.isMeal }.sumOf { it.calories }.toInt()
    val dailyCalories = dailyEaten - dailyBurned
    val dailyCalorieLimit = onboarding?.dailyCalorieLimit ?: 0
    val progress = if (dailyCalorieLimit > 0) dailyCalories.toFloat() / dailyCalorieLimit else 0f
    val remaining = kotlin.math.abs(dailyCalorieLimit - dailyCalories)

    val listState = rememberLazyListState()
    val resources = LocalResources.current
    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                vm.onResume()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    LaunchedEffect(Unit) {
        vm.connectionEvents.collect { event ->
            val message = when (event) {
                UiConnectionEvent.NoInternet -> resources.getString(R.string.error_no_internet)
                UiConnectionEvent.Timeout -> resources.getString(R.string.error_timeout)
                UiConnectionEvent.UnknownError -> resources.getString(R.string.error_unknown)
            }
            snackbarHostState.showSnackbar(message, duration = SnackbarDuration.Long)
        }
    }

    LaunchedEffect(Unit) {
        vm.messageEvents.collect { resId ->
            snackbarHostState.showSnackbar(
                message = resources.getString(resId),
                duration = SnackbarDuration.Long
            )
        }
    }

    LaunchedEffect(dailyEntries.size) {
        if (dailyEntries.isNotEmpty()) {
            listState.animateScrollToItem(
                index = listState.layoutInfo.totalItemsCount - 1,
                scrollOffset = Int.MAX_VALUE
            )
        }
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {

        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                item {
                    WeekDaySelector(
                        vm.visibleDays,
                        vm.selectedDate,
                        onProgressForDate = { vm.progressForDate(it) },
                        onWeekDaySelected = { vm.selectedDate = it }
                    )
                }
                item {
                    CalorieRing(
                        remainingCalories = remaining,
                        progress = progress,
                        isOverLimit = dailyCalories > dailyCalorieLimit
                    )
                }

                item {
                    BalanceCard(
                        dailyEaten,
                        dailyBurned,
                        dailyCalorieLimit
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

//        item {
//            RecentSuggestions(vm.recentEntries) { entry ->
//                vm.addNutritionFromSuggestion(entry)
//            }
//            Spacer(modifier = Modifier.height(4.dp))
//        }

                item {
                    EntriesCard(
                        entries = dailyEntries,
                        onDelete = { vm.removeNutritionEntry(it) },
                        onUpdate = { updateEntry ->
                            vm.updateEntry(updateEntry)
                        }
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                }
            }
        }
        AddEntryFab(
            onTextClick = { vm.requestNutritionValues(textQuery = it) },
            onCameraClick = { photoFilePath ->
                vm.requestNutritionValues("", photoFilePath)

            },
            onGalleryClick = { photoFilePath ->
                vm.requestNutritionValues("", photoFilePath)
            },
            onMicClick = { vm.requestNutritionValues(textQuery = it) },
            onBarcodeScanned = { barcode ->
                barcode?.let { vm.fetchBarcode(it) }
            },
            isLoading = vm.isLoading,
            modifier = Modifier.align(Alignment.BottomEnd),
        )
    }
}