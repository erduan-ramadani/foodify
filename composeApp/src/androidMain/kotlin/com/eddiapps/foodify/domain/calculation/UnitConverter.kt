package com.eddiapps.foodify.domain.calculation

import kotlin.math.roundToInt

object UnitConverter {
    fun convertFeetInchesToCm(feet: Int, inches: Int): Int {
        return ((feet * 12 + inches) * 2.54).roundToInt()
    }

    fun convertCmToFeetInches(cm: Int): Pair<Int, Int> {
        val totalInches = cm / 2.54
        val feet = (totalInches / 12).toInt()
        val inches = (totalInches % 12).roundToInt()
        return Pair(feet, inches)
    }

    fun convertKgToLb(kg: Double): Int {
        val lbs = kg * 2.20462
//        return (lbs * 10).roundToInt() / 10.0
        return lbs.roundToInt()
    }

    fun convertLbToKg(lb: Int): Double {
        return (lb / 2.20462)
    }

    fun toDecimal(intPart: Int, decimalPart: Int?): Double {
        return intPart + (decimalPart ?: 0) / 10.0
    }
}