package com.ercoding.foodify.presentation.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF7DD3A0),
    onPrimary = Color(0xFF0E0D14),
    primaryContainer = Color(0xFF2A3A30),
    onPrimaryContainer = Color(0xFFF5F1EA),
    background = Color(0xFF0E0D14),
    surface = Color(0xFF1A1820),
    surfaceVariant = Color(0xFF221F2A),
    onBackground = Color(0xFFF5F1EA),
    onSurface = Color(0xFFF5F1EA),
    onSurfaceVariant = Color(0xFF8B8696),
    outline = Color(0xFF2A2733),
    outlineVariant = Color(0xFF3A3744)
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF4A9F6F),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFE8F2EC),
    onPrimaryContainer = Color(0xFF1F1B16),
    background = Color(0xFFFAF8F4),
    surface = Color(0xFFFFFFFF),
    surfaceVariant = Color(0xFFF2EFE9),
    onBackground = Color(0xFF1F1B16),
    onSurface = Color(0xFF1F1B16),
    onSurfaceVariant = Color(0xFF7A6E60),
    outline = Color(0xFFE8E3DA),
    outlineVariant = Color(0xFFD8D2C5)
)

@Composable
fun FoodifyTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}