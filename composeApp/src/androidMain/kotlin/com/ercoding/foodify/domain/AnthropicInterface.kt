package com.ercoding.foodify.domain

import com.ercoding.foodify.domain.model.sheet.NutritionEntry

interface AnthropicInterface {
    suspend fun requestNutritionValues(query: String): Result<NutritionEntry>
}