package com.ercoding.foodify.presentation.onboarding

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.ercoding.foodify.domain.calculation.calculateBMR
import com.ercoding.foodify.domain.model.onboarding.OnboardingData
import com.ercoding.foodify.domain.model.onboarding.WeightGoal

class OnboardingViewModel(
) : ViewModel() {
    var isMale: Boolean? by mutableStateOf(null)
    var age: Int by mutableIntStateOf(36)
    var height: Int by mutableIntStateOf(180)
    var weight: Int by mutableIntStateOf(80)
    var weightGoal: WeightGoal? by mutableStateOf(WeightGoal.NORMAL)
    var dailyCalorieLimit: Int by mutableIntStateOf(0)
    val bmr: Int
        get() = calculateBMR(isMale == true, weight, height, age).toInt()

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
            dailyCalorieLimit = dailyCalorieLimit,
            weightGoal = weightGoal
        )
    }

    fun canProceed(currentPage: Int): Boolean = when (currentPage) {
        0 -> isMale != null
        1 -> true
        2 -> true
        else -> false
    }


    fun getGoalText(): String {
        val base = calculateBMR(isMale == true, weight, height, age)
        dailyCalorieLimit = (base - (weightGoal?.dailyDeficit ?: 0)).toInt()
        val goalMessage =
            "Du brauchst ein Kaloriendefizit von ${weightGoal?.dailyDeficit} kcal täglich, " +
                    "um ${weightGoal?.kgPerWeek}kg pro Woche abzunehmen. " +
                    "Daraus ergibt sich ein Tagesziel von $dailyCalorieLimit kcal."
        return goalMessage
    }

}