package com.eddiapps.foodify.presentation.dashboard.daytab.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.eddiapps.foodify.R
import com.eddiapps.foodify.presentation.dashboard.DashboardViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CalorieRing(
    viewModel: DashboardViewModel
) {
    val isOverLimit = viewModel.dailyCalories > viewModel.dailyCalorieLimit

    val ringColor = when {
        viewModel.progress > 1f -> Color(0xFFC77B5B)   // Über Limit - Orange
        viewModel.progress > 0.85f -> Color(0xFFD4A04E) // Knapp - Gelb
        else -> Color(0xFF4A9F6F)             // OK - Grün
    }
    val backgroundColor = MaterialTheme.colorScheme.outline

    val animatedCalories by animateIntAsState(
        targetValue = viewModel.remainingDailyCaloriesLimit,
        animationSpec = tween(durationMillis = 2000),
        label = "calorieAnimation"
    )

    val animatedProgress by animateFloatAsState(
        targetValue = viewModel.progress,
        animationSpec = tween(durationMillis = 4000),
        label = "calorieAnimation"
    )

    Box(
        modifier = Modifier.size(200.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .padding(2.dp)
        ) {
            val strokeWidth = 5.dp.toPx()
            val arcSize = Size(size.width - strokeWidth, size.height - strokeWidth)
            val topLeft = Offset(strokeWidth / 2, strokeWidth / 2)

            // Hintergrund-Bogen
            drawArc(
                color = backgroundColor,
                startAngle = 135f,
                sweepAngle = 270f,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )

            // Progress-Bogen
            drawArc(
                color = ringColor,
                startAngle = 135f,
                sweepAngle = 270f * animatedProgress.coerceIn(0f, 1f),
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = if (isOverLimit) stringResource(R.string.over_limit) else stringResource(R.string.left_over),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = animatedCalories.toString(),
                fontSize = 48.sp,
                fontWeight = FontWeight.Light,
                color = MaterialTheme.colorScheme.onSurface,
                letterSpacing = (-1.5).sp,
                lineHeight = 48.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = stringResource(R.string.kcal),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}