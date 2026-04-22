package com.ercoding.foodify.domain.model.onboarding

import kotlinx.serialization.Serializable

@Serializable
data class OnboardingData(
    val isMale: Boolean,
    val age: Int,
    val size: Int,
    val weight: Int,
    val dailyCalorieLimit: Int,
    val weightGoal: Int
)