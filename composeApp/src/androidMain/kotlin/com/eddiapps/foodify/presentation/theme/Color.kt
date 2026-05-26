package com.eddiapps.foodify.presentation.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.ui.graphics.Color

val CalorieGreen = Color(0xFF4A9F6F)
val CalorieYellow = Color(0xFFD4A04E)
val CalorieOrange = Color(0xFFC77B5B)

val ProteinColor = Color(0xFF6650A4)
val CarbsColor = Color(0xFFFF7E19)
val FatColor = Color(0xFF004D02)
val SugarColor = Color(0xFFE91E63)

val ColorScheme.proteinColor get() = ProteinColor
val ColorScheme.carbsColor get() = CarbsColor
val ColorScheme.fatColor get() = FatColor
val ColorScheme.sugarColor get() = SugarColor