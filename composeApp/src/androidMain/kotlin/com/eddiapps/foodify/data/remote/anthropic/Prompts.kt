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
        - Use a standard, average serving size for ONE unit, regardless of quantity.
        - Example: 1 Döner = always ~550 kcal, whether the input is "1 Döner" or "10 Döner".
        - DO NOT change the per-unit values based on quantity.
        - For ambiguous items, use the simplest preparation:
        - "1 egg" = 1 medium boiled egg (~75 kcal), not fried with oil
        - "1 toast" = 1 slice of plain bread, not buttered
        - When unsure, choose the lower-calorie standard preparation
        - Extract quantity from input, including written numbers 
            (e.g. "vier" = 4, "ein/eine" = 1, "zwei" = 2, "ten" = 10).
        - If no quantity is found, set quantity=1.
        - If the quantity is >1 add it to the title
            (e.g. quantity = 2, title = Äpfel, title should be: 2 Äpfel)

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
        title, isMeal, isMealDetected, calories, quantity, emoji, protein, fat, saturatedFat, unsaturatedFat, carbohydrates,
        sugar, fiber, salt, cholesterol, sodium, potassium, vitaminA, vitaminB6,
        vitaminB12, vitaminC, vitaminD, vitaminE, vitaminK, folicAcid, calcium,
        iron, magnesium, zinc, phosphorus, selenium, copper, manganese, omega3, omega6
    """.trimIndent()
}

fun buildImageNutritionQuery(): String {
    val locale = Locale.getDefault().displayLanguage
    return """
        Analyze the food in this image. Output language context: $locale.
        Identify all food items and estimate portion sizes visually.
        Return nutritional values as JSON. No text before or after the JSON.
        
        - Estimate the total values for the entire meal shown.
        - If multiple items are visible, sum them up.
        - Calories must be positive.
        - isMeal is true, quantity is 1.
        - If you dont recognize a meal set isMealDetected to false
        
        JSON fields:
        title, isMeal, isMealDetected, calories, quantity, emoji, protein, fat, saturatedFat, unsaturatedFat, carbohydrates,
        sugar, fiber, salt, cholesterol, sodium, potassium, vitaminA, vitaminB6,
        vitaminB12, vitaminC, vitaminD, vitaminE, vitaminK, folicAcid, calcium,
        iron, magnesium, zinc, phosphorus, selenium, copper, manganese, omega3, omega6
    """.trimIndent()
}