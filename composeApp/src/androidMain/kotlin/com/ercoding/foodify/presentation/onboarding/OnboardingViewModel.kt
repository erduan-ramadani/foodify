package com.ercoding.foodify.presentation.onboarding

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.ercoding.foodify.domain.model.onboarding.OnboardingData

class OnboardingViewModel(
) : ViewModel() {
    var isMale: Boolean? by mutableStateOf(null)
    var age: Int by mutableIntStateOf(36)
    var height: Int by mutableIntStateOf(180)
    var weight: Int by mutableIntStateOf(80)
    var weightGoal: Int by mutableIntStateOf(78)
    var dailyGoal: Int by mutableIntStateOf(0)

    fun getButtonLabel(currentPage: Int): String = when (currentPage) {
        0 -> "Bedarf berechnen"
        1 -> "Los gehts"
        else -> "Weiter"
    }

    fun getOnboardingData(
    ): OnboardingData {
        return OnboardingData(
            isMale = isMale ?: false,
            age = age,
            height = height,
            weight = weight,
            dailyCalorieLimit = dailyGoal,
            weightGoal = weightGoal
        )
    }

    fun canProceed(currentPage: Int): Boolean = when (currentPage) {
        0 -> isMale != null
        1 -> true
        2 -> true
        else -> false
    }

    fun calculateBMR(): Double {
        val base = (10 * weight) + (6.25 * height) - (5 * age)
        return if (isMale == true) base + 5 else base - 161
    }

    fun getGoalText(): String {
        val base = calculateBMR().toInt()
        val weeklyDeficit = 7700 / 7
        if (weight < weightGoal) {
            dailyGoal = base + weeklyDeficit
            return "Du brauchst einen Kalorienüberschuss von $weeklyDeficit kcal täglich, um 1kg pro Woche zuzunehmen. Daraus ergibt sich ein Tagesziel von $dailyGoal kcal."
        } else if (weight > weightGoal) {
            dailyGoal = base - weeklyDeficit
            return "Du brauchst ein Kaloriendefizit von $weeklyDeficit kcal täglich, um 1kg pro Woche abzunehmen. Daraus ergibt sich ein Tageslimit von $dailyGoal kcal."
        } else {
            dailyGoal = base
            return "Du brauchst täglich $base kcal um dein Gewicht zu halten"
        }
    }

}