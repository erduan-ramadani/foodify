package com.eddiapps.foodify.presentation.picker

import com.eddiapps.foodify.domain.model.UnitSystem
import com.eddiapps.foodify.presentation.picker.PickerConfig.DualPickerConfig
import com.eddiapps.foodify.presentation.picker.PickerConfig.SinglePickerConfig

enum class PickerType { HEIGHT, WEIGHT, AGE }

object PickerConfigMapper {

    private val HEIGHT_RANGE_CM: IntRange = 92..241
    private val HEIGHT_RANGE_FEET: IntRange = 3..7
    private val HEIGHT_RANGE_INCHES: IntRange = 0..11
    private val WEIGHT_RANGE_KG: IntRange = 30..202
    private val WEIGHT_RANGE_KG_DECIMAL: IntRange = 0..9
    private val WEIGHT_RANGE_LB: IntRange = 67..447
    private val AGE_RANGE: IntRange = 13..120

    fun create(
        unitSystem: UnitSystem,
        type: PickerType,
        title: String,
        state: PickerState?
    ): PickerConfig {
        if (type == PickerType.AGE) {
            return SinglePickerConfig(
                title = title,
                currentValue = state?.age ?: 36,
                range = AGE_RANGE,
                step = 1,
                unit = "",
            )
        }
        return when (unitSystem) {

            UnitSystem.METRIC -> when (type) {

                PickerType.HEIGHT -> SinglePickerConfig(
                    title = title,
                    currentValue = state?.heightCm ?: 180,
                    range = HEIGHT_RANGE_CM,
                    step = 1,
                    unit = "cm",
                )

                PickerType.WEIGHT -> DualPickerConfig(
                    title = title,
                    range = WEIGHT_RANGE_KG,
                    decimalRange = WEIGHT_RANGE_KG_DECIMAL,
                    unit = "kg",
                    initialValue = state?.weightKg?.toInt() ?: 80,
                    initialDecimal = ((state?.weightKg ?: 80.0) * 10).toInt() % 10
                )
            }

            UnitSystem.IMPERIAL -> when (type) {
                PickerType.HEIGHT -> DualPickerConfig(
                    title = title,
                    range = HEIGHT_RANGE_FEET,
                    decimalRange = HEIGHT_RANGE_INCHES,
                    unit = "ft/in",
                    initialValue = state?.heightFt ?: 4,
                    initialDecimal = state?.heightIn ?: 4
                )

                PickerType.WEIGHT -> SinglePickerConfig(
                    title = title,
                    range = WEIGHT_RANGE_LB,
                    step = 1,
                    unit = "lb",
                    currentValue = state?.weightLb ?: 170,
                )
            }
        }
    }
}