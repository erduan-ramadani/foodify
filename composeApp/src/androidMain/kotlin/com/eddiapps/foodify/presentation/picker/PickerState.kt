package com.eddiapps.foodify.presentation.picker

import kotlinx.serialization.Serializable

@Serializable
data class PickerState(
    val age: Int,
    val heightCm: Int,
    val weightKg: Double,
    val heightFt: Int,
    val heightIn: Int,
    val weightLb: Int
)