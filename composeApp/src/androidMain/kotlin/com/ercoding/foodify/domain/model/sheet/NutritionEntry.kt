package com.ercoding.foodify.domain.model.sheet

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

fun NutritionEntry.toNutrientSections(): List<NutrientSection> = listOf(
    NutrientSection(
        "Makronährwerte",
        listOf(
            NutrientRow("Kalorien", calories, "kcal", isBold = true),
            NutrientRow("Kohlenhydrate", carbohydrates, "g", isBold = true),
            NutrientRow("davon Zucker", sugar, "g", isIndented = true),
            NutrientRow("Ballaststoffe", fiber, "g", isIndented = true),
            NutrientRow("Protein", protein, "g", isBold = true),
            NutrientRow("Fett", fat, "g", isBold = true),
            NutrientRow("gesättigt", saturatedFat, "g", isIndented = true),
            NutrientRow("ungesättigt", unsaturatedFat, "g", isIndented = true),
            NutrientRow("Salz", salt, "g"),
            NutrientRow("Cholesterin", cholesterol, "mg"),
        )
    ),
    NutrientSection(
        "Fettsäuren",
        listOf(
            NutrientRow("Omega-3", omega3, "mg"),
            NutrientRow("Omega-6", omega6, "mg"),
        )
    ),
    NutrientSection(
        "Vitamine",
        listOf(
            NutrientRow("Vitamin A", vitaminA, "µg"),
            NutrientRow("Vitamin B6", vitaminB6, "mg"),
            NutrientRow("Vitamin B12", vitaminB12, "µg"),
            NutrientRow("Vitamin C", vitaminC, "mg"),
            NutrientRow("Vitamin D", vitaminD, "µg"),
            NutrientRow("Vitamin E", vitaminE, "mg"),
            NutrientRow("Vitamin K", vitaminK, "µg"),
            NutrientRow("Folsäure", folicAcid, "µg"),
        )
    ),
    NutrientSection(
        "Mineralstoffe",
        listOf(
            NutrientRow("Kalzium", calcium, "mg"),
            NutrientRow("Eisen", iron, "mg"),
            NutrientRow("Magnesium", magnesium, "mg"),
            NutrientRow("Zink", zinc, "mg"),
            NutrientRow("Kalium", potassium, "mg"),
            NutrientRow("Phosphor", phosphorus, "mg"),
        )
    ),
)

val NutritionEntry.formattedTime: String
    get() {
        val instant = Instant.ofEpochMilli(createdAt)
        val time = instant.atZone(ZoneId.systemDefault()).toLocalTime()
        return time.format(DateTimeFormatter.ofPattern("HH:mm"))
    }