package com.eddiapps.foodify.domain.model.onboarding

import com.eddiapps.foodify.domain.model.UnitSystem
import com.eddiapps.foodify.presentation.picker.PickerState
import kotlinx.serialization.Serializable

@Serializable
data class OnboardingData(
    val isMale: Boolean,
    val unitSystem: UnitSystem,
    val pickerState: PickerState,
    val dailyCalorieLimit: Int,
    val weightGoal: WeightGoal?,
    val tdee: Int
)

enum class WeightGoal(val kgPerWeek: Double) {
    SLOW(0.25),
    NORMAL(0.5),
    FAST(0.75),
    AGGRESSIVE(1.0);

    val dailyDeficit: Int
        get() = (7700 * kgPerWeek / 7).toInt()
}