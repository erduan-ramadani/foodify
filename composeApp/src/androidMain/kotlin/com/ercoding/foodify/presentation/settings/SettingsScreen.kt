package com.ercoding.foodify.presentation.settings

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ercoding.foodify.BuildConfig
import com.ercoding.foodify.R
import com.ercoding.foodify.data.local.Scheduling
import com.ercoding.foodify.domain.model.onboarding.WeightGoal
import com.ercoding.foodify.presentation.util.rememberReminderScheduler
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBackClick: () -> Unit,
    onDeleteAllData: () -> Unit,
) {
    val viewModel: SettingsViewModel = koinViewModel()
    val isDarkMode by viewModel.isDarkMode.collectAsState(false)
    val isReminding by viewModel.isReminding.collectAsState(true)
    val onboardingData by viewModel.onboardingData.collectAsState(null)
    var editingField by remember { mutableStateOf<Settingsfield?>(null) }
    var showConfirmDataDeletionDialog by remember { mutableStateOf(false) }


    val context = LocalContext.current
    val scheduleReminder = rememberReminderScheduler()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(stringResource(R.string.settings))
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "arrowBack")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState()),
        ) {
            SettingsSectionHeader(stringResource(R.string.settings_personal_data))
            SettingsCard {
                SettingsRow(
                    label = stringResource(R.string.gender),
                    value = if (onboardingData?.isMale == true) {
                        stringResource(R.string.settings_male)
                    } else {
                        stringResource(R.string.settings_female)
                    },
                    onSettingClick = { viewModel.toggleGender() }
                )
                SettingsDivider()
                SettingsRow(
                    label = stringResource(R.string.age),
                    value = "${onboardingData?.age ?: 25} " + stringResource(R.string.age),
                    onSettingClick = { editingField = Settingsfield.AGE }
                )
                SettingsDivider()
                SettingsRow(
                    label = stringResource(R.string.height),
                    value = "${onboardingData?.height ?: 175} cm",
                    onSettingClick = { editingField = Settingsfield.HEIGHT }
                )
                SettingsDivider()
                SettingsRow(
                    label = stringResource(R.string.weight),
                    value = "${onboardingData?.weight ?: 75} kg",
                    onSettingClick = { editingField = Settingsfield.WEIGHT }
                )
                SettingsDivider()
                SettingsRow(
                    label = stringResource(R.string.weekly_goal),
                    value = "${onboardingData?.weightGoal?.kgPerWeek ?: WeightGoal.NORMAL} kg",
                    onSettingClick = { editingField = Settingsfield.WEIGHT_GOAL }
                )
            }
            SettingsSectionHeader(stringResource(R.string.app))
            SettingsCard {
                SettingsToggleRow(
                    label = stringResource(R.string.dark_mode),
                    icon = "🌙",
                    checked = isDarkMode,
                    onToggle = { viewModel.toggleDarkMode() }
                )
                SettingsDivider()
                SettingsToggleRow(
                    label = stringResource(R.string.reminder),
                    icon = "🔔",
                    checked = isReminding,
                    onToggle = { newValue ->
                        if (newValue) scheduleReminder()
                        else Scheduling(context).cancel()
                        viewModel.toggleReminder()
                    }
                )
            }
            SettingsSectionHeader(stringResource(R.string.data))
            SettingsCard {
//                Row(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .clickable { }
//                        .padding(horizontal = 20.dp, vertical = 14.dp),
//                    verticalAlignment = Alignment.CenterVertically,
//                    horizontalArrangement = Arrangement.spacedBy(10.dp)
//                ) {
//                    Text("\uD83D\uDCE4️", fontSize = 16.sp)
//                    Text(
//                        stringResource(R.string.data_export),
//                        style = MaterialTheme.typography.bodyMedium,
//                        color = MaterialTheme.colorScheme.error,
//                        fontWeight = FontWeight.Medium
//                    )
//                }
//                SettingsDivider()
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showConfirmDataDeletionDialog = true }
                        .padding(horizontal = 20.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text("🗑️", fontSize = 16.sp)
                    Text(
                        stringResource(R.string.delete_all_data),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.error,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            editingField?.let { field ->
                SettingsBottomSheet(
                    editingField = field,
                    onboardingData = onboardingData,
                    onSave = { fieldName, value ->
                        viewModel.saveSettingsBottomSheetChange(fieldName, value)
                        editingField = null
                    },
                    onSaveWeightGoal = { goal ->
                        viewModel.setWeightGoal(goal)
                        editingField = null
                    },
                    onDismiss = { editingField = null }
                )
            }

            Text(
                text = "Foodify v${BuildConfig.VERSION_NAME} - Made with 💜",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }

    if (showConfirmDataDeletionDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmDataDeletionDialog = false },
            title = { Text(stringResource(R.string.confirm_delete_title)) },
            text = { Text(stringResource(R.string.confirm_delete_description)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteAllData()
                        Scheduling(context).cancel()
                        onDeleteAllData()
                        showConfirmDataDeletionDialog = false
                    }
                ) {
                    Text(stringResource(R.string.delete), color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmDataDeletionDialog = false }) {
                    Text(
                        stringResource(R.string.cancel),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        )
    }
}

@Composable
private fun SettingsSectionHeader(title: String) {
    Text(
        text = title.uppercase(),
        modifier = Modifier.padding(start = 24.dp, top = 20.dp, bottom = 8.dp),
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        letterSpacing = 0.8.sp
    )
}

@Composable
private fun SettingsCard(content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column { content() }
    }
}

@Composable
private fun SettingsRow(
    label: String,
    value: String,
    onSettingClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onSettingClick)
            .padding(horizontal = 20.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
            )
            Icon(
                Icons.Default.ChevronRight,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = MaterialTheme.colorScheme.outlineVariant
            )
        }
    }
}

@Composable
private fun SettingsToggleRow(
    label: String,
    icon: String,
    checked: Boolean,
    onToggle: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(icon, fontSize = 16.sp)
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = { newValue -> onToggle(newValue) },
            colors = SwitchDefaults.colors(
                uncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant,
                uncheckedBorderColor = MaterialTheme.colorScheme.outline,
                uncheckedThumbColor = MaterialTheme.colorScheme.onSurfaceVariant
            )
        )
    }
}

@Composable
private fun SettingsDivider() {
    HorizontalDivider(
        modifier = Modifier.padding(horizontal = 20.dp),
        color = MaterialTheme.colorScheme.outlineVariant
    )
}
