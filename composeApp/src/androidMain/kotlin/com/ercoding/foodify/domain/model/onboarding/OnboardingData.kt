package com.ercoding.foodify.domain.model.onboarding

import kotlinx.serialization.Serializable

@Serializable
data class OnboardingData(
    val isMale: Boolean,
    val age: Int,
    val height: Int,
    val weight: Int,
    val dailyCalorieLimit: Int,
    val weightGoal: WeightGoal?
)

enum class WeightGoal(val kgPerWeek: Double, val label: String, val warning: String? = null) {
    SLOW(0.25, "Langsam", null),
    NORMAL(0.5, "Empfohlen", null),
    FAST(0.75, "Schnell", "Erfordert strikte Disziplin"),
    AGGRESSIVE(1.0, "Sehr schnell", "Nur für kurze Zeiträume empfohlen")
}