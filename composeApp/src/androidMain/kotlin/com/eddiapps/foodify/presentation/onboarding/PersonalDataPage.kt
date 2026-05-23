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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.eddiapps.foodify.R
import com.eddiapps.foodify.presentation.picker.PickerBottomSheet
import com.eddiapps.foodify.presentation.picker.PickerConfigMapper
import com.eddiapps.foodify.presentation.picker.PickerType

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

    val pickerState by vm.pickerState.collectAsStateWithLifecycle()


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
                value = pickerState.age.toFloat(),
                onValueChange = { vm.onAgePicked(it.toInt()) },
                valueRange = vm.ageRange,
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
        UnitToggle(
            vm.unitSystem,
            onChange = { vm.onUnitSystemChange(it) }
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = vm.displayHeightText,
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
                value = vm.displayWeightText,
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
            val heightTitle = stringResource(R.string.height)
            val config = PickerConfigMapper.create(
                unitSystem = vm.unitSystem,
                type = PickerType.HEIGHT,
                title = heightTitle,
                state = pickerState
            )
            ModalBottomSheet(
                onDismissRequest = { showHeightPicker = false }
            ) {
                PickerBottomSheet(
                    config,
                    onConfirmSingle = { cm ->
                        vm.onHeightPicked(cm, null)
                        showHeightPicker = false
                    },
                    onConfirmDual = { feet, inches ->
                        vm.onHeightPicked(feet, inches)
                        showHeightPicker = false
                    },
                    onDismiss = { showHeightPicker = false }
                )
            }
        }

        if (showWeightPicker) {
            val config = PickerConfigMapper.create(
                unitSystem = vm.unitSystem,
                type = PickerType.WEIGHT,
                title = stringResource(R.string.weight),
                state = pickerState
            )
            ModalBottomSheet(
                onDismissRequest = { showWeightPicker = false }
            ) {
                PickerBottomSheet(
                    config = config,
                    onConfirmSingle = { lbs ->
                        vm.onWeightPicked(lbs, null)
                        showWeightPicker = false
                    },
                    onConfirmDual = { kg, decimal ->
                        vm.onWeightPicked(kg, decimal)
                        showWeightPicker = false
                    },
                    onDismiss = {
                        showWeightPicker = false
                    }
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