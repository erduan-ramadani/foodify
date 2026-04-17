package com.ercoding.foodify.data.remote

import com.ercoding.foodify.domain.AnthropicInterface
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

    override suspend fun requestProteinAmount(query: String): Result<String> {
        val key = firebaseRepository.fetchAnthropicApiKey()
        val anthropicQueryWithEmoji =
            "Wie viel Gramm Eiweiß enthält folgende Mahlzeit: $query. Wenn die Menge unklar ist, schätze eine typische Portion. Antworte nur mit einer ganzen Zahl in Gramm und einem passenden Emoji getrennt durch |. Beispiel: 31|\uD83C\uDF57"
        return runCatching {
            client.post("https://api.anthropic.com/v1/messages") {
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
                                content = anthropicQueryWithEmoji
                            )
                        )
                    )
                )
            }.body<MessageResponse>()
                .content.firstOrNull()?.text ?: ""
        }
    }
}