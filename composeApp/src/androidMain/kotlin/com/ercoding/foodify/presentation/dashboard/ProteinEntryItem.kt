package com.ercoding.foodify.presentation.dashboard

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ercoding.foodify.domain.ProteinEntry


@Composable
fun ProteinEntryItem(
    entry: ProteinEntry,
    onDismiss: () -> Unit
) {

    val swipeState = rememberSwipeToDismissBoxState()

    LaunchedEffect(swipeState.currentValue) {
        if (swipeState.currentValue != SwipeToDismissBoxValue.Settled) {
            onDismiss()
        }
    }

    SwipeToDismissBox(
        state = swipeState,
        backgroundContent = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
            )
        }
    ) {
        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp)
        ) {
            Row(modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = entry.emoji,
                        fontSize = 18.sp
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    entry.meal,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    entry.proteinAmount.toString() + "g",
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.titleMedium,
                )
            }
        }
    }
}