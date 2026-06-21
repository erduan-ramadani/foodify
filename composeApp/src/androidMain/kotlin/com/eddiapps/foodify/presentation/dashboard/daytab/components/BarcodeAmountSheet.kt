package com.eddiapps.foodify.presentation.dashboard.daytab.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.eddiapps.foodify.R
import com.eddiapps.foodify.data.remote.openfoodfacts.OpenFoodFactsProduct

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BarcodeAmountSheet(
    product: OpenFoodFactsProduct,
    onDismiss: () -> Unit,
    onSave: (grams: Int) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var gramsText by remember { mutableStateOf("100") }

    val grams = gramsText.toIntOrNull() ?: 0
    val factor = grams / 100.0
    val caloriesPreview = ((product.nutriments?.caloriesPer100g ?: 0.0) * factor).toInt()
    val proteinPreview = (product.nutriments?.proteinPer100g ?: 0.0) * factor
    val carbsPreview = (product.nutriments?.carbsPer100g ?: 0.0) * factor
    val fatPreview = (product.nutriments?.fatPer100g ?: 0.0) * factor

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .imePadding()
                .navigationBarsPadding()
                .padding(horizontal = 24.dp, vertical = 16.dp)
        ) {
            Text(
                text = product.productName ?: stringResource(R.string.product_not_found),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            product.brands?.let { brand ->
                Text(
                    text = brand,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = gramsText,
                    onValueChange = { newValue ->
                        if (newValue.isEmpty() || newValue.all { it.isDigit() }) {
                            gramsText = newValue
                        }
                    },
                    label = { Text(stringResource(R.string.quantity)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = "g",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                PreviewRow(
                    stringResource(R.string.nutrient_calories),
                    "$caloriesPreview kcal ${stringResource(R.string.kcal)}"
                )
                PreviewRow(
                    stringResource(R.string.nutrient_protein),
                    "%.1f g".format(proteinPreview)
                )
                PreviewRow(stringResource(R.string.nutrient_carbs), "%.1f g".format(carbsPreview))
                PreviewRow(stringResource(R.string.nutrient_fat), "%.1f g".format(fatPreview))
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    if (grams > 0) {
                        onSave(grams)
                    }
                },
                enabled = grams > 0,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.add))
            }

            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
private fun PreviewRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold
        )
    }
}