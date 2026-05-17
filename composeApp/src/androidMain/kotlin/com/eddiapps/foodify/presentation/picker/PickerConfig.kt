package com.eddiapps.foodify.presentation.picker

sealed class PickerConfig {
    data class SinglePickerConfig(
        val title: String,
        val currentValue: Int,
        val range: IntRange,
        val step: Int = 1,
        val unit: String,
    ) : PickerConfig()

    data class DualPickerConfig(
        val title: String,
        val range: IntRange,
        val decimalRange: IntRange = 0..9,
        val unit: String,
        val initialValue: Int,
        val initialDecimal: Int
    ) : PickerConfig()
}