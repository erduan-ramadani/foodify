package com.ercoding.foodify.presentation.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
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
import androidx.compose.ui.Modifier
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBackClick: () -> Unit
) {

    val viewModel: SettingsViewModel = koinViewModel()
    val isDarkMode by viewModel.isDarkMode.collectAsState(false)
    val dailyThreshold by viewModel.dailyThreshold.collectAsState(0)
    var showDailyThresholdDialog by remember { mutableStateOf(false) }
    val listItemColors = ListItemDefaults.colors(
        containerColor = MaterialTheme.colorScheme.background
    )

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
        ) {
            ListItem(
                headlineContent = { Text("Dark Mode") },
                supportingContent = { Text("App im Dunkelmodus anzeigen") },
                colors = listItemColors,
                trailingContent = {
                    Switch(
                        checked = isDarkMode,
                        onCheckedChange = { viewModel.toggleDarkMode() }
                    )
                },
            )
            ListItem(
                headlineContent = { Text("Täglicher Kalorienbedarf") },
                supportingContent = { Text("Aktuell: $dailyThreshold") },
                modifier = Modifier.clickable { showDailyThresholdDialog = true },
                colors = listItemColors
            )

            if (showDailyThresholdDialog) {
                var input by remember { mutableStateOf("") }
                AlertDialog(
                    title = { Text("Tägl. Kalorienbedarf") },
                    text = {
                        OutlinedTextField(
                            value = input,
                            onValueChange = { if (it.all(Char::isDigit)) input = it },
                            label = { Text("kcal") }
                        )
                    },
                    confirmButton = {
                        TextButton(onClick = {
                            viewModel.setDailyThreshold(input.toIntOrNull() ?: 0)
                            showDailyThresholdDialog = false
                        }) {
                            Text("Speichern")
                        }
                    },
                    onDismissRequest = { showDailyThresholdDialog = false },
                )
            }
        }
    }
}