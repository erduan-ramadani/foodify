package com.ercoding.foodify.presentation.onboarding

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlin.math.pow
import kotlin.math.roundToInt

@Composable
fun ActivityPage(vm: OnboardingViewModel) {

    val showBMI = vm.activityLevel != null


    val (minBmi, maxBmi) = when (vm.activityLevel) {
        ActivityLevel.SEDENTARY -> 22.0 to 24.0
        ActivityLevel.LIGHT -> 23.0 to 25.0
        ActivityLevel.ACTIVE -> 23.0 to 26.0
        ActivityLevel.VERY_ACTIVE -> 24.0 to 27.0
        null -> 23.0 to 25.0  // Default
    }

    val idealWeightMin = (minBmi * (vm.height / 100.0).pow(2)).roundToInt()
    val idealWeightMax = (maxBmi * (vm.height / 100.0).pow(2)).roundToInt()

    val bmi = vm.weight / ((vm.height / 100.0) * (vm.height / 100.0))
    val bmiCategory = when {
        bmi < 18.5 -> "Untergewicht"
        bmi < 25.0 -> "Normalgewicht"
        bmi < 27.0 -> "Leicht erhöht"
        bmi < 30.0 -> "Übergewicht"
        else -> "Adipositas"
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 24.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "Schritt 2 von 3",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Dein Aktivitätslevel",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(6.dp))
        ActivityLevel.entries.forEach { level ->
            ActivityLevelCard(
                level = level,
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
                idealWeightMin = idealWeightMin,
                idealWeightMax = idealWeightMax
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
private fun ActivityLevelCard(
    level: ActivityLevel,
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
                imageVector = level.icon,
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
                    text = level.label,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = level.description,
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