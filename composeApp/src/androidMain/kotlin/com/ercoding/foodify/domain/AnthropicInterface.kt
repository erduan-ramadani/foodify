package com.ercoding.foodify.domain

interface AnthropicInterface {
    suspend fun requestNutritionValues(query: String): Result<NutritionEntry>
}