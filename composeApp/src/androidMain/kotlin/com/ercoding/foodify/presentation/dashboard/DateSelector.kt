package com.ercoding.foodify.presentation.dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.AssistChip
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.time.LocalDate

@Composable
fun DateSelector(
    vm: DashboardViewModel
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        FilledTonalIconButton(
            onClick = {
                vm.selectedDate = vm.selectedDate?.minusDays(1)
            }
        ) {
            Icon(
                Icons.Default.ChevronLeft,
                "Zurück"
            )
        }
        AssistChip(
            onClick = {
                vm.selectedDate = LocalDate.now()
            },
            label = {
                Text("${vm.selectedDate}")
            },
            leadingIcon = {
                Icon(
                    Icons.Default.DateRange,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
            }
        )
        FilledTonalIconButton(
            onClick = {
                vm.selectedDate = vm.selectedDate?.plusDays(1)
            },
            enabled = vm.selectedDate != LocalDate.now()
        ) {
            Icon(
                Icons.Default.ChevronRight,
                "Vor"
            )
        }
    }
}