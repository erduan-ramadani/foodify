package com.eddiapps.foodify.presentation.onboarding

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.eddiapps.foodify.domain.calculation.UnitConverter.convertCmToFeetInches
import com.eddiapps.foodify.domain.calculation.UnitConverter.convertKgToLb
import com.eddiapps.foodify.domain.calculation.UnitConverter.convertLbToKg
import com.eddiapps.foodify.domain.calculation.UnitConverter.toDecimal
import com.eddiapps.foodify.domain.calculation.calculateBMR
import com.eddiapps.foodify.domain.model.UnitSystem
import com.eddiapps.foodify.domain.model.onboarding.OnboardingData
import com.eddiapps.foodify.domain.model.onboarding.WeightGoal

class OnboardingViewModel(
) : ViewModel() {
    var unitSystem: UnitSystem by mutableStateOf(UnitSystem.METRIC)
        private set
    var isMale: Boolean? by mutableStateOf(null)
    var age: Int by mutableIntStateOf(36)
    var height: Int by mutableIntStateOf(180)
    var weight: Int by mutableIntStateOf(80)
    val heightUnit: String
        get() = if (unitSystem == UnitSystem.METRIC) "cm" else "ft/in"
    val weightUnit: String
        get() = if (unitSystem == UnitSystem.METRIC) "kg" else "lb"
    var weightGoal: WeightGoal? by mutableStateOf(WeightGoal.NORMAL)
    val dailyCalorieLimit: Int
        get() = bmr - (weightGoal?.dailyDeficit ?: 0)
    val bmr: Int
        get() = calculateBMR(isMale == true, weight, height, age).toInt()
    var activityLevel: ActivityLevel? by mutableStateOf(null)

    fun getOnboardingData(
    ): OnboardingData {
        return OnboardingData(
            isMale = isMale ?: false,
            age = age,
            height = height,
            weight = weight,
            unitSystem = unitSystem,
            dailyCalorieLimit = dailyCalorieLimit,
            weightGoal = weightGoal
        )
    }

    fun canProceed(currentPage: Int): Boolean = when (currentPage) {
        0 -> isMale != null
        1 -> activityLevel != null
        2 -> true
        else -> false
    }
    fun onUnitSystemChange(system: UnitSystem) {
        unitSystem = system
    }
}
