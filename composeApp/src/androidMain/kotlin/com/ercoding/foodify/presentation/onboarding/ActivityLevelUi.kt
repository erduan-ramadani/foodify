package com.ercoding.foodify.presentation.onboarding

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.DirectionsRun
import androidx.compose.material.icons.automirrored.outlined.DirectionsWalk
import androidx.compose.material.icons.outlined.Chair
import androidx.compose.material.icons.outlined.FitnessCenter
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import com.ercoding.foodify.R

enum class ActivityLevel(val factor: Double) {
    SEDENTARY(1.2),
    LIGHT(1.375),
    ACTIVE(1.55),
    VERY_ACTIVE(1.725)
}

@Composable
fun ActivityLevel.label(): String = when (this) {
    ActivityLevel.SEDENTARY -> stringResource(R.string.activity_sedentary_label)
    ActivityLevel.LIGHT -> stringResource(R.string.activity_light_label)
    ActivityLevel.ACTIVE -> stringResource(R.string.activity_active_label)
    ActivityLevel.VERY_ACTIVE -> stringResource(R.string.activity_very_active_label)
}

@Composable
fun ActivityLevel.description(): String = when (this) {
    ActivityLevel.SEDENTARY -> stringResource(R.string.activity_sedentary_description)
    ActivityLevel.LIGHT -> stringResource(R.string.activity_light_description)
    ActivityLevel.ACTIVE -> stringResource(R.string.activity_active_description)
    ActivityLevel.VERY_ACTIVE -> stringResource(R.string.activity_very_active_description)
}

val ActivityLevel.icon: ImageVector
    get() = when (this) {
        ActivityLevel.SEDENTARY -> Icons.Outlined.Chair
        ActivityLevel.LIGHT -> Icons.AutoMirrored.Outlined.DirectionsWalk
        ActivityLevel.ACTIVE -> Icons.AutoMirrored.Outlined.DirectionsRun
        ActivityLevel.VERY_ACTIVE -> Icons.Outlined.FitnessCenter
    }


