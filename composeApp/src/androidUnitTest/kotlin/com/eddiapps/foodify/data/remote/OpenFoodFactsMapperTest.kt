package com.eddiapps.foodify.data.remote

import com.eddiapps.foodify.data.remote.openfoodfacts.OpenFoodFactsNutriments
import com.eddiapps.foodify.data.remote.openfoodfacts.OpenFoodFactsProduct
import com.eddiapps.foodify.data.remote.openfoodfacts.toNutritionEntry
import org.junit.Assert.assertEquals
import org.junit.Test

class OpenFoodFactsMapperTest {
    @Test
    fun `100g product returns exact nutriments values`() {
        val product = OpenFoodFactsProduct(
            productName = "Nutella",
            nutriments = OpenFoodFactsNutriments(
                caloriesPer100g = 539.0,
                proteinPer100g = 6.3,
                carbsPer100g = 57.5,
                fatPer100g = 30.9
            )
        )

        val entry = product.toNutritionEntry(grams = 100, timestamp = 0L)

        assertEquals(539.0, entry.calories, 0.01)
        assertEquals(6.3, entry.protein, 0.01)
        assertEquals(57.5, entry.carbohydrates, 0.01)
        assertEquals(30.9, entry.fat, 0.01)
    }

    @Test
    fun `50g product returns halved nutriments values`() {
        val product = OpenFoodFactsProduct(
            productName = "Nutella",
            nutriments = OpenFoodFactsNutriments(
                caloriesPer100g = 539.0,
                proteinPer100g = 6.3
            )
        )

        val entry = product.toNutritionEntry(grams = 50, timestamp = 0L)

        assertEquals(269.5, entry.calories, 0.01)
        assertEquals(3.15, entry.protein, 0.01)
    }

    @Test
    fun `200g product returns doubled nutriments values`() {
        val product = OpenFoodFactsProduct(
            productName = "Nutella",
            nutriments = OpenFoodFactsNutriments(
                caloriesPer100g = 539.0
            )
        )

        val entry = product.toNutritionEntry(grams = 200, timestamp = 0L)

        assertEquals(1078.0, entry.calories, 0.01)
    }

    @Test
    fun `null nutriments returns zero values`() {
        val product = OpenFoodFactsProduct(
            productName = "Empty product",
            nutriments = null
        )

        val entry = product.toNutritionEntry(grams = 100, timestamp = 0L)

        assertEquals(0.0, entry.calories, 0.01)
        assertEquals(0.0, entry.protein, 0.01)
    }

    @Test
    fun `null productName falls back to default`() {
        val product = OpenFoodFactsProduct(productName = null)

        val entry = product.toNutritionEntry(grams = 100, timestamp = 0L)

        assertEquals("Unknown product", entry.title)
    }
}