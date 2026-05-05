package com.eddiapps.foodify.presentation.onboarding

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.eddiapps.foodify.R
import com.eddiapps.foodify.domain.model.onboarding.WeightGoal

@Composable
fun WeightGoal.label(): String = when (this) {
    WeightGoal.SLOW -> stringResource(R.string.weight_goal_slow)
    WeightGoal.NORMAL -> stringResource(R.string.weight_goal_normal)
    WeightGoal.FAST -> stringResource(R.string.weight_goal_fast)
    WeightGoal.AGGRESSIVE -> stringResource(R.string.weight_goal_aggressive)
}

@Composable
fun WeightGoal.warning(): String? = when (this) {
    WeightGoal.FAST -> stringResource(R.string.weight_goal_fast_warning)
    WeightGoal.AGGRESSIVE -> stringResource(R.string.weight_goal_aggressive_warning)
    else -> null
}