package com.eddiapps.foodify.domain.calculation

import com.eddiapps.foodify.domain.model.onboarding.WeightGoal
import com.eddiapps.foodify.presentation.onboarding.ActivityLevel

fun calculateBMR(isMale: Boolean, weight: Double, height: Int, age: Int): Double {
    val base = (10.0 * weight) + (6.25 * height) - (5.0 * age)
    return if (isMale) base + 5 else base - 161
}

fun calculateDailyCalorieLimit(
    isMale: Boolean,
    age: Int,
    weight: Double,
    height: Int,
    weightGoal: WeightGoal
): Int {
    return calculateBMR(isMale, weight, height, age).toInt() - weightGoal.dailyDeficit
}

fun calculateTDEE(bmr: Int, activityLevel: ActivityLevel?): Int {
    val tdee = when (activityLevel) {
        ActivityLevel.SEDENTARY -> bmr * 1.2
        ActivityLevel.LIGHT -> bmr * 1.375
        ActivityLevel.ACTIVE -> bmr * 1.55
        ActivityLevel.VERY_ACTIVE -> bmr * 1.725
        else -> bmr * 1.375
    }
    return tdee.toInt()
}

fun calculateSaturatedFatLimit(
    dailyCalorieLimit: Int,
    days: Int
): Double {
    val dailySatFatLimit = (dailyCalorieLimit * 0.1) / 9.0
    return (dailySatFatLimit * days)
}

fun calculateSugarLimit(
    dailyCalorieLimit: Int,
    days: Int,
): Double {
    return (((dailyCalorieLimit * 0.1) / 4.0) * days)
}

fun calculateSaltLimit(days: Int): Double {
    return 5.0 * days
}