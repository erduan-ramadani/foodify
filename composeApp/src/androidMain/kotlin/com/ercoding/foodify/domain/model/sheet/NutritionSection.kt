package com.ercoding.foodify.domain.model.sheet

data class NutrientSection(
    val title: String,
    val items: List<NutrientRow>
)
