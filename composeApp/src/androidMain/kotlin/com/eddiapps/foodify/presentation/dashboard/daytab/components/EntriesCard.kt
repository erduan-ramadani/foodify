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
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.eddiapps.foodify.R
import com.eddiapps.foodify.domain.model.sheet.NutritionEntry
import com.eddiapps.foodify.domain.model.sheet.formattedTime
import com.eddiapps.foodify.presentation.dashboard.daytab.sheet.NutritionBottomSheet

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun EntriesCard(
    entries: List<NutritionEntry>,
    onDelete: (NutritionEntry) -> Unit,
    onUpdate: (NutritionEntry) -> Unit
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
                                onDelete = { onDelete(it) },
                                onUpdate = { updatedEntry ->
                                    onUpdate(updatedEntry)
                                }
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
    onUpdate: (NutritionEntry) -> Unit,
    onDelete: (NutritionEntry) -> Unit
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
            .padding(end = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(100.dp)
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            if (entry.imagePath != null) {
                val isUrl = entry.imagePath.startsWith("http")
                AsyncImage(
                    model = entry.imagePath,
                    contentDescription = null,
                    modifier = Modifier
                        .size(100.dp),
                    contentScale = if (isUrl) ContentScale.Fit else ContentScale.Crop
                )
            } else {
                Text(text = entry.emoji, fontSize = 40.sp)
            }
        }

        Column(
            modifier = Modifier
                .weight(1f)
        ) {
            Text(
                text = entry.formattedTime,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .align(Alignment.End)
                    .padding(top = 8.dp)
            )
            Text(
                text = entry.title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Row(
                horizontalArrangement = Arrangement.End
            ) {
                Text(
                    text = if (entry.isMeal) "+${entry.calories.toInt()}" else "-${entry.calories.toInt()}",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = if (entry.isMeal)
                        MaterialTheme.colorScheme.onSurface
                    else
                        MaterialTheme.colorScheme.primary,
                    modifier = Modifier.alignByBaseline()

                )
                Spacer(modifier = Modifier.width(2.dp))
                Text(
                    text = "kcal",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.alignByBaseline()
                )
            }
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
            onUpdate = { updatedEntry ->
                onUpdate(updatedEntry)
                showEditSheet = false
            },
            onDelete = {
                onDelete(entry)
            },
            onDismiss = { showEditSheet = false }
        )
    }
}

