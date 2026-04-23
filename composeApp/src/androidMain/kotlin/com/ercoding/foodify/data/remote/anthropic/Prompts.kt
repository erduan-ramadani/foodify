package com.ercoding.foodify.data.remote.anthropic

import java.util.Locale


fun buildNutritionQuery(query: String): String {
    val locale = Locale.getDefault().displayLanguage
    return """
        The input language is $locale. Interpret all food names in this language's context."
        The input can be either a meal or a physical activity.
        Return nutritional values for "$query" as JSON. No text before or after the JSON.
        
        If the input is a MEAL:
        - Calculate values based on standard nutrition tables (USDA).
        - If no amount is specified, assume a typical serving size.
        - Calories must be positive.
        - JSON field isMeal is true.
        
        If the input is an ACTIVITY (e.g. "30min spazieren", "1h Fahrrad", "45min joggen"):
        - Return negative calories based on average burn rate for a 75kg person.
        - Set ALL other nutritional values to 0.
        - Use an appropriate activity emoji.
        - JSON field isMeal is false.
        
        JSON fields:
        isMeal, calories, emoji, protein, fat, saturatedFat, unsaturatedFat, carbohydrates,
        sugar, fiber, salt, cholesterol, sodium, potassium, vitaminA, vitaminB6,
        vitaminB12, vitaminC, vitaminD, vitaminE, vitaminK, folicAcid, calcium,
        iron, magnesium, zinc, phosphorus, selenium, copper, manganese, omega3, omega6
    """.trimIndent()
}