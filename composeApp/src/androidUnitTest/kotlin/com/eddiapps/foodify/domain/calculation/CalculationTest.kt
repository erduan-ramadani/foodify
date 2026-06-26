package com.eddiapps.foodify.domain.calculation

import com.eddiapps.foodify.domain.model.onboarding.WeightGoal
import org.junit.Test
import kotlin.test.assertEquals

class CalculationTest {

    @Test
    fun `calculateBMR returns correct value for male`() {
        val bmr = calculateBMR(
            isMale = true,
            weight = 80.0,
            height = 180,
            age = 36
        )
        // BMR = 10*80 + 6.25*180 - 5*36 + 5 = 1750
        assertEquals(1750.0, bmr, 0.0)
    }

    @Test
    fun `calculateBMR returns correct value for female`() {
        val bmr = calculateBMR(
            isMale = false,
            weight = 80.0,
            height = 180,
            age = 36
        )
        // BMR = 10*80 + 6.25*180 - 5*36 -161 = 1584
        assertEquals(1584.0, bmr, 0.0)
    }

    @Test
    fun `calculateBMR returns lower value for older person`() {
        val youngBMR = calculateBMR(
            isMale = true,
            weight = 80.0,
            height = 180,
            age = 25
        )

        val oldBMR = calculateBMR(
            isMale = true,
            weight = 80.0,
            height = 180,
            age = 85
        )
        assert(oldBMR < youngBMR)
    }

    @Test
    fun `calculateBMR returns higher value for heavier person`() {
        val lightBMR = calculateBMR(
            isMale = true,
            weight = 80.0,
            height = 180,
            age = 25
        )
        val heavyBMR = calculateBMR(
            isMale = true,
            weight = 100.0,
            height = 180,
            age = 25
        )
        assert(heavyBMR > lightBMR)
    }

    @Test
    fun `calculateDailyCalorieLimit applies activity limit before deficit`() {
        val limit = calculateDailyCalorieLimit(
            isMale = true,
            age = 36,
            weight = 80.0,
            height = 180,
            weightGoal = WeightGoal.NORMAL  // 0.5 kg/Woche = 550 kcal Defizit
        )
        // BMR = 1750
        // TDEE (sedentary, 1.2) = 2100
        // Limit = 2100 - 550 = 1550
        assertEquals(1550, limit)
    }

    @Test
    fun `calculateDailyCalorieLimit decreases with aggressive goal`() {
        val normalLimit = calculateDailyCalorieLimit(
            isMale = true,
            age = 36,
            weight = 80.0,
            height = 180,
            weightGoal = WeightGoal.NORMAL
        )
        val aggressiveLimit = calculateDailyCalorieLimit(
            isMale = true,
            age = 36,
            weight = 80.0,
            height = 180,
            weightGoal = WeightGoal.AGGRESSIVE
        )
        assert(aggressiveLimit < normalLimit)
    }

    @Test
    fun `calculateDailyCalorieLimit returns same value when called multiple times`() {
        val firstTime = calculateDailyCalorieLimit(
            isMale = true,
            age = 36,
            weight = 80.0,
            height = 180,
            weightGoal = WeightGoal.NORMAL
        )
        val secondTime = calculateDailyCalorieLimit(
            isMale = true,
            age = 36,
            weight = 80.0,
            height = 180,
            weightGoal = WeightGoal.NORMAL
        )
        assertEquals(firstTime, secondTime)
    }
}