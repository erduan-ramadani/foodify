package com.ercoding.foodify.presentation.dashboard.daytab.sheet

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.ercoding.foodify.R
import com.ercoding.foodify.domain.model.sheet.NutrientRow
import com.ercoding.foodify.domain.model.sheet.NutrientSection
import com.ercoding.foodify.domain.model.sheet.NutritionEntry

@Composable
fun NutritionEntry.toNutrientSections(): List<NutrientSection> = listOf(
    NutrientSection(
        stringResource(R.string.nutrients_macros),
        listOf(
            NutrientRow(
                stringResource(R.string.nutrient_calories),
                calories,
                "kcal",
                isBold = true
            ),
            NutrientRow(stringResource(R.string.nutrient_carbs), carbohydrates, "g", isBold = true),
            NutrientRow(stringResource(R.string.nutrient_sugar), sugar, "g", isIndented = true),
            NutrientRow(stringResource(R.string.nutrient_fiber), fiber, "g", isIndented = true),
            NutrientRow(stringResource(R.string.nutrient_protein), protein, "g", isBold = true),
            NutrientRow(stringResource(R.string.nutrient_fat), fat, "g", isBold = true),
            NutrientRow(
                stringResource(R.string.nutrient_fat_saturated),
                saturatedFat,
                "g",
                isIndented = true
            ),
            NutrientRow(
                stringResource(R.string.nutrient_fat_unsaturated),
                unsaturatedFat,
                "g",
                isIndented = true
            ),
            NutrientRow(stringResource(R.string.nutrient_salt), salt, "g"),
            NutrientRow(stringResource(R.string.nutrient_cholesterol), cholesterol, "mg"),
        )
    ),
    NutrientSection(
        stringResource(R.string.nutrients_fatty_acids),
        listOf(
            NutrientRow(stringResource(R.string.nutrient_omega3), omega3, "mg"),
            NutrientRow(stringResource(R.string.nutrient_omega6), omega6, "mg"),
        )
    ),
    NutrientSection(
        stringResource(R.string.nutrients_vitamins),
        listOf(
            NutrientRow(stringResource(R.string.nutrient_vitamin_a), vitaminA, "µg"),
            NutrientRow(stringResource(R.string.nutrient_vitamin_b6), vitaminB6, "mg"),
            NutrientRow(stringResource(R.string.nutrient_vitamin_b12), vitaminB12, "µg"),
            NutrientRow(stringResource(R.string.nutrient_vitamin_c), vitaminC, "mg"),
            NutrientRow(stringResource(R.string.nutrient_vitamin_d), vitaminD, "µg"),
            NutrientRow(stringResource(R.string.nutrient_vitamin_e), vitaminE, "mg"),
            NutrientRow(stringResource(R.string.nutrient_vitamin_k), vitaminK, "µg"),
            NutrientRow(stringResource(R.string.nutrient_folic_acid), folicAcid, "µg"),
        )
    ),
    NutrientSection(
        stringResource(R.string.nutrients_minerals),
        listOf(
            NutrientRow(stringResource(R.string.nutrient_calcium), calcium, "mg"),
            NutrientRow(stringResource(R.string.nutrient_iron), iron, "mg"),
            NutrientRow(stringResource(R.string.nutrient_magnesium), magnesium, "mg"),
            NutrientRow(stringResource(R.string.nutrient_zinc), zinc, "mg"),
            NutrientRow(stringResource(R.string.nutrient_potassium), potassium, "mg"),
            NutrientRow(stringResource(R.string.nutrient_phosphorus), phosphorus, "mg"),
        )
    ),
)