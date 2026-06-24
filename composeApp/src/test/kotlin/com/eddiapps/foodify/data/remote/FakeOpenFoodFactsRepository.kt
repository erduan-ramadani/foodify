package com.eddiapps.foodify.data.remote

import com.eddiapps.foodify.data.remote.openfoodfacts.OpenFoodFactsResponse
import com.eddiapps.foodify.domain.OpenFoodFactsInterface

class FakeOpenFoodFactsRepository(
    private var nextResult: Result<OpenFoodFactsResponse> = Result.success(
        OpenFoodFactsResponse(status = 0, product = null)
    )
) : OpenFoodFactsInterface {

    var lastBarcode: String? = null
        private set

    fun setNextResult(result: Result<OpenFoodFactsResponse>) {
        nextResult = result
    }

    override suspend fun getProductByBarcode(barcode: String): Result<OpenFoodFactsResponse> {
        lastBarcode = barcode
        return nextResult
    }
}