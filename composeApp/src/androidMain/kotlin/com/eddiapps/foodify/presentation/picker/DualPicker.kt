package com.eddiapps.foodify.presentation.picker

import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.eddiapps.foodify.R

@Composable
fun DualPicker(
    config: PickerConfig.DualPickerConfig,
    onConfirm: (Int, Int) -> Unit,
    onDismiss: () -> Unit
) {
    val rangeList = remember(config.range) {
        config.range.toList()
    }

    val rangeDecimalList = remember(config.decimalRange) {
        config.decimalRange.toList()
    }

    val listStateValue = remember(config.initialValue, config.range) {
        LazyListState(
            firstVisibleItemIndex = config.range.toList().indexOf(config.initialValue)
        )
    }
    val listStateDecimal = remember(config.initialDecimal, config.decimalRange) {
        LazyListState(
            firstVisibleItemIndex = config.decimalRange.toList().indexOf(config.initialDecimal)
        )
    }
    var selectedValue by remember(config.initialValue) {
        mutableIntStateOf(config.initialValue)
    }

    var selectedDecimal by remember(config.initialDecimal) {
        mutableIntStateOf(config.initialDecimal)
    }

    val snappingLayoutValue = rememberSnapFlingBehavior(listStateValue)
    val snappingLayoutDecimal = rememberSnapFlingBehavior(listStateDecimal)

    LaunchedEffect(listStateValue) {
        snapshotFlow { listStateValue.firstVisibleItemIndex }
            .collect { index ->
                rangeList.getOrNull(index)?.let { selectedValue = it }
            }
    }

    LaunchedEffect(listStateDecimal) {
        snapshotFlow { listStateDecimal.firstVisibleItemIndex }
            .collect { index ->
                rangeDecimalList.getOrNull(index)?.let { selectedDecimal = it }
            }
    }

    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onDismiss) {
                Icon(Icons.Default.Close, contentDescription = stringResource(R.string.cancel))
            }
            Text(
                config.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            IconButton(onClick = { onConfirm(selectedValue, selectedDecimal) }) {
                Icon(Icons.Default.Check, contentDescription = stringResource(R.string.confirm))
            }
        }

        // Drum Picker
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .height(200.dp)
                    .weight(0.4f),
                contentAlignment = Alignment.Center
            ) {
                HorizontalDivider(modifier = Modifier.offset(y = (-20).dp))
                HorizontalDivider(modifier = Modifier.offset(y = 20.dp))

                LazyColumn(
                    state = listStateValue,
                    flingBehavior = snappingLayoutValue,
                    horizontalAlignment = Alignment.End,
                    contentPadding = PaddingValues(top = 80.dp, bottom = 80.dp, end = 40.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(rangeList) { value ->
                        val isSelected = value == selectedValue
                        Text(
                            text = "$value",
                            fontSize = if (isSelected) 26.sp else 18.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected)
                                MaterialTheme.colorScheme.primary
                            else
                                MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
                }
            }

            // Komma
            Box(
                modifier = Modifier
                    .height(200.dp)
                    .weight(0.1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = ",",
                    fontSize = 20.sp
                )
            }

            Box(
                modifier = Modifier
                    .height(200.dp)
                    .weight(0.5f),
                contentAlignment = Alignment.Center
            ) {
                HorizontalDivider(modifier = Modifier.offset(y = (-20).dp))
                HorizontalDivider(modifier = Modifier.offset(y = 20.dp))

                LazyColumn(
                    state = listStateDecimal,
                    flingBehavior = snappingLayoutDecimal,
                    horizontalAlignment = Alignment.Start,
                    contentPadding = PaddingValues(top = 80.dp, bottom = 80.dp, start = 40.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(rangeDecimalList) { value ->
                        val isSelected = value == selectedDecimal
                        Text(
                            text = "$value ${config.unit}",
                            fontSize = if (isSelected) 26.sp else 18.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected)
                                MaterialTheme.colorScheme.primary
                            else
                                MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(32.dp))
    }
}