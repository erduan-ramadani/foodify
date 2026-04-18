package com.ercoding.foodify.presentation.dashboard

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.koinViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale


@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    onSettingsClick: () -> Unit
) {

    val viewModel: DashboardViewModel = koinViewModel()
    val dailyEntriesByDate = viewModel.nutritionEntriesByDate
    val pagerState = rememberPagerState(
        initialPage = viewModel.last7Days.size - 1,
        pageCount = { viewModel.last7Days.size }
    )

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.events.collect { apiResponse ->
            snackbarHostState.showSnackbar(
                message = apiResponse,
                duration = SnackbarDuration.Long
            )
        }
    }

    LaunchedEffect(pagerState.currentPage) {
        viewModel.selectedDate = viewModel.last7Days[pagerState.currentPage]
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                ),
                title = {
                    Column {
                        Text(
                            text = LocalDate.now().format(
                                DateTimeFormatter.ofPattern("EEEE, dd. MMMM", Locale.GERMAN)
                            ),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.labelSmall
                        )
                        Text("Foodfiy", style = MaterialTheme.typography.headlineSmall)
                    }
                },
                actions = {
                    IconButton(onClick = onSettingsClick) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Settings, contentDescription = "Profile")
                        }
                    }
                }
            )
        },
        bottomBar = {}
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp, 0.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            DailySummary(viewModel)
            Spacer(modifier = Modifier.padding(8.dp))
            NutritionCards(viewModel)
            Spacer(modifier = Modifier.padding(8.dp))
            ProteinInputSection(
                onClick = { query ->
                    viewModel.addNutritionValues(query, viewModel.selectedDate)
                },
                isLoading = viewModel.isLoading
            )
            Spacer(modifier = Modifier.padding(8.dp))
            MealDayPager(
                pagerState = pagerState,
                dailyEntriesByDate = dailyEntriesByDate,
                last7Days = viewModel.last7Days,
                onDismiss = { entry -> viewModel.removeNutritionEntry(entry) },
            )
        }
    }
}