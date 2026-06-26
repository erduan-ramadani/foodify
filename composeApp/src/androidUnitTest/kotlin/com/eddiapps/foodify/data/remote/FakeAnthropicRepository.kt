package com.eddiapps.foodify.data.remote

import com.eddiapps.foodify.domain.AnthropicInterface
import com.eddiapps.foodify.domain.model.sheet.NutritionEntry

class FakeAnthropicRepository(
    private var nextResult: Result<NutritionEntry>
    = Result.success(NutritionEntry(title = "Default"))
) : AnthropicInterface {

    var lastQuery: String? = null
        private set

    fun setNextResult(result: Result<NutritionEntry>) {
        nextResult = result
    }

    override suspend fun requestNutritionValues(
        query: String,
        weight: Double
    ): Result<NutritionEntry> {
        lastQuery = query
        return nextResult
    }

    override suspend fun requestNutritionValuesFromImage(base64Image: String): Result<NutritionEntry> {
        return nextResult
    }
}