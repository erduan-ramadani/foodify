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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ercoding.foodify.data.local.Scheduling
import com.ercoding.foodify.domain.model.onboarding.WeightGoal
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

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Einstellungen")
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
            SettingsSectionHeader("Persönliche Daten")
            SettingsCard {
                SettingsRow(
                    label = "Geschlecht",
                    value = if (onboardingData?.isMale == true) "♂ Männlich" else "♀ Weiblich",
                    onSettingClick = { viewModel.toggleGender() }
                )
                SettingsDivider()
                SettingsRow(
                    label = "Alter",
                    value = "${onboardingData?.age ?: 25} Jahre",
                    onSettingClick = { editingField = Settingsfield.AGE }
                )
                SettingsDivider()
                SettingsRow(
                    label = "Größe",
                    value = "${onboardingData?.height ?: 175} cm",
                    onSettingClick = { editingField = Settingsfield.HEIGHT }
                )
                SettingsDivider()
                SettingsRow(
                    label = "Gewicht",
                    value = "${onboardingData?.weight ?: 75} kg",
                    onSettingClick = { editingField = Settingsfield.WEIGHT }
                )
                SettingsDivider()
                SettingsRow(
                    label = "Wochenziel",
                    value = "${onboardingData?.weightGoal?.kgPerWeek ?: WeightGoal.NORMAL} kg",
                    onSettingClick = { editingField = Settingsfield.WEIGHT_GOAL }
                )
            }
            SettingsSectionHeader("App")
            SettingsCard {
                SettingsToggleRow(
                    label = "Dark Mode",
                    icon = "🌙",
                    checked = isDarkMode,
                    onToggle = { viewModel.toggleDarkMode() }
                )
                SettingsDivider()
                SettingsToggleRow(
                    label = "Erinnerungen",
                    icon = "🔔",
                    checked = isReminding,
                    onToggle = { newValue ->
                        if (newValue) Scheduling(context).schedule()
                        else Scheduling(context).cancel()
                        viewModel.toggleReminder()
                    }
                )
            }
            SettingsSectionHeader("Daten")
            SettingsCard {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { }
                        .padding(horizontal = 20.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text("\uD83D\uDCE4️", fontSize = 16.sp)
                    Text(
                        "Daten exportieren",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.error,
                        fontWeight = FontWeight.Medium
                    )
                }
                SettingsDivider()
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
                        "Alle Daten löschen",
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
                text = "Foodify v1.0.0 - Made with \uD83D\uDC9C",
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
            title = { Text("Alle Daten löschen?") },
            text = { Text("Alle Einträge und deine Onboarding-Daten werden unwiderruflich gelöscht.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteAllData()
                        Scheduling(context).cancel()
                        onDeleteAllData()
                        showConfirmDataDeletionDialog = false
                    }
                ) {
                    Text("Löschen", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmDataDeletionDialog = false }) {
                    Text("Abbrechen", color = MaterialTheme.colorScheme.onSurfaceVariant)
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
