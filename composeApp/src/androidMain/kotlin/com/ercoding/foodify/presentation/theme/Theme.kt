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
    secondary = PurpleGrey80,
    tertiary = Pink80,
    background = Color(0xFF0E0D14),
    surface = Color(0xFF1A1820),
    surfaceVariant = Color(0xFF221F2A),
    onBackground = Color(0xFFF5F1EA),
    onSurface = Color(0xFFF5F1EA),
    onSurfaceVariant = Color(0xFF8B8696),
    outline = Color(0xFF2A2733),

    )

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF4A9F6F),
    secondary = PurpleGrey40,
    tertiary = Pink40,
    background = Color(0xFFFAF8F4),
    surface = Color(0xFFFFFFFF),
    surfaceVariant = Color(0xFFF2EFE9),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1F1B16),
    onSurface = Color(0xFF1F1B16),
    outline = Color(0xFFE8E3DA),
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