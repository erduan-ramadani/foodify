package com.ercoding.foodify.domain

interface AnthropicInterface {
    suspend fun requestProteinAmount(query: String): Result<String>
}