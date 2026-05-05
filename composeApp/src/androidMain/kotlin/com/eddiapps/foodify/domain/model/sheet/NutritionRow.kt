package com.eddiapps.foodify.domain.model.sheet

data class NutrientRow(
    val label: String,
    val value: Double,
    val unit: String,
    val isIndented: Boolean = false,
    val isBold: Boolean = false
)
