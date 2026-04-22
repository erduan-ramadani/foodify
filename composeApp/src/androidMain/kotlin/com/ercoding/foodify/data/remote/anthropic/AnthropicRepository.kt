package com.ercoding.foodify.data.remote.anthropic

import com.ercoding.foodify.data.remote.firebase.FirebaseRepository
import com.ercoding.foodify.domain.AnthropicInterface
import com.ercoding.foodify.domain.model.sheet.NutritionEntry
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

class AnthropicRepository(
    private val firebaseRepository: FirebaseRepository
) : AnthropicInterface {

    private val client = HttpClient {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }
    }

    override suspend fun requestNutritionValues(query: String): Result<NutritionEntry> {
        val key = firebaseRepository.fetchAnthropicApiKey()
        return runCatching {
            val response = client.post("https://api.anthropic.com/v1/messages") {
                contentType(ContentType.Application.Json)
                header("x-api-key", key)
                header("anthropic-version", "2023-06-01")
                setBody(
                    MessageRequest(
                        model = "claude-sonnet-4-6",
                        max_tokens = 1024,
                        messages = listOf(
                            Message(
                                role = "user",
                                content = buildNutritionQuery(query)
                            )
                        )
                    )
                )
            }.body<MessageResponse>()
                .content.firstOrNull()?.text ?: ""
            val cleaned = "{${response.substringAfter("{").substringBeforeLast("}")}}"
            val json = Json { ignoreUnknownKeys = true }
            json.decodeFromString<NutritionEntry>(cleaned).copy(meal = query)
        }
    }
}