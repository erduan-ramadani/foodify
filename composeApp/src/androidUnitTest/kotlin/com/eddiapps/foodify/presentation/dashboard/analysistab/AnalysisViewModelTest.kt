package com.eddiapps.foodify.presentation.dashboard.analysistab

import com.eddiapps.foodify.data.local.FakeNutritionRepository
import com.eddiapps.foodify.data.local.FakePreferencesRepository
import com.eddiapps.foodify.domain.model.sheet.NutritionEntry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.time.LocalDate
import java.time.ZoneId
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class AnalysisViewModelTest {
    private lateinit var fakePreferences: FakePreferencesRepository
    private lateinit var fakeNutrition: FakeNutritionRepository
    private lateinit var viewModel: AnalysisViewModel

    private fun todayTimestamp(): Long =
        LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()

    private fun daysAgoTimestamp(daysAgo: Long): Long =
        LocalDate.now().minusDays(daysAgo).atStartOfDay(ZoneId.systemDefault()).toInstant()
            .toEpochMilli()

    @Before
    fun setup() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        fakePreferences = FakePreferencesRepository()
        fakeNutrition = FakeNutritionRepository()
        viewModel = AnalysisViewModel(fakePreferences, fakeNutrition)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `totalEaten sums calories of meal entries`() = runTest {
        fakeNutrition.addEntry(
            NutritionEntry(
                title = "Pizza", calories = 800.0, isMeal = true, createdAt = todayTimestamp()
            )
        )
        fakeNutrition.addEntry(
            NutritionEntry(
                title = "Burger", calories = 600.0, isMeal = true, createdAt = todayTimestamp()
            )
        )

        assertEquals(1400, viewModel.totalEaten)
    }

    @Test
    fun `totalBurned sums calories of non-meal entries`() = runTest {
        fakeNutrition.addEntry(
            NutritionEntry(
                title = "Run", calories = 400.0, isMeal = false, createdAt = todayTimestamp()
            )
        )
        fakeNutrition.addEntry(
            NutritionEntry(
                title = "Gym", calories = 800.0, isMeal = false, createdAt = todayTimestamp()
            )
        )
        assertEquals(1200, viewModel.totalBurned)
    }

    @Test
    fun `totalCalories equals eaten minus burned`() = runTest {
        fakeNutrition.addEntry(
            NutritionEntry(
                title = "Pizza", calories = 800.0, isMeal = true, createdAt = todayTimestamp()
            )
        )
        fakeNutrition.addEntry(
            NutritionEntry(
                title = "Run", calories = 300.0, isMeal = false, createdAt = todayTimestamp()
            )
        )

        assertEquals(500, viewModel.totalCalories)
    }

    @Test
    fun `avgEaten returns average calories per tracked day`() = runTest {
        val today = todayTimestamp()
        fakeNutrition.addEntry(
            NutritionEntry(
                title = "Pizza", calories = 1000.0, isMeal = true, createdAt = today
            )
        )
        fakeNutrition.addEntry(
            NutritionEntry(
                title = "Salad", calories = 500.0, isMeal = true, createdAt = today
            )
        )

        // Ein Tag tracked, 1500 kcal total → avg 1500
        assertEquals(1500, viewModel.avgEaten)
    }

    @Test
    fun `avgEaten returns 0 when no entries`() = runTest {
        assertEquals(0, viewModel.avgEaten)
    }

    @Test
    fun `trackedDays counts only one day when entries are on same day`() = runTest {
        fakeNutrition.addEntry(
            NutritionEntry(
                title = "Pizza", calories = 800.0, isMeal = true, createdAt = todayTimestamp()
            )
        )

        assertEquals(1, viewModel.trackedDays)
    }

    @Test
    fun `trackedDays counts multiple days correctly`() = runTest {
        fakeNutrition.addEntry(
            NutritionEntry(
                title = "Pizza", calories = 800.0, isMeal = true, createdAt = todayTimestamp()
            )
        )
        fakeNutrition.addEntry(
            NutritionEntry(
                title = "Burger", calories = 500.0, isMeal = true, createdAt = daysAgoTimestamp(1)
            )
        )

        assertEquals(2, viewModel.trackedDays)
    }

    @Test
    fun `range can be changed`() = runTest {
        viewModel.range = 30

        assertEquals(30, viewModel.range)
    }

    @Test
    fun `totalSugar sums sugar from all entries on same day`() = runTest {
        fakeNutrition.addEntry(
            NutritionEntry(
                title = "Cookie", sugar = 25.0, isMeal = true, createdAt = todayTimestamp()
            )
        )
        fakeNutrition.addEntry(
            NutritionEntry(
                title = "Cola", sugar = 35.0, isMeal = true, createdAt = todayTimestamp()
            )
        )

        assertEquals(60, viewModel.totalSugar)
    }

    @Test
    fun `totalSugar sums sugar from all entries on different days`() = runTest {
        fakeNutrition.addEntry(
            NutritionEntry(
                title = "Cookie", sugar = 25.0, isMeal = true, createdAt = todayTimestamp()
            )
        )
        fakeNutrition.addEntry(
            NutritionEntry(
                title = "Cola", sugar = 35.0, isMeal = true, createdAt = daysAgoTimestamp(3)
            )
        )

        assertEquals(60, viewModel.totalSugar)
    }

    @Test
    fun `totalSugar includes entries within selected range`() = runTest {
        fakeNutrition.addEntry(
            NutritionEntry(
                title = "Cookie", sugar = 25.0, isMeal = true, createdAt = todayTimestamp()
            )
        )
        fakeNutrition.addEntry(
            NutritionEntry(
                title = "Cola", sugar = 35.0, isMeal = true, createdAt = daysAgoTimestamp(15)
            )
        )

        // Default range = 7 → nur Cookie (heute) zählt
        assertEquals(25, viewModel.totalSugar)
    }

    @Test
    fun `totalSugar includes more entries when range is extended`() = runTest {
        fakeNutrition.addEntry(
            NutritionEntry(
                title = "Cookie", sugar = 25.0, isMeal = true, createdAt = todayTimestamp()
            )
        )
        fakeNutrition.addEntry(
            NutritionEntry(
                title = "Cola", sugar = 35.0, isMeal = true, createdAt = daysAgoTimestamp(15)
            )
        )

        // Range auf 30 erweitern → jetzt zählen beide
        viewModel.range = 30

        assertEquals(60, viewModel.totalSugar)
    }

    @Test
    fun `getTotalNutritionValue with protein selector`() = runTest {
        fakeNutrition.addEntry(
            NutritionEntry(
                title = "Chicken", protein = 30.0, isMeal = true, createdAt = todayTimestamp()
            )
        )
        fakeNutrition.addEntry(
            NutritionEntry(
                title = "Eggs", protein = 12.0, isMeal = true, createdAt = todayTimestamp()
            )
        )

        assertEquals(42, viewModel.getTotalNutritionValue { it.protein })
    }

    @Test
    fun `getTotalNutritionValue returns 0 when no entries`() = runTest {
        assertEquals(0, viewModel.getTotalNutritionValue { it.protein })
    }
}