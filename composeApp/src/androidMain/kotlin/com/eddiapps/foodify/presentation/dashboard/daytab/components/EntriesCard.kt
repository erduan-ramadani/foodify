package com.eddiapps.foodify.presentation.dashboard.daytab.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.eddiapps.foodify.R
import com.eddiapps.foodify.domain.model.sheet.NutritionEntry
import com.eddiapps.foodify.domain.model.sheet.formattedTime
import com.eddiapps.foodify.presentation.dashboard.DashboardViewModel
import com.eddiapps.foodify.presentation.dashboard.daytab.sheet.NutritionBottomSheet

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun EntriesCard(
    entries: List<NutritionEntry>,
    viewModel: DashboardViewModel
) {
    Column {
        Text(
            text = stringResource(R.string.entries),
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(4.dp)
        )

        if (entries.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("🍽️", fontSize = 32.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = stringResource(R.string.no_entries),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = stringResource(R.string.add_first_entry),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
            ) {
                Column {
                    val sortedEntries = entries.sortedBy { it.createdAt }
                    sortedEntries.forEachIndexed { index, entry ->
                        key(entry.id) {
                            EntryRow(
                                entry = entry,
                                viewModel = viewModel
                            )
                            if (index < entries.size - 1) {
                                HorizontalDivider(
                                    modifier = Modifier.padding(horizontal = 16.dp),
                                    color = MaterialTheme.colorScheme.outline
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun EntryRow(
    entry: NutritionEntry,
    viewModel: DashboardViewModel
) {
    var showNutritionSheet by remember { mutableStateOf(false) }
    var showEditSheet by remember { mutableStateOf(false) }
    val haptic = LocalHapticFeedback.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = {
                    if (entry.isMeal) {
                        showNutritionSheet = true
                    }
                },
                onLongClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    showEditSheet = true
                }
            )
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Icon
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = entry.emoji,
                fontSize = 16.sp
            )
        }

        // Name + Subtitle
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = entry.query,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "${entry.formattedTime} · ${
                    if (entry.isMeal) stringResource(R.string.meal)
                    else stringResource(R.string.activity)
                }",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Calories
        Row(verticalAlignment = Alignment.Bottom) {
            Text(
                text = if (entry.isMeal) "+${entry.calories.toInt()}" else "${entry.calories.toInt()}",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = if (entry.isMeal)
                    MaterialTheme.colorScheme.onSurface
                else
                    MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(2.dp))
            Text(
                text = "kcal",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 2.dp)
            )
        }
    }

    if (showNutritionSheet) {
        NutritionBottomSheet(
            entry = entry,
            onDismiss = { showNutritionSheet = false })
    }

    if (showEditSheet) {
        EditEntrySheet(
            entry = entry,
            onSave = { name, time, calories ->
                viewModel.updateEntry(entry, name, time, calories)
                showEditSheet = false
            },
            onEntryDelete = { viewModel.removeNutritionEntry(entry) },
            onDismiss = { showEditSheet = false }
        )
    }
}

