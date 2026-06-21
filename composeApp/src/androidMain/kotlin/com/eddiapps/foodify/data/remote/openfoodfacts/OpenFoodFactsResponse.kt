package com.eddiapps.foodify.data.remote.openfoodfacts

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OpenFoodFactsResponse(
    val status: Int,
    val product: OpenFoodFactsProduct? = null
)

@Serializable
data class OpenFoodFactsProduct(
    @SerialName("product_name") val productName: String? = null,
    val brands: String? = null,
    @SerialName("image_url") val imageUrl: String? = null,
    @SerialName("image_front_url") val imageFrontUrl: String? = null,
    @SerialName("image_small_url") val imageSmallUrl: String? = null,
    val nutriments: OpenFoodFactsNutriments? = null
)

@Serializable
data class OpenFoodFactsNutriments(
    @SerialName("energy-kcal_100g") val caloriesPer100g: Double? = null,
    @SerialName("proteins_100g") val proteinPer100g: Double? = null,
    @SerialName("carbohydrates_100g") val carbsPer100g: Double? = null,
    @SerialName("sugars_100g") val sugarPer100g: Double? = null,
    @SerialName("fat_100g") val fatPer100g: Double? = null,
    @SerialName("saturated-fat_100g") val saturatedFatPer100g: Double? = null,
    @SerialName("fiber_100g") val fiberPer100g: Double? = null,
    @SerialName("salt_100g") val saltPer100g: Double? = null,
    @SerialName("cholesterol_100g") val cholesterolPer100g: Double? = null,
    @SerialName("sodium_100g") val sodiumPer100g: Double? = null
)