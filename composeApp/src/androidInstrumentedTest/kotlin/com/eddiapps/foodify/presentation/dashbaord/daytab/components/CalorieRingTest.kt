package com.eddiapps.foodify.presentation.dashbaord.daytab.components

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.eddiapps.foodify.presentation.dashboard.daytab.components.CalorieRing
import org.junit.Rule
import org.junit.Test

class CalorieRingTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun calorieRing_displaysRemainingCalories() {
        composeTestRule.setContent {
            CalorieRing(
                remainingCalories = 1500,
                progress = 0.5f,
                isOverLimit = false
            )
        }

        composeTestRule.onNodeWithText("1500").assertIsDisplayed()
    }

    @Test
    fun calorieRing_showsRemainingLabelWhenUnderLimit() {
        composeTestRule.setContent {
            CalorieRing(
                remainingCalories = 500,
                progress = 0.5f,
                isOverLimit = false
            )
        }

        composeTestRule.onNodeWithText("Remaining").assertIsDisplayed()
    }

    @Test
    fun calorieRing_showsOverLimitLabelWhenOverLimit() {
        composeTestRule.setContent {
            CalorieRing(
                remainingCalories = 200,
                progress = 1.2f,
                isOverLimit = true
            )
        }

        composeTestRule.onNodeWithText("Over limit").assertIsDisplayed()
    }
}