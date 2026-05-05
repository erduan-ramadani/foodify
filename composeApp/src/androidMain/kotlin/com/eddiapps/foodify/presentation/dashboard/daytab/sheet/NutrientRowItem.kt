package com.eddiapps.foodify.presentation.dashboard.daytab.sheet

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.eddiapps.foodify.domain.model.sheet.NutrientRow

@Composable
fun NutrientRowItem(row: NutrientRow) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                start = if (row.isIndented) 12.dp else 0.dp,
            ),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = row.label,
            fontSize = 13.sp,
            fontWeight = if (row.isBold) FontWeight.SemiBold else FontWeight.Normal,
            color = if (row.isIndented)
                MaterialTheme.colorScheme.onSurfaceVariant
            else
                MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = "${row.value} ${row.unit}",
            fontSize = 13.sp,
            fontWeight = if (row.isBold) FontWeight.Bold else FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}