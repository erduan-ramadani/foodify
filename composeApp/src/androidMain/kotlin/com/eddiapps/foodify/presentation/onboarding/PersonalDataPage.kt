@file:OptIn(ExperimentalMaterial3Api::class)

package com.eddiapps.foodify.presentation.onboarding

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.eddiapps.foodify.R

@Composable
fun PersonalDataPage(
    vm: OnboardingViewModel,
    currentStep: Int,
    totalSteps: Int
) {

    var showHeightPicker by remember { mutableStateOf(false) }
    var showWeightPicker by remember { mutableStateOf(false) }

    val interactionSourceWeight = remember { MutableInteractionSource() }
    val interactionSourceHeight = remember { MutableInteractionSource() }

    val pickerState by vm.pickerState.collectAsState()


    // Used in OutlinedTextField to open PickerBottomSheet on click
    LaunchedEffect(interactionSourceHeight) {
        interactionSourceHeight.interactions.collect { interaction ->
            if (interaction is PressInteraction.Release) {
                showHeightPicker = true
            }
        }
    }

    // Used in OutlinedTextField to open PickerBottomSheet on click
    LaunchedEffect(interactionSourceWeight) {
        interactionSourceWeight.interactions.collect { interaction ->
            if (interaction is PressInteraction.Release) {
                showWeightPicker = true
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 24.dp)
    ) {
        Text(
            text = stringResource(
                R.string.step_indicator,
                currentStep, totalSteps
            ),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = stringResource(R.string.about_you),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = stringResource(R.string.calculate_calorie_title),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = stringResource(R.string.gender),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            GenderCard(
                label = stringResource(R.string.male),
                icon = "♂",
                isSelected = vm.isMale == true,
                onClick = { vm.isMale = true },
                modifier = Modifier.weight(1f)
            )
            GenderCard(
                label = stringResource(R.string.female),
                icon = "♀",
                isSelected = vm.isMale == false,
                onClick = { vm.isMale = false },
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = stringResource(R.string.age),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "${pickerState.age}",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = stringResource(R.string.years),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Slider(
                value = vm.age.toFloat(),
                onValueChange = { vm.age = it.toInt() },
                valueRange = 10f..99f,
                modifier = Modifier.weight(1f),
                colors = SliderDefaults.colors(
                    thumbColor = MaterialTheme.colorScheme.primary,
                    activeTrackColor = MaterialTheme.colorScheme.primary,
                    inactiveTrackColor = MaterialTheme.colorScheme.surfaceVariant
                )
            )
        }
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            stringResource(R.string.weight_and_height),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = stringResource(R.string.x_cm, vm.height),
                onValueChange = {},
                readOnly = true,
                shape = RoundedCornerShape(12.dp),
                interactionSource = interactionSourceHeight,
                trailingIcon = {
                    Icon(
                        Icons.Default.ChevronRight,
                        null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                modifier = Modifier.weight(1f)

            )
            OutlinedTextField(
                value = stringResource(R.string.x_kg, vm.weight),
                onValueChange = {},
                readOnly = true,
                shape = RoundedCornerShape(12.dp),
                interactionSource = interactionSourceWeight,
                trailingIcon = {
                    Icon(
                        Icons.Default.ChevronRight,
                        null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                modifier = Modifier.weight(1f)
            )
        }
        Spacer(modifier = Modifier.height(20.dp))

        if (showHeightPicker) {
            ModalBottomSheet(onDismissRequest = { showHeightPicker = false }) {
                PickerBottomSheet(
                    text = stringResource(R.string.height),
                    unit = stringResource(R.string.cm),
                    currentValue = vm.height,
                    range = 100..210,
                    onConfirm = {
                        vm.height = it
                        showHeightPicker = false
                    },
                    onDismiss = { showHeightPicker = false }
                )
            }
        }

        if (showWeightPicker) {
            ModalBottomSheet(onDismissRequest = { showWeightPicker = false }) {
                PickerBottomSheet(
                    text = stringResource(R.string.weight),
                    unit = stringResource(R.string.kg),
                    range = 30..200,
                    currentValue = vm.weight,
                    onConfirm = {
                        vm.weight = it
                        showWeightPicker = false
                    },
                    onDismiss = { showWeightPicker = false }
                )
            }
        }
    }
}

@Composable
private fun GenderCard(
    label: String,
    icon: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier,
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
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = icon,
                fontSize = 28.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}