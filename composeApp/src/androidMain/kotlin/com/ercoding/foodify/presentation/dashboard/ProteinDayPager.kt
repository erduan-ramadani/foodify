package com.ercoding.foodify.presentation.dashboard

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.ercoding.foodify.domain.ProteinEntry
import java.time.LocalDate
import java.time.format.DateTimeFormatter


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ProteinDayPager(
    pagerState: PagerState,
    dailyEntriesByDate: Map<LocalDate, List<ProteinEntry>>,
    last7Days: List<LocalDate>,
    onDismiss: (ProteinEntry) -> Unit
) {
    HorizontalPager(pagerState) { page ->

        Column(
            modifier = Modifier.fillMaxSize(),
        ) {
            val dateKey = last7Days[page]
            val today = LocalDate.now()
            val yesterday = today.minusDays(1)
            val dateLabel = when (dateKey) {
                today -> "Heute"
                yesterday -> "Gestern"
                else -> dateKey.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))
            }
            Spacer(modifier = Modifier.padding(3.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = dateLabel,
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.width(12.dp))
                repeat(pagerState.pageCount) { index ->
                    Box(
                        modifier = Modifier
                            .size(if (pagerState.currentPage == index) 10.dp else 6.dp)
                            .clip(CircleShape)
                            .background(
                                if (pagerState.currentPage == index)
                                    MaterialTheme.colorScheme.primary
                                else
                                    MaterialTheme.colorScheme.outline
                            )
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                }
            }

            if (last7Days.isEmpty()) {
                // leere Ansicht anzeigen
            } else {
                val entriesForDay = dailyEntriesByDate[dateKey] ?: emptyList()
                val listState = rememberLazyListState()
                LazyColumn(
                    state = listState,
                ) {
                    items(entriesForDay, key = { it.id }) { entry ->
                        ProteinEntryItem(
                            entry,
                            onDismiss = { onDismiss(entry) }
                        )
                    }
                }
            }
        }
    }
}