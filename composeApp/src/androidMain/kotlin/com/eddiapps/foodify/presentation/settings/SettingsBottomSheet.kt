package com.eddiapps.foodify.presentation.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.eddiapps.foodify.R
import com.eddiapps.foodify.domain.model.UnitSystem
import com.eddiapps.foodify.domain.model.onboarding.OnboardingData
import com.eddiapps.foodify.domain.model.onboarding.WeightGoal
import com.eddiapps.foodify.presentation.onboarding.WeightGoalSelector
import com.eddiapps.foodify.presentation.picker.PickerBottomSheet
import com.eddiapps.foodify.presentation.picker.PickerConfigMapper
import com.eddiapps.foodify.presentation.picker.PickerState
import com.eddiapps.foodify.presentation.picker.PickerType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsBottomSheet(
    unitSystem: UnitSystem,
    editingField: SettingsField,
    pickerState: PickerState?,
    onboardingData: OnboardingData?,
    onConfirmSinglePicker: (SettingsField, Int) -> Unit,
    onConfirmDualPicker: (SettingsField, Int, Int) -> Unit,
    onSaveWeightGoal: (WeightGoal) -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 24.dp)
                .padding(bottom = 40.dp)
        ) {
            when (editingField) {
                SettingsField.AGE -> {
                    val config = PickerConfigMapper.create(
                        unitSystem,
                        PickerType.AGE,
                        stringResource(R.string.age),
                        pickerState
                    )
                    PickerBottomSheet(
                        config,
                        onConfirmSingle = { age ->
                            onConfirmSinglePicker(
                                SettingsField.AGE,
                                age
                            )
                        },
                        onConfirmDual = { _, _ -> },
                        onDismiss = { onDismiss() }
                    )
                }

                SettingsField.WEIGHT_GOAL -> {
                    Text(
                        text = stringResource(R.string.weekly_goal),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    WeightGoalSelector(
                        unitSystem = unitSystem,
                        selected = onboardingData?.weightGoal,
                        onSelect = { selectedGoal ->
                            onSaveWeightGoal(selectedGoal)
                        }
                    )
                }

                SettingsField.WEIGHT -> {
                    val config = PickerConfigMapper.create(
                        unitSystem,
                        PickerType.WEIGHT,
                        stringResource(R.string.weight),
                        pickerState
                    )
                    PickerBottomSheet(
                        config,
                        onConfirmSingle = { lbs ->
                            onConfirmSinglePicker(
                                SettingsField.WEIGHT,
                                lbs
                            )
                        },
                        onConfirmDual = { kg, decimal ->
                            onConfirmDualPicker(
                                SettingsField.WEIGHT,
                                kg,
                                decimal
                            )
                        },
                        onDismiss = { onDismiss() }
                    )
                }

                SettingsField.HEIGHT -> {
                    val config = PickerConfigMapper.create(
                        unitSystem,
                        PickerType.HEIGHT,
                        stringResource(R.string.height),
                        pickerState
                    )
                    PickerBottomSheet(
                        config,
                        onConfirmSingle = { cm ->
                            onConfirmSinglePicker(
                                SettingsField.HEIGHT,
                                cm
                            )
                        },
                        onConfirmDual = { feet, inches ->
                            onConfirmDualPicker(
                                SettingsField.HEIGHT,
                                feet,
                                inches
                            )
                        },
                        onDismiss = { onDismiss() }
                    )
                }
            }
        }
    }
}