package com.eddiapps.foodify.presentation.settings

import com.eddiapps.foodify.data.local.FakeNutritionRepository
import com.eddiapps.foodify.data.local.FakePreferencesRepository
import com.eddiapps.foodify.domain.model.UnitSystem
import com.eddiapps.foodify.domain.model.onboarding.OnboardingData
import com.eddiapps.foodify.domain.model.onboarding.WeightGoal
import com.eddiapps.foodify.presentation.picker.PickerState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class SettingsViewModelTest {
    private val fakePreferencesRepository = FakePreferencesRepository()
    private val fakeNutritionRepository = FakeNutritionRepository()
    private lateinit var viewModel: SettingsViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        viewModel = SettingsViewModel(fakePreferencesRepository, fakeNutritionRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `toggleDarkMode switches from false to true`() = runTest {
        assertEquals(false, fakePreferencesRepository.darkMode.first())
        viewModel.toggleDarkMode()
        assertEquals(true, fakePreferencesRepository.darkMode.first())
    }

    @Test
    fun `toggleDarkMode switches from true to false`() = runTest {
        val fake = FakePreferencesRepository(initialDarkMode = true)
        assertEquals(true, fake.darkMode.first())

        val vm = SettingsViewModel(fake, fakeNutritionRepository)
        vm.toggleDarkMode()
        assertEquals(false, fake.darkMode.first())
    }

    @Test
    fun `toggleReminder switches from false to true`() = runTest {
        assertEquals(false, fakePreferencesRepository.reminder.first())
        viewModel.toggleReminder()
        assertEquals(true, fakePreferencesRepository.reminder.first())
    }

    @Test
    fun `toggleGender flips gender and recalculates limit`() = runTest {
        val initial = OnboardingData(
            isMale = true,
            unitSystem = UnitSystem.METRIC,
            pickerState = PickerState(
                age = 36,
                heightCm = 180,
                weightKg = 80.0,
                heightFt = 5,
                heightIn = 11,
                weightLb = 176
            ),
            dailyCalorieLimit = 1550, // BMR 1750 * 1.2 - 550
            weightGoal = WeightGoal.NORMAL,
            tdee = 2100
        )
        val fakePreferencesRepository = FakePreferencesRepository(initial)
        val vm = SettingsViewModel(fakePreferencesRepository, fakeNutritionRepository)

        vm.onboardingData.first { it != null }
        vm.toggleGender()

        val updated = fakePreferencesRepository.onboardingData.first()
        assertEquals(false, updated?.isMale)
        // Frau: BMR 1584 * 1.2 - 550 = 1351
        assertEquals(1350, updated?.dailyCalorieLimit)
    }

    @Test
    fun `deleteAllData clears both repositories`() = runTest {
        viewModel.deleteAllData()

        assertEquals(true, fakeNutritionRepository.clearAllCalled)
        assertEquals(true, fakePreferencesRepository.clearAllCalled)
        assertEquals(0, fakeNutritionRepository.entries.value.size)
    }
}