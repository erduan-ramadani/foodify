//@file:OptIn(ExperimentalCoroutinesApi::class)
//
//package com.ercoding.foodify.presentation.dashboard
//
//import com.ercoding.foodify.data.local.FakePreferencesRepository
//import com.ercoding.foodify.data.remote.FakeAnthropicRepository
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.ExperimentalCoroutinesApi
//import kotlinx.coroutines.test.UnconfinedTestDispatcher
//import kotlinx.coroutines.test.advanceUntilIdle
//import kotlinx.coroutines.test.resetMain
//import kotlinx.coroutines.test.runTest
//import kotlinx.coroutines.test.setMain
//import org.junit.After
//import org.junit.Before
//import org.junit.Test
//
//class DashboardViewModelTest {
//    private val fakePreferencesRepo = FakePreferencesRepository()
//    private lateinit var viewModel: DashboardViewModel
//
//    @Before
//    fun setup() {
//        Dispatchers.setMain(UnconfinedTestDispatcher())
//    }
//
//    @After
//    fun tearDown() {
//        Dispatchers.resetMain()
//    }
//
//    @Test
//    fun addProteins_withZeroProtein_doesNotAddEntry() = runTest {
//        viewModel = DashboardViewModel(
//            FakeAnthropicRepository(0), fakePreferencesRepo
//        )
//        viewModel.addNutritionValues("asdfg")
//        advanceUntilIdle()
//        assert(viewModel.nutritionEntries.isEmpty())
//    }
//
//    @Test
//    fun reset_clearsDailyReachedAndEntries() = runTest {
//        viewModel = DashboardViewModel(
//            FakeAnthropicRepository(35), fakePreferencesRepo
//        )
//        viewModel.addNutritionValues("100g hähnchen")
//        advanceUntilIdle()
//        viewModel.reset()
//    }
//}