package com.ercoding.foodify.domain.extension

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

fun LocalDate.toDisplayString(): String {
    val today = LocalDate.now()
    val formatter = DateTimeFormatter.ofPattern("d. MMMM", Locale.GERMAN)
    val fullFormatter = DateTimeFormatter.ofPattern("EEEE, d. MMMM", Locale.GERMAN)

    return when (this) {
        today -> "Heute, ${format(formatter)}"
        today.minusDays(1) -> "Gestern, ${format(formatter)}"
        else -> format(fullFormatter)
    }
}