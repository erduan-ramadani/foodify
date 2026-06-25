package com.eddiapps.foodify.presentation.dashbaord.daytab.components

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.eddiapps.foodify.presentation.dashboard.daytab.components.BalanceCard
import org.junit.Rule
import org.junit.Test

class BalanceCardTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun balanceCard_displaysAllThreeValues() {
        composeTestRule.setContent {
            BalanceCard(
                dailyCaloriesEaten = 1200,
                dailyCaloriesBurned = 300,
                dailyCalorieLimit = 2000
            )
        }

        composeTestRule.onNodeWithText("1200").assertIsDisplayed()
        composeTestRule.onNodeWithText("300").assertIsDisplayed()
        composeTestRule.onNodeWithText("2000").assertIsDisplayed()
    }
}