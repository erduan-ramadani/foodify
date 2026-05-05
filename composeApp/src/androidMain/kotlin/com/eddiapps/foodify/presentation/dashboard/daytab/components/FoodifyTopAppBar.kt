package com.eddiapps.foodify.presentation.dashboard.daytab.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.eddiapps.foodify.R
import com.eddiapps.foodify.presentation.dashboard.DashboardViewModel
import com.eddiapps.foodify.presentation.util.toDisplayString
import java.time.LocalDate

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FoodifyTopAppBar(
    onSettingsClick: () -> Unit,
    viewModel: DashboardViewModel
) {
    CenterAlignedTopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.background
        ),
        navigationIcon = {
            Spacer(modifier = Modifier.size(48.dp))
        },
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                IconButton(
                    onClick = { viewModel.selectedDate = viewModel.selectedDate.minusDays(1) }
                ) {
                    Icon(
                        Icons.Default.ChevronLeft,
                        contentDescription = stringResource(R.string.previous_day),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Text(
                    text = viewModel.selectedDate.toDisplayString(),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier
                        .clickable { viewModel.selectedDate = LocalDate.now() }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                )

                IconButton(
                    onClick = {
                        if (viewModel.selectedDate < LocalDate.now()) {
                            viewModel.selectedDate = viewModel.selectedDate.plusDays(1)
                        }
                    },
                    enabled = viewModel.selectedDate < LocalDate.now()
                ) {
                    Icon(
                        Icons.Default.ChevronRight,
                        contentDescription = stringResource(R.string.next_day),
                        tint = if (viewModel.selectedDate < LocalDate.now())
                            MaterialTheme.colorScheme.onSurfaceVariant
                        else
                            MaterialTheme.colorScheme.outline
                    )
                }
            }
        },
        actions = {
            IconButton(onClick = onSettingsClick) {
                Icon(
                    imageVector = Icons.Outlined.Settings,
                    contentDescription = stringResource(R.string.settings),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    )
}
