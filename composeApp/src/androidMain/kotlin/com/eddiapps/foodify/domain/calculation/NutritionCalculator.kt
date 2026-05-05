package com.eddiapps.foodify.domain.calculation

import com.eddiapps.foodify.domain.model.onboarding.WeightGoal

fun calculateBMR(isMale: Boolean, weight: Int, height: Int, age: Int): Double {
    val base = (10.0 * weight) + (6.25 * height) - (5.0 * age)
    return if (isMale) base + 5 else base - 161
}

fun calculateDailyCalorieLimit(
    isMale: Boolean,
    weight: Int,
    height: Int,
    age: Int,
    weightGoal: WeightGoal
): Int {
    return calculateBMR(isMale, weight, height, age).toInt() - weightGoal.dailyDeficit
}