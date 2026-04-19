package com.ercoding.foodify.presentation.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.ercoding.foodify.presentation.theme.carbsColor
import com.ercoding.foodify.presentation.theme.fatColor
import com.ercoding.foodify.presentation.theme.proteinColor
import com.ercoding.foodify.presentation.theme.sugarColor

@Composable
fun ColorLegend(
    textCarbs: String,
    textProtein: String,
    textFat: String,
    textSugar: String
) {
    Box(
        modifier = Modifier
            .size(8.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.carbsColor)
    )
    Spacer(modifier = Modifier.padding(horizontal = 1.dp))
    Text(
        text = textCarbs,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        style = MaterialTheme.typography.bodySmall,
    )
    Spacer(modifier = Modifier.padding(horizontal = 4.dp))
    Box(
        modifier = Modifier
            .size(8.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.proteinColor)
    )
    Spacer(modifier = Modifier.padding(horizontal = 1.dp))
    Text(
        text = textProtein,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        style = MaterialTheme.typography.bodySmall,
    )
    Spacer(modifier = Modifier.padding(horizontal = 4.dp))
    Box(
        modifier = Modifier
            .size(8.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.fatColor)
    )
    Spacer(modifier = Modifier.padding(horizontal = 1.dp))
    Text(
        text = textFat,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        style = MaterialTheme.typography.bodySmall,
    )
    Spacer(modifier = Modifier.padding(horizontal = 4.dp))
    Box(
        modifier = Modifier
            .size(8.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.sugarColor)
    )
    Spacer(modifier = Modifier.padding(horizontal = 1.dp))
    Text(
        text = textSugar,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        style = MaterialTheme.typography.bodySmall,
    )
    Spacer(modifier = Modifier.padding(horizontal = 4.dp))
}