package com.ercoding.foodify.presentation.dashboard.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.ercoding.foodify.domain.extension.toDisplayString
import com.ercoding.foodify.presentation.dashboard.DashboardViewModel
import java.time.LocalDate

@Composable
fun DateSelector(
    vm: DashboardViewModel
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Max),
        horizontalArrangement = Arrangement.Center
    ) {
        FilledTonalIconButton(
            onClick = {
                vm.selectedDate = vm.selectedDate.minusDays(1)
            }
        ) {
            Icon(
                Icons.Default.ChevronLeft,
                "Zurück"
            )
        }
        AssistChip(
            onClick = { vm.selectedDate = LocalDate.now() },
            label = { Text(vm.selectedDate.toDisplayString()) },
            leadingIcon = {
                if (!vm.isToday) {
                    Icon(
                        Icons.Outlined.CalendarToday,
                        contentDescription = "Kalender",
                    )
                }
            },
            shape = RoundedCornerShape(20.dp),
            colors = AssistChipDefaults.assistChipColors(
                containerColor = if (vm.isToday)
                    MaterialTheme.colorScheme.secondaryContainer
                else
                    MaterialTheme.colorScheme.surface
            ),
            border = AssistChipDefaults.assistChipBorder(
                enabled = true,
                borderColor = if (vm.isToday)
                    Color.Transparent
                else
                    MaterialTheme.colorScheme.outlineVariant
            )
        )
        FilledTonalIconButton(
            onClick = {
                if (!vm.isToday) vm.selectedDate = vm.selectedDate.plusDays(1)
            },
            colors = IconButtonDefaults.filledTonalIconButtonColors(
                disabledContainerColor = IconButtonDefaults.filledTonalIconButtonColors().containerColor,
                disabledContentColor = IconButtonDefaults.filledTonalIconButtonColors().contentColor
            ),
            enabled = !vm.isToday,
            modifier = Modifier.alpha(if (vm.isToday) 0.4f else 1f)
        ) {
            Icon(
                Icons.Default.ChevronRight,
                "Vor"
            )
        }
    }
}