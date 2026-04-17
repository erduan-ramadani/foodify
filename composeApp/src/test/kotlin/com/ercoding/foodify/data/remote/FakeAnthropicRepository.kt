package com.ercoding.foodify.data.remote

import com.ercoding.foodify.domain.AnthropicInterface

class FakeAnthropicRepository(private val returnValue: Int = 35) : AnthropicInterface {
    override suspend fun requestProteinAmount(query: String): Result<Int> {
        return Result.success(returnValue)
    }
}