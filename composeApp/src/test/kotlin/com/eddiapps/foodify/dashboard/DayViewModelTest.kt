@file:OptIn(ExperimentalCoroutinesApi::class)

package com.eddiapps.foodify.dashboard

import com.eddiapps.foodify.data.local.FakeNutritionRepository
import com.eddiapps.foodify.data.local.FakePreferencesRepository
import com.eddiapps.foodify.data.remote.FakeAnthropicRepository
import com.eddiapps.foodify.data.remote.FakeOpenFoodFactsRepository
import com.eddiapps.foodify.data.remote.openfoodfacts.OpenFoodFactsProduct
import com.eddiapps.foodify.data.remote.openfoodfacts.OpenFoodFactsResponse
import com.eddiapps.foodify.domain.model.sheet.NutritionEntry
import com.eddiapps.foodify.presentation.dashboard.daytab.DayViewModel
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
import kotlin.test.assertEquals

class DayViewModelTest {
    private val fakeNutritionRepository = FakeNutritionRepository()
    private val fakePreferencesRepository = FakePreferencesRepository()
    private val fakeAnthropicRepository = FakeAnthropicRepository()
    private val fakeOpenFoodFactsRepository = FakeOpenFoodFactsRepository()
    private lateinit var viewModel: DayViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        viewModel = DayViewModel(
            fakeAnthropicRepository,
            fakePreferencesRepository,
            fakeNutritionRepository,
            fakeOpenFoodFactsRepository
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `removeNutritionEntry removes entry from repository`() = runTest {
        // Given: ein Eintrag im Repository
        val entry = NutritionEntry(title = "Pizza", calories = 800.0)
        fakeNutritionRepository.addEntry(entry)

        // When: Eintrag wird entfernt
        viewModel.removeNutritionEntry(entry)

        // Then: Repository ist leer
        assertEquals(emptyList<NutritionEntry>(), fakeNutritionRepository.entries.value)
    }

    @Test
    fun `addNutritionEntry adds entry to repository`() = runTest {
        // Given: ein NutritionEntry
        val entry = NutritionEntry(title = "Pizza", calories = 800.0)
        // When: Eintrag wird hinzufgefügt
        viewModel.addNutritionEntry(entry, null)
        // Then: Repository hat einen Eintrag
        assertEquals(1, fakeNutritionRepository.entries.value.size)
        assertEquals("Pizza", fakeNutritionRepository.entries.value.first().title)
    }

    @Test
    fun `addNutritionEntry sets image path on entry`() = runTest {
        val entry = NutritionEntry(title = "Burger", calories = 600.0)
        val imagePath = "/path/to/image.jpg"

        viewModel.addNutritionEntry(entry, imagePath)

        val saved = fakeNutritionRepository.entries.value.first()
        assertEquals(imagePath, saved.imagePath)
    }

    @Test
    fun `addNutritionEntry sets today timestamp when selectedDate is today`() = runTest {
        viewModel.selectedDate = LocalDate.now()
        val entry = NutritionEntry(
            title = "Burger",
            calories = 600.0,
            createdAt = System.currentTimeMillis()
        )
        val beforeAdd = System.currentTimeMillis()

        viewModel.addNutritionEntry(entry, null)

        val saved = fakeNutritionRepository.entries.value.first()
        assert(saved.createdAt >= beforeAdd)
    }

    @Test
    fun `updateEntry updates existing entry in repository`() = runTest {
        val entry = NutritionEntry(
            title = "Burger",
            calories = 600.0,
        )
        fakeNutritionRepository.addEntry(entry)

        val updatedEntry = entry.copy(title = "Pommes")
        viewModel.updateEntry(updatedEntry)
        val saved = fakeNutritionRepository.entries.value.first().title
        assertEquals("Pommes", saved)
    }

    @Test
    fun `updateEntry does nothing when entry does not exist`() = runTest {
        val entry = NutritionEntry(
            id = "xyz",
            title = "Burger",
            calories = 600.0,
        )
        viewModel.updateEntry(entry)
        assertEquals(0, fakeNutritionRepository.entries.value.size)
    }

    @Test
    fun `fetchBarcode sets scannedProduct when product found`() = runTest {
        val product = OpenFoodFactsProduct(productName = "Nutella")
        fakeOpenFoodFactsRepository.setNextResult(
            Result.success(OpenFoodFactsResponse(status = 1, product))
        )

        viewModel.fetchBarcode("3017624010701")
        assertEquals(product, viewModel.scannedProduct.value)
    }

    @Test
    fun `fetchBarcode does not set scannedProduct when product not found`() = runTest {
        fakeOpenFoodFactsRepository.setNextResult(
            Result.success(OpenFoodFactsResponse(status = 0, null))
        )
        viewModel.fetchBarcode("0000000000000")
        assertEquals(null, viewModel.scannedProduct.value)
    }

    @Test
    fun `fetchBarcode passes barcode to repository`() = runTest {
        val barcode = "3017624010701"
        viewModel.fetchBarcode(barcode)
        assertEquals(barcode, fakeOpenFoodFactsRepository.lastBarcode)
    }

    @Test
    fun `fetchBarcode does not set scannedProduct on failure`() = runTest {
        fakeOpenFoodFactsRepository.setNextResult(Result.failure(Exception("network error")))
        viewModel.fetchBarcode("3017624010701")
        assertEquals(null, viewModel.scannedProduct.value)
    }
}