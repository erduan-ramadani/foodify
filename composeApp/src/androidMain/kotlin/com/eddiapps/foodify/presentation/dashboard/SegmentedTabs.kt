@file:Suppress("COMPOSE_APPLIER_CALL_MISMATCH")

package com.eddiapps.foodify.presentation.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.eddiapps.foodify.R

@Composable
fun SegmentedTabs(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit
) {
    val tabs = listOf(stringResource(R.string.day), stringResource(R.string.analysis))

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.fillMaxWidth()) {
            tabs.forEachIndexed { index, title ->
                val isSelected = index == selectedTab

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onTabSelected(index) },
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = title,
                        modifier = Modifier.padding(vertical = 12.dp),
                        color = if (isSelected)
                            MaterialTheme.colorScheme.onSurface
                        else
                            MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
                        style = MaterialTheme.typography.labelLarge
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(2.dp)
                            .background(
                                if (isSelected) MaterialTheme.colorScheme.primary
                                else Color.Transparent
                            )
                    )
                }
            }
        }
        HorizontalDivider(color = MaterialTheme.colorScheme.outline)
    }
}