package com.ercoding.foodify.data.remote

import com.ercoding.foodify.domain.AnthropicInterface
import com.ercoding.foodify.domain.NutritionEntry

class FakeAnthropicRepository(private val returnValue: Int = 35) : AnthropicInterface {
    override suspend fun requestNutritionValues(query: String): Result<NutritionEntry> {
        return Result.success(NutritionEntry())
    }
}