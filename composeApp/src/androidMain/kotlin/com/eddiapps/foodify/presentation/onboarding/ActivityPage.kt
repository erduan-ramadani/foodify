package com.eddiapps.foodify.presentation.onboarding

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.eddiapps.foodify.R

@Composable
fun ActivityPage(
    vm: OnboardingViewModel,
    currentStep: Int,
    totalSteps: Int
) {
    val pickerState by vm.pickerState.collectAsState()

    val showBMI = vm.activityLevel != null

    val bmi =
        pickerState.weightKg /
                ((pickerState.heightCm / 100.0) *
                        (pickerState.heightCm / 100.0))
    val bmiCategory = when {
        bmi < 18.5 -> stringResource(R.string.bmi_low)
        bmi < 25.0 -> stringResource(R.string.bmi_normal)
        bmi < 27.0 -> stringResource(R.string.bmi_lightly_increased)
        bmi < 30.0 -> stringResource(R.string.bmi_high)
        else -> "Adipositas"
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 24.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = stringResource(
                R.string.step_indicator,
                currentStep, totalSteps
            ), style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = stringResource(R.string.your_activity_level),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(6.dp))
        ActivityLevel.entries.forEach { level ->
            ActivityLevelCard(
                activityLevel = level,
                isSelected = vm.activityLevel == level,
                onClick = {
                    vm.activityLevel = level
                }
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
        Spacer(modifier = Modifier.height(20.dp))

        if (showBMI) {
            BMICard(
                bmi = bmi,
                bmiCategory = bmiCategory,
                idealWeightMin = vm.idealWeightMin,
                idealWeightMax = vm.idealWeightMax,
                vm.weightUnit
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
private fun ActivityLevelCard(
    activityLevel: ActivityLevel,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected)
                MaterialTheme.colorScheme.primaryContainer
            else
                MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(
            width = 1.5.dp,
            color = if (isSelected)
                MaterialTheme.colorScheme.primary
            else
                MaterialTheme.colorScheme.outlineVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = activityLevel.icon,
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = if (isSelected)
                    MaterialTheme.colorScheme.primary
                else
                    MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.width(6.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = activityLevel.label(),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = activityLevel.description(),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}