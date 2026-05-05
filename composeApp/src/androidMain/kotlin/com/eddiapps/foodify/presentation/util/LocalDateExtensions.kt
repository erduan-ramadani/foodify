package com.eddiapps.foodify.presentation.util

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.eddiapps.foodify.R
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun LocalDate.toDisplayString(): String {
    val today = LocalDate.now()
    return when (this) {
        today -> stringResource(R.string.today)
        today.minusDays(1) -> stringResource(R.string.yesterday)
        else -> {
            val formatter = DateTimeFormatter.ofPattern("d. MMMM", Locale.getDefault())
            format(formatter)
        }
    }
}