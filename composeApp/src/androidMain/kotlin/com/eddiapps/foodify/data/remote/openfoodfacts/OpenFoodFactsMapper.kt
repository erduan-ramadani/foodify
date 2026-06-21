package com.eddiapps.foodify.data.remote.openfoodfacts

import com.eddiapps.foodify.domain.model.sheet.NutritionEntry

fun OpenFoodFactsProduct.toNutritionEntry(
    grams: Int,
    timestamp: Long
): NutritionEntry {
    val factor = grams / 100.0
    val n = nutriments

    return NutritionEntry(
        title = productName ?: "Unknown product",
        imagePath = imageUrl ?: imageFrontUrl ?: imageSmallUrl,
        emoji = "🛒",
        isMeal = true,
        isMealDetected = true,
        quantity = 1,
        createdAt = timestamp,
        calories = (n?.caloriesPer100g ?: 0.0) * factor,
        protein = (n?.proteinPer100g ?: 0.0) * factor,
        fat = (n?.fatPer100g ?: 0.0) * factor,
        saturatedFat = (n?.saturatedFatPer100g ?: 0.0) * factor,
        carbohydrates = (n?.carbsPer100g ?: 0.0) * factor,
        sugar = (n?.sugarPer100g ?: 0.0) * factor,
        fiber = (n?.fiberPer100g ?: 0.0) * factor,
        salt = (n?.saltPer100g ?: 0.0) * factor,
        cholesterol = (n?.cholesterolPer100g ?: 0.0) * factor,
        sodium = (n?.sodiumPer100g ?: 0.0) * factor
    )
}