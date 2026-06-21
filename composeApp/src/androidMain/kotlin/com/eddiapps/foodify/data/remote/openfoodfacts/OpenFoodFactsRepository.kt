package com.eddiapps.foodify.data.remote.openfoodfacts

import com.eddiapps.foodify.domain.OpenFoodFactsInterface
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

class OpenFoodFactsRepository : OpenFoodFactsInterface {

    private val client = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                coerceInputValues = true
            })
        }
    }

    override suspend fun getProductByBarcode(barcode: String): Result<OpenFoodFactsResponse> {
        return try {
            val response: OpenFoodFactsResponse = client.get(
                "https://world.openfoodfacts.org/api/v2/product/$barcode.json"
            ).body()
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}