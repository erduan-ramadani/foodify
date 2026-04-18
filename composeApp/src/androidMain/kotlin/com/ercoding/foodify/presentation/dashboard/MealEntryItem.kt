package com.ercoding.foodify.presentation.dashboard

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ercoding.foodify.domain.NutritionEntry
import com.ercoding.foodify.domain.toDisplayList

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MealEntryItem(
    entry: NutritionEntry,
    onDismiss: () -> Unit
) {
    val swipeState = rememberSwipeToDismissBoxState()
    var showDialog by remember { mutableStateOf(false) }


    LaunchedEffect(swipeState.currentValue) {
        if (swipeState.currentValue != SwipeToDismissBoxValue.Settled) {
            onDismiss()
        }
    }

    SwipeToDismissBox(
        state = swipeState,
        backgroundContent = {
            Box(modifier = Modifier.fillMaxWidth())
        }
    ) {
        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
            onClick = { showDialog = true },
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp)
        ) {
            Row(
                modifier = Modifier
                    .padding(12.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = entry.emoji,
                        fontSize = 22.sp
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = entry.meal,
                        style = MaterialTheme.typography.titleMedium
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            "K: ${entry.carbohydrates.toInt()}g",
                            style = MaterialTheme.typography.bodySmall
                        )
                        Text(
                            "E: ${entry.protein.toInt()}g",
                            style = MaterialTheme.typography.bodySmall
                        )
                        Text("F: ${entry.fat.toInt()}g", style = MaterialTheme.typography.bodySmall)
                        Text(
                            "Z: ${entry.sugar.toInt()}g",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
                Text(
                    text = "${entry.calories.toInt()}kcal",
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.titleMedium,
                )

                if (showDialog) {
                    AlertDialog(
                        onDismissRequest = { showDialog = false },
                        title = { Text(entry.meal) },
                        text = {
                            Column(
                                modifier = Modifier
                                    .height(400.dp)
                                    .verticalScroll(rememberScrollState())
                            ) {
                                entry.toDisplayList().forEach { (label, value) ->
                                    Text("$label: $value")
                                }
                            }
                        },
                        confirmButton = {
                            TextButton(onClick = { showDialog = false }) {
                                Text("Schließen")
                            }
                        }
                    )
                }
            }
        }
    }
}