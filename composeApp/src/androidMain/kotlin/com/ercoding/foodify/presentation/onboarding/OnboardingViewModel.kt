package com.ercoding.foodify.presentation.onboarding

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.ercoding.foodify.domain.model.onboarding.NutritionGoal
import com.ercoding.foodify.domain.model.onboarding.OnboardingData

class OnboardingViewModel(
) : ViewModel() {
    var isMale: Boolean? by mutableStateOf(null)
    var age: Int by mutableIntStateOf(30)
    var size: Int by mutableIntStateOf(180)
    var weight: Int by mutableIntStateOf(80)
    var dailyCalorieLimit: Int by mutableIntStateOf(0)
    var goal: NutritionGoal? by mutableStateOf(null)

    fun getButtonLabel(currentPage: Int): String = when (currentPage) {
        0 -> "Bedarf berechnen"
        1 -> "Tracking starten"
        else -> "Weiter"
    }

    fun getOnboardingData(
    ): OnboardingData {
        return OnboardingData(
            isMale = isMale ?: false,
            age = age,
            size = size,
            weight = weight,
            dailyCalorieLimit = dailyCalorieLimit,
            goal = goal ?: NutritionGoal.LOSE
        )
    }

    fun canProceed(currentPage: Int): Boolean = when (currentPage) {
        0 -> isMale != null
        1 -> goal != null
        2 -> true
        else -> false
    }

    fun calculateBMR(): Double {
        val base = (10 * weight) + (6.25 * size) - (5 * age)
        return if (isMale == true) base + 5 else base - 161
    }

    fun getGoalText(): String {
        val base = calculateBMR().toInt()
        dailyCalorieLimit = base - (7700 / 7)
        return when (goal) {
            NutritionGoal.LOSE -> "Du brauchst ein Kaloriendefizit von $dailyCalorieLimit kcal täglich, um 1kg pro Woche abzunehmen"
            NutritionGoal.MAINTAIN -> "Du brauchst tägl. $base kcal um dein Gewicht zu halten"
            NutritionGoal.GAIN -> "Du brauchst einen Kalorienüberschuss von $dailyCalorieLimit kcal täglich, um 1kg pro Woche zuzunehmen"
            else -> ""
        }
    }

}