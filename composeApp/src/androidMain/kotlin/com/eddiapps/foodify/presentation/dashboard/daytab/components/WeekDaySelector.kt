package com.eddiapps.foodify.presentation.dashboard.daytab.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.eddiapps.foodify.presentation.theme.CalorieGreen
import com.eddiapps.foodify.presentation.theme.CalorieOrange
import com.eddiapps.foodify.presentation.theme.CalorieYellow
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun WeekDaySelector(
    visibleDays: List<LocalDate>,
    selectedDate: LocalDate,
    onProgressForDate: (LocalDate) -> Float,
    onWeekDaySelected: (LocalDate) -> Unit
) {
    val today = LocalDate.now()
    val listState = rememberLazyListState()

    // Beim Öffnen zum heutigen Tag scrollen
    LaunchedEffect(Unit) {
        val todayIndex = visibleDays.indexOfFirst { it == today }
        if (todayIndex >= 0) listState.scrollToItem(todayIndex)
    }

    LazyRow(
        state = listState,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(2.dp),
        contentPadding = PaddingValues(horizontal = 8.dp)
    ) {
        items(visibleDays) { date ->
            val isSelected = date == selectedDate
            val isToday = date == today
            val isFuture = date.isAfter(today)

            Column(
                modifier = Modifier
                    .width(48.dp)
                    .clickable(enabled = !isFuture) { onWeekDaySelected(date) }
                    .padding(vertical = 4.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = date.dayOfWeek.getDisplayName(
                        TextStyle.SHORT, Locale.getDefault()
                    ),
                    style = MaterialTheme.typography.labelSmall,
                    color = if (isFuture)
                        MaterialTheme.colorScheme.outline
                    else
                        MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(4.dp))

                Box(
                    modifier = Modifier.size(36.dp),
                    contentAlignment = Alignment.Center
                ) {
                    val progress = onProgressForDate(date)
                    val ringColor = when {
                        progress > 1f -> CalorieOrange
                        progress > 0.85f -> CalorieYellow
                        else -> CalorieGreen
                    }
                    val trackColor = MaterialTheme.colorScheme.outline
                    val isSelectedBg = MaterialTheme.colorScheme.primary

                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val stroke = 2.5.dp.toPx()
                        val inset = stroke / 2
                        val arcSize = Size(size.width - stroke, size.height - stroke)
                        val topLeft = Offset(inset, inset)

                        if (isSelected) {
                            drawCircle(color = isSelectedBg)
                        }

                        if (!isSelected) {
                            // Track — unten offen
                            drawArc(
                                color = trackColor,
                                startAngle = 135f,
                                sweepAngle = 270f,
                                useCenter = false,
                                topLeft = topLeft,
                                size = arcSize,
                                style = Stroke(width = stroke, cap = StrokeCap.Round)
                            )
                            // Fortschritt — unten offen
                            drawArc(
                                color = ringColor,
                                startAngle = 135f,
                                sweepAngle = 270f * progress,
                                useCenter = false,
                                topLeft = topLeft,
                                size = arcSize,
                                style = Stroke(width = stroke, cap = StrokeCap.Round)
                            )
                        }
                    }

                    Text(
                        text = date.dayOfMonth.toString(),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = if (isToday || isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = when {
                            isSelected -> MaterialTheme.colorScheme.onPrimary
                            isFuture -> MaterialTheme.colorScheme.outline
                            else -> MaterialTheme.colorScheme.onSurface
                        }
                    )
                }
            }
        }
    }
}