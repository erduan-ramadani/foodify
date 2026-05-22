package com.eddiapps.foodify.data.remote.anthropic

import com.eddiapps.foodify.data.remote.firebase.FirebaseRepository
import com.eddiapps.foodify.domain.AnthropicInterface
import com.eddiapps.foodify.domain.model.sheet.NutritionEntry
import com.eddiapps.foodify.domain.model.sheet.multiplyByQuantity
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

class AnthropicRepository(
    private val firebaseRepository: FirebaseRepository
) : AnthropicInterface {

    private val json = Json { ignoreUnknownKeys = true }

    private val client = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                encodeDefaults = true
            })
        }
    }

    override suspend fun requestNutritionValues(
        query: String,
        weight: Double
    ): Result<NutritionEntry> {
        val key = firebaseRepository.fetchAnthropicApiKey()
        return runCatching {
            val response = client.post("https://api.anthropic.com/v1/messages") {
                contentType(ContentType.Application.Json)
                header("x-api-key", key)
                header("anthropic-version", "2023-06-01")
                setBody(
                    MessageRequest(
                        model = "claude-haiku-4-5",
                        max_tokens = 1024,
                        temperature = 0.0,
                        messages = listOf(
                            Message(
                                role = "user",
                                content = listOf(
                                    ContentBlock.Text(
                                        text = buildNutritionQuery(query, weight)
                                    )
                                )
                            )
                        )
                    )
                )
            }.body<MessageResponse>()
                .content.firstOrNull()?.text ?: ""
            val cleaned = "{${response.substringAfter("{").substringBeforeLast("}")}}"
            val json = Json { ignoreUnknownKeys = true }
            val parsed = json.decodeFromString<NutritionEntry>(cleaned).copy(query = query)
            val entry = if (parsed.isMeal) parsed.multiplyByQuantity() else parsed
            entry
        }
    }

    override suspend fun requestNutritionValuesFromImage(
        base64Image: String,
    ): Result<NutritionEntry> {
        val key = firebaseRepository.fetchAnthropicApiKey()
        return runCatching {
            val httpResponse = client.post("https://api.anthropic.com/v1/messages") {
                contentType(ContentType.Application.Json)
                header("x-api-key", key)
                header("anthropic-version", "2023-06-01")
                setBody(
                    MessageRequest(
                        model = "claude-sonnet-4-6",
                        max_tokens = 1024,
                        temperature = 0.0,
                        messages = listOf(
                            Message(
                                role = "user",
                                content = listOf(
                                    ContentBlock.Image(source = ImageSource(data = base64Image)),
                                    ContentBlock.Text(text = buildImageNutritionQuery())
                                )
                            )
                        )
                    )
                )
            }
            val rawBody = httpResponse.bodyAsText()
            val messageResponse = json.decodeFromString<MessageResponse>(rawBody)
            val response = messageResponse.content.firstOrNull()?.text ?: ""

            val cleaned = "{${response.substringAfter("{").substringBeforeLast("}")}}"
            json.decodeFromString<NutritionEntry>(cleaned)
        }
    }
}