@file:Suppress("COMPOSE_APPLIER_CALL_MISMATCH")

package com.ercoding.foodify.presentation.dashboard

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun SegmentedTabs(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit
) {
    val tabs = listOf("Tag", "Analyse")

    val backgroundColor = Color(0xFFEDE7F6)
    val selectedColor = Color.White
    val textSelected = Color(0xFF4A3AFF)
    val textUnselected = Color(0xFF7B6F9B)

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(backgroundColor)
            .padding(4.dp)
    ) {
        val tabWidth = maxWidth / tabs.size

        // 🔥 Animierter Indicator
        val indicatorOffset by animateDpAsState(
            targetValue = tabWidth * selectedTab,
            label = ""
        )

        Box(
            modifier = Modifier
                .offset(x = indicatorOffset)
                .width(tabWidth)
                .height(40.dp)
                .align(Alignment.CenterStart)
                .clip(RoundedCornerShape(20.dp))
                .background(selectedColor)
        )

        Row {
            tabs.forEachIndexed { index, title ->
                val isSelected = index == selectedTab

                Box(
                    modifier = Modifier
                        .width(tabWidth)
                        .clickable { onTabSelected(index) }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {

                        if (index == 0) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = if (isSelected) textSelected else textUnselected,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(Modifier.width(6.dp))
                        }

                        Text(
                            text = title,
                            color = if (isSelected) textSelected else textUnselected,
                            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}