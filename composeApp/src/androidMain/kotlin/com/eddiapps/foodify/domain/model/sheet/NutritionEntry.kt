package com.eddiapps.foodify.domain.model.sheet

import android.os.Build
import androidx.annotation.RequiresApi
import kotlinx.serialization.Serializable
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.UUID

@Serializable
data class NutritionEntry(
    val id: String = UUID.randomUUID().toString(),
    val query: String = "",
    val title: String = "",
    val isMeal: Boolean = true,
    val isMealDetected: Boolean = true,
    val imagePath: String? = null,
    val emoji: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val calories: Double = 0.0,
    val quantity: Int = 1,
    val protein: Double = 0.0,
    val fat: Double = 0.0,
    val saturatedFat: Double = 0.0,
    val unsaturatedFat: Double = 0.0,
    val carbohydrates: Double = 0.0,
    val sugar: Double = 0.0,
    val fiber: Double = 0.0,
    val salt: Double = 0.0,
    val cholesterol: Double = 0.0,
    val sodium: Double = 0.0,
    val potassium: Double = 0.0,
    val vitaminA: Double = 0.0,
    val vitaminB6: Double = 0.0,
    val vitaminB12: Double = 0.0,
    val vitaminC: Double = 0.0,
    val vitaminD: Double = 0.0,
    val vitaminE: Double = 0.0,
    val vitaminK: Double = 0.0,
    val folicAcid: Double = 0.0,
    val calcium: Double = 0.0,
    val iron: Double = 0.0,
    val magnesium: Double = 0.0,
    val zinc: Double = 0.0,
    val phosphorus: Double = 0.0,
    val selenium: Double = 0.0,
    val copper: Double = 0.0,
    val manganese: Double = 0.0,
    val omega3: Double = 0.0,
    val omega6: Double = 0.0
)

val NutritionEntry.formattedTime: String
    @RequiresApi(Build.VERSION_CODES.O)
    get() {
        val instant = Instant.ofEpochMilli(createdAt)
        val time = instant.atZone(ZoneId.systemDefault()).toLocalTime()
        return time.format(DateTimeFormatter.ofPattern("HH:mm"))
    }

fun NutritionEntry.multiplyByQuantity(): NutritionEntry {
    if (quantity <= 1) return this
    val q = quantity
    return copy(
        calories = calories * q,
        protein = protein * q,
        fat = fat * q,
        saturatedFat = saturatedFat * q,
        unsaturatedFat = unsaturatedFat * q,
        carbohydrates = carbohydrates * q,
        sugar = sugar * q,
        fiber = fiber * q,
        salt = salt * q,
        cholesterol = cholesterol * q,
        sodium = sodium * q,
        potassium = potassium * q,
        vitaminA = vitaminA * q,
        vitaminB6 = vitaminB6 * q,
        vitaminB12 = vitaminB12 * q,
        vitaminC = vitaminC * q,
        vitaminD = vitaminD * q,
        vitaminE = vitaminE * q,
        vitaminK = vitaminK * q,
        folicAcid = folicAcid * q,
        calcium = calcium * q,
        iron = iron * q,
        magnesium = magnesium * q,
        zinc = zinc * q,
        phosphorus = phosphorus * q,
        selenium = selenium * q,
        copper = copper * q,
        manganese = manganese * q,
        omega3 = omega3 * q,
        omega6 = omega6 * q
    )
}