package com.eddiapps.foodify.presentation.picker

import androidx.compose.runtime.Composable

@Composable
fun PickerBottomSheet(
    config: PickerConfig,
    onConfirmSingle: (Int) -> Unit,
    onConfirmDual: (Int, Int) -> Unit,
    onDismiss: () -> Unit
) {
    when (config) {
        is PickerConfig.SinglePickerConfig -> SinglePicker(config, onConfirmSingle, onDismiss)
        is PickerConfig.DualPickerConfig -> DualPicker(config, onConfirmDual, onDismiss)
    }
}