package com.eddiapps.foodify.domain.model.onboarding

import kotlinx.serialization.Serializable

@Serializable
data class OnboardingData(
    val isMale: Boolean,
    val age: Int,
    val height: Int,
    val weight: Int,
    val unitSystem: UnitSystem,
    val dailyCalorieLimit: Int,
    val weightGoal: WeightGoal?
)

enum class WeightGoal(val kgPerWeek: Double) {
    SLOW(0.25),
    NORMAL(0.5),
    FAST(0.75),
    AGGRESSIVE(1.0);

    val dailyDeficit: Int
        get() = (7700 * kgPerWeek / 7).toInt()
}