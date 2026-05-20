package com.eddiapps.foodify.data.remote.anthropic

import java.util.Locale


fun buildNutritionQuery(query: String, userWeightKg: Double): String {
    val locale = Locale.getDefault().displayLanguage
    return """
        The input language is $locale. Interpret all food names in this language's context.
        The input can be either a meal or a physical activity.
        Return nutritional values for "$query" as JSON. No text before or after the JSON.

        If the input is a MEAL:
        - Extract the quantity from the input (e.g. "5 Dönerkebap" → quantity=5, item=Dönerkebap).
        - If no quantity is specified, set quantity=1 and assume a typical serving size.
        - Calculate nutritional values for ONE unit only (e.g. 1 Dönerkebap).
        - The app will multiply these values by quantity automatically.
        - Calories must be positive.
        - JSON field isMeal is true.

        If the input is an ACTIVITY (e.g. "30min walking", "1h cycling", "45min jogging"):
        - Calculate calories burned using MET values for a person weighing $userWeightKg kg.
        - Return positive calories (the value will be subtracted from intake by the app).
        - Set ALL other nutritional values to 0.
        - Use an appropriate activity emoji.
        - JSON field isMeal is false.
        
        Examples for a ${userWeightKg}kg person:
        - "10km cycling" (≈30min moderate) ≈ ${(userWeightKg * 8 * 0.5).toInt()} kcal
        - "5km running" (≈30min) ≈ ${(userWeightKg * 9.8 * 0.5).toInt()} kcal
        - "10000 steps" ≈ ${(userWeightKg * 4)} kcal

        JSON fields:
        isMeal, calories, quantity, emoji, protein, fat, saturatedFat, unsaturatedFat, carbohydrates,
        sugar, fiber, salt, cholesterol, sodium, potassium, vitaminA, vitaminB6,
        vitaminB12, vitaminC, vitaminD, vitaminE, vitaminK, folicAcid, calcium,
        iron, magnesium, zinc, phosphorus, selenium, copper, manganese, omega3, omega6
    """.trimIndent()
}