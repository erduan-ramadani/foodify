package com.eddiapps.foodify.domain

import com.eddiapps.foodify.data.remote.openfoodfacts.OpenFoodFactsResponse

interface OpenFoodFactsInterface {
    suspend fun getProductByBarcode(barcode: String): Result<OpenFoodFactsResponse>
}