package com.eddiapps.foodify.data.remote.anthropic

import com.eddiapps.foodify.domain.AnthropicInterface
import com.eddiapps.foodify.domain.model.sheet.NutritionEntry
import com.eddiapps.foodify.domain.model.sheet.multiplyByQuantity
import com.google.firebase.appcheck.FirebaseAppCheck
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.tasks.await
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

class AnthropicRepository : AnthropicInterface {

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
        return runCatching {
            val appCheckToken = FirebaseAppCheck.getInstance()
                .getAppCheckToken(false)
                .await()
                .token

            val response = client.post(
                "https://nutrition-3cj75uw5fa-uc.a.run.app"
            ) {
                contentType(ContentType.Application.Json)
                header("X-Firebase-AppCheck", appCheckToken)

                setBody(
                    buildJsonObject {
                        put("query", query)
                        put("weight", weight)
                        put("language", java.util.Locale.getDefault().displayLanguage)
                    }
                )
            }.body<MessageResponse>()

            val responseText = response.content
                .firstOrNull()
                ?.text
                ?: error("Empty response")

            val parsed = json.decodeFromString<NutritionEntry>(
                cleanJsonResponse(responseText)
            )
                .copy(query = query)

            if (parsed.isMeal) {
                parsed.multiplyByQuantity()
            } else {
                parsed
            }
        }
    }

    override suspend fun requestNutritionValuesFromImage(
        base64Image: String,
    ): Result<NutritionEntry> {
        return runCatching {
            val appCheckToken = FirebaseAppCheck.getInstance()
                .getAppCheckToken(false)
                .await()
                .token

            val response = client.post(
                "https://nutrition-3cj75uw5fa-uc.a.run.app"
            ) {
                contentType(ContentType.Application.Json)
                header("X-Firebase-AppCheck", appCheckToken)

                setBody(
                    buildJsonObject {
                        put("image", base64Image)
                        put(
                            "language",
                            java.util.Locale.getDefault().displayLanguage
                        )
                    }
                )
            }.body<MessageResponse>()

            val responseText = response.content
                .firstOrNull()
                ?.text
                ?: error("Empty response")

            json.decodeFromString<NutritionEntry>(
                cleanJsonResponse(responseText)
            )
        }
    }

    private fun cleanJsonResponse(responseText: String): String {
        return responseText
            .trim()
            .removePrefix("```json")
            .removePrefix("```")
            .removeSuffix("```")
            .trim()
    }
}