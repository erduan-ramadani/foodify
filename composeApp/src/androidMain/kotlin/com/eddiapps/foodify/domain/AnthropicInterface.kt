package com.eddiapps.foodify.domain

import com.eddiapps.foodify.domain.model.sheet.NutritionEntry

interface AnthropicInterface {
    suspend fun requestNutritionValues(query: String, weight: Double): Result<NutritionEntry>
}