package com.eddiapps.foodify.presentation.onboarding

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.eddiapps.foodify.R

@Composable
fun BMICard(
    bmi: Double,
    bmiCategory: String,
    idealWeightMin: Int,
    idealWeightMax: Int,
    weightUnit: String,
    modifier: Modifier = Modifier
) {
    val categoryColor = when {
        bmi < 18.5 -> Color(0xFF5A87B5)   // Untergewicht - blau
        bmi < 25.0 -> Color(0xFF4A9F6F)   // Normal - grün
        bmi < 30.0 -> Color(0xFFD4A04E)   // Erhöht - gelb
        else -> Color(0xFFC77B5B)         // Adipositas - orange
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = stringResource(R.string.bmi),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(4.dp))

            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = String.format(Locale.getDefault(), "%.1f", bmi),
                    style = MaterialTheme.typography.displaySmall,
                    fontWeight = FontWeight.Light,
                    color = MaterialTheme.colorScheme.onSurface,
                    letterSpacing = (-1).sp
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = bmiCategory,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = categoryColor,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // BMI-Skala mit Marker
            BMIScale(bmi = bmi, categoryColor = categoryColor)

            Spacer(modifier = Modifier.height(20.dp))

            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.recommended_weight),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "$idealWeightMin – $idealWeightMax $weightUnit",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@Composable
private fun BMIScale(bmi: Double, categoryColor: Color) {
    val minBmi = 15f
    val maxBmi = 35f
    val markerPosition = ((bmi.toFloat() - minBmi) / (maxBmi - minBmi)).coerceIn(0f, 1f)

    Column {
        // Skala mit gemischten Farben pro Bereich
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp))
        ) {
            Row(modifier = Modifier.fillMaxSize()) {
                Box(
                    modifier = Modifier
                        .weight(((18.5f - minBmi) / (maxBmi - minBmi)))
                        .fillMaxHeight()
                        .background(Color(0xFF5A87B5).copy(alpha = 0.4f))
                )
                Box(
                    modifier = Modifier
                        .weight(((25f - 18.5f) / (maxBmi - minBmi)))
                        .fillMaxHeight()
                        .background(Color(0xFF4A9F6F).copy(alpha = 0.4f))
                )
                Box(
                    modifier = Modifier
                        .weight(((30f - 25f) / (maxBmi - minBmi)))
                        .fillMaxHeight()
                        .background(Color(0xFFD4A04E).copy(alpha = 0.4f))
                )
                Box(
                    modifier = Modifier
                        .weight(((maxBmi - 30f) / (maxBmi - minBmi)))
                        .fillMaxHeight()
                        .background(Color(0xFFC77B5B).copy(alpha = 0.4f))
                )
            }

            // Marker
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(4.dp)
                        .offset(x = with(LocalDensity.current) {
                            (markerPosition * 1000).dp - 2.dp
                        })
                        .background(categoryColor)
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Skala-Beschriftung
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                "18.5",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                "25",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                "30",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
