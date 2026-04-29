package com.ercoding.foodify.domain.model.sheet

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
    val isMeal: Boolean = true,
    val emoji: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val calories: Double = 0.0,
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