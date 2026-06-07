package com.eddiapps.foodify.presentation.dashboard.analysistab.components

import com.eddiapps.foodify.R
import com.eddiapps.foodify.domain.model.sheet.NutritionEntry

data class NutrientRow(
    val labelRes: Int,
    val unit: String,
    val selector: (NutritionEntry) -> Double
)

val NUTRIENT_ROWS = listOf(
    // Macros
    NutrientRow(R.string.nutrient_protein, "g") { it.protein },
    NutrientRow(R.string.nutrient_carbs, "g") { it.carbohydrates },
    NutrientRow(R.string.nutrient_fiber, "g") { it.fiber },
    NutrientRow(R.string.nutrient_fat, "g") { it.fat },
    NutrientRow(R.string.nutrient_cholesterol, "mg") { it.cholesterol },

    // Fatty acids
    NutrientRow(R.string.nutrient_omega3, "g") { it.omega3 },
    NutrientRow(R.string.nutrient_omega6, "g") { it.omega6 },

    // Vitamins
    NutrientRow(R.string.nutrient_vitamin_a, "µg") { it.vitaminA },
    NutrientRow(R.string.nutrient_vitamin_b6, "mg") { it.vitaminB6 },
    NutrientRow(R.string.nutrient_vitamin_b12, "µg") { it.vitaminB12 },
    NutrientRow(R.string.nutrient_vitamin_c, "mg") { it.vitaminC },
    NutrientRow(R.string.nutrient_vitamin_d, "µg") { it.vitaminD },
    NutrientRow(R.string.nutrient_vitamin_e, "mg") { it.vitaminE },
    NutrientRow(R.string.nutrient_vitamin_k, "µg") { it.vitaminK },
    NutrientRow(R.string.nutrient_folic_acid, "µg") { it.folicAcid },

    // Minerals
    NutrientRow(R.string.nutrient_calcium, "mg") { it.calcium },
    NutrientRow(R.string.nutrient_iron, "mg") { it.iron },
    NutrientRow(R.string.nutrient_magnesium, "mg") { it.magnesium },
    NutrientRow(R.string.nutrient_zinc, "mg") { it.zinc },
    NutrientRow(R.string.nutrient_potassium, "mg") { it.potassium },
    NutrientRow(R.string.nutrient_phosphorus, "mg") { it.phosphorus },
)
