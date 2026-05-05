package com.eddiapps.foodify.data.remote

import com.eddiapps.foodify.domain.AnthropicInterface
import com.eddiapps.foodify.domain.model.sheet.NutritionEntry

class FakeAnthropicRepository(private val returnValue: Int = 35) : AnthropicInterface {
    override suspend fun requestNutritionValues(query: String): Result<NutritionEntry> {
        return Result.success(NutritionEntry())
    }
}