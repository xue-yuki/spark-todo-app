package com.example.erlangga.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.random.Random
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.erlangga.viewmodels.AnalyticsViewModel

@Composable
fun AnalyticsScreen(
    analyticsViewModel: AnalyticsViewModel = viewModel()
) {
    val analyticsData by analyticsViewModel.analyticsData.collectAsState()
    val analyticsState by analyticsViewModel.analyticsState.collectAsState()

    // Load analytics when screen appears
    LaunchedEffect(Unit) {
        analyticsViewModel.loadAnalytics()
    }

    val heatmapData = analyticsData.heatmap_data.ifEmpty { List(105) { 0 } }
    val tasksPerDay = analyticsData.tasks_per_day.ifEmpty { List(14) { 0 } }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 20.dp)
            .padding(top = 14.dp, bottom = 24.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Header
        Column(modifier = Modifier.padding(bottom = 18.dp)) {
            Text(
                text = "April · Last 15 weeks",
                fontSize = 12.sp,
                letterSpacing = 0.1.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "You shipped ${analyticsData.total_shipped} things",
                fontSize = 30.sp,
                fontWeight = FontWeight.Medium,
                letterSpacing = (-0.6).sp,
                lineHeight = 31.5.sp,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        // Stat cards row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            StatCard(
                modifier = Modifier.weight(1f),
                label = "STREAK",
                value = "${analyticsData.streak_days}",
                unit = "days"
            )
            StatCard(
                modifier = Modifier.weight(1f),
                label = "FOCUS",
                value = "${analyticsData.focus_time_hours.toInt()}h",
                unit = "total"
            )
            StatCard(
                modifier = Modifier.weight(1f),
                label = "RATE",
                value = "${analyticsData.completion_rate}%",
                unit = "complete"
            )
        }

        // Heatmap card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            shape = RoundedCornerShape(20.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Activity heatmap",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = (-0.1).sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Each box = 1 day",
                        fontSize = 10.5.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Heatmap grid
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    for (row in 0 until 7) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            for (col in 0 until 15) {
                                val index = col * 7 + row
                                if (index < heatmapData.size) {
                                    HeatmapCell(level = heatmapData[index])
                                }
                            }
                        }
                    }
                }

                // Legend
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Less",
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.weight(1f)
                    )
                    for (level in 0..4) {
                        HeatmapCell(level = level, size = 10.dp)
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "More",
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Tasks per day chart
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            shape = RoundedCornerShape(20.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column {
                        Text(
                            text = "Tasks per day",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            letterSpacing = (-0.1).sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Last 14 days · avg 4.9",
                            fontSize = 10.5.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                    Text(
                        text = "+12%",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                // Bar chart
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp),
                    horizontalArrangement = Arrangement.spacedBy(5.dp),
                    verticalAlignment = Alignment.Bottom
                ) {
                    tasksPerDay.forEachIndexed { index, value ->
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight(value / 8f)
                                .clip(RoundedCornerShape(3.dp))
                                .background(
                                    if (index == tasksPerDay.lastIndex)
                                        MaterialTheme.colorScheme.onBackground
                                    else MaterialTheme.colorScheme.primary.copy(alpha = 0.75f)
                                )
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun StatCard(
    modifier: Modifier = Modifier,
    label: String,
    value: String,
    unit: String
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(18.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Text(
                text = label,
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 0.16.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value,
                fontFamily = FontFamily.Monospace,
                fontSize = 22.sp,
                fontWeight = FontWeight.Normal,
                letterSpacing = (-0.5).sp,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(top = 4.dp)
            )
            Text(
                text = unit,
                fontSize = 10.5.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun HeatmapCell(
    level: Int,
    size: Dp = 12.dp
) {
    val colors = listOf(
        Color.Transparent,
        MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
        MaterialTheme.colorScheme.primary.copy(alpha = 0.4f),
        MaterialTheme.colorScheme.primary,
        MaterialTheme.colorScheme.onBackground
    )

    Box(
        modifier = Modifier
            .size(size)
            .clip(RoundedCornerShape(3.dp))
            .background(colors[level])
            .then(
                if (level == 0) Modifier.border(
                    1.dp,
                    MaterialTheme.colorScheme.outline,
                    RoundedCornerShape(3.dp)
                ) else Modifier
            )
    )
}
