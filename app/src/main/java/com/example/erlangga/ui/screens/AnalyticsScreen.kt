package com.example.erlangga.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.erlangga.viewmodels.AnalyticsViewModel
import androidx.compose.foundation.Canvas
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.Stroke
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

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
    val tasksByTag = analyticsData.tasks_by_tag

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

        // Category breakdown card
        if (tasksByTag.isNotEmpty()) {
            CategoryBreakdownCard(
                tasksByTag = tasksByTag,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )
        }

        // Tasks per day chart
        TasksPerDayCard(
            tasksPerDay = tasksPerDay,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )
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
fun CategoryBreakdownCard(
    tasksByTag: Map<String, Int>,
    modifier: Modifier = Modifier
) {
    val tagColors = listOf(
        Color(0xFF1A1A1A), Color(0xFF6B7280), Color(0xFFD4A853),
        Color(0xFF7C9A7E), Color(0xFFB07BAC), Color(0xFF5B8DB8)
    )
    val total = tasksByTag.values.sum().toFloat()
    val entries = tasksByTag.entries.toList()
    var showDonut by remember { mutableStateOf(true) }

    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "By category",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = (-0.1).sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                // Toggle button
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .padding(2.dp)
                ) {
                    listOf("Donut" to true, "Bar" to false).forEach { (label, isDonut) ->
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(
                                    if (showDonut == isDonut) MaterialTheme.colorScheme.surface
                                    else Color.Transparent
                                )
                                .clickable { showDonut = isDonut }
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = label,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                color = if (showDonut == isDonut)
                                    MaterialTheme.colorScheme.onSurface
                                else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            if (showDonut) {
                // Donut chart
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(contentAlignment = Alignment.Center, modifier = Modifier.size(120.dp)) {
                        Canvas(modifier = Modifier.size(120.dp)) {
                            var startAngle = -90f
                            entries.forEachIndexed { i, (_, count) ->
                                val sweep = (count / total) * 360f
                                drawArc(
                                    color = tagColors[i % tagColors.size],
                                    startAngle = startAngle,
                                    sweepAngle = sweep - 2f,
                                    useCenter = false,
                                    style = Stroke(width = 22.dp.toPx()),
                                    topLeft = Offset(11.dp.toPx(), 11.dp.toPx()),
                                    size = Size(size.width - 22.dp.toPx(), size.height - 22.dp.toPx())
                                )
                                startAngle += sweep
                            }
                        }
                        Text(
                            text = "${total.toInt()}",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    // Legend
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        entries.forEachIndexed { i, (tag, count) ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(RoundedCornerShape(2.dp))
                                        .background(tagColors[i % tagColors.size])
                                )
                                Text(text = tag, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface)
                                Text(
                                    text = count.toString(),
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }
                    }
                }
            } else {
                // Horizontal bar chart
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    val max = entries.maxOfOrNull { it.value }?.toFloat() ?: 1f
                    entries.forEachIndexed { i, (tag, count) ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = tag,
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.width(60.dp)
                            )
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(10.dp)
                                    .clip(RoundedCornerShape(5.dp))
                                    .background(MaterialTheme.colorScheme.surfaceVariant)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxHeight()
                                        .fillMaxWidth(count / max)
                                        .clip(RoundedCornerShape(5.dp))
                                        .background(tagColors[i % tagColors.size])
                                )
                            }
                            Text(
                                text = count.toString(),
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.width(16.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TasksPerDayCard(
    tasksPerDay: List<Int>,
    modifier: Modifier = Modifier
) {
    var selectedIndex by remember { mutableStateOf<Int?>(null) }
    val maxVal = (tasksPerDay.maxOrNull() ?: 1).coerceAtLeast(1)
    val avg = if (tasksPerDay.isNotEmpty()) tasksPerDay.average() else 0.0
    val growth = if (tasksPerDay.size >= 14) {
        val first = tasksPerDay.take(7).average()
        val last = tasksPerDay.takeLast(7).average()
        if (first > 0) ((last - first) / first * 100).toInt() else 0
    } else 0

    val dateLabels = remember(tasksPerDay.size) {
        (0 until tasksPerDay.size).map { i ->
            val daysAgo = tasksPerDay.size - 1 - i
            Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -daysAgo) }.time
        }
    }
    val dayShort = remember(dateLabels) {
        val fmt = SimpleDateFormat("EEE", Locale.getDefault())
        dateLabels.map { fmt.format(it).take(2) }
    }
    val dayFull = remember(dateLabels) {
        val fmt = SimpleDateFormat("EEE, d MMM", Locale.getDefault())
        dateLabels.map { fmt.format(it) }
    }

    val subtitleText = selectedIndex?.let { i ->
        "${dayFull[i]} · ${tasksPerDay[i]} tasks"
    } ?: "Last ${tasksPerDay.size} days · avg ${"%.1f".format(avg)}"

    val growthText = when {
        growth > 0 -> "+$growth%"
        growth < 0 -> "$growth%"
        else -> "—"
    }
    val growthColor = when {
        growth > 0 -> Color(0xFF4CAF50)
        growth < 0 -> MaterialTheme.colorScheme.error
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(18.dp)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
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
                        text = subtitleText,
                        fontSize = 10.5.sp,
                        color = if (selectedIndex != null) MaterialTheme.colorScheme.onSurface
                                else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
                Text(
                    text = growthText,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = growthColor
                )
            }

            // Chart
            val barColor = MaterialTheme.colorScheme.onBackground
            val barMutedColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.25f)
            val selectedColor = MaterialTheme.colorScheme.onBackground
            val gridColor = MaterialTheme.colorScheme.outlineVariant
            val density = LocalDensity.current

            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp)
            ) {
                val chartWidthPx = with(density) { maxWidth.toPx() }
                val chartHeightPx = with(density) { 110.dp.toPx() }
                val barCount = tasksPerDay.size
                val slotWidth = chartWidthPx / barCount
                val barPaddingPx = with(density) { 2.5.dp.toPx() }

                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                        .pointerInput(tasksPerDay) {
                            detectTapGestures { offset ->
                                val idx = (offset.x / slotWidth)
                                    .toInt()
                                    .coerceIn(0, barCount - 1)
                                selectedIndex = if (selectedIndex == idx) null else idx
                            }
                        }
                ) {
                    val gridFractions = listOf(0.25f, 0.5f, 0.75f, 1f)
                    val dash = PathEffect.dashPathEffect(floatArrayOf(6f, 6f), 0f)

                    // Grid lines
                    gridFractions.forEach { frac ->
                        val y = size.height * (1f - frac)
                        drawLine(
                            color = gridColor,
                            start = Offset(0f, y),
                            end = Offset(size.width, y),
                            strokeWidth = 1.dp.toPx(),
                            pathEffect = if (frac < 1f) dash else null
                        )
                    }

                    // Bars
                    tasksPerDay.forEachIndexed { i, value ->
                        val barH = if (maxVal > 0) (value.toFloat() / maxVal) * size.height else 0f
                        val x = i * slotWidth + barPaddingPx
                        val w = slotWidth - barPaddingPx * 2
                        val color = when {
                            selectedIndex == null -> barColor.copy(alpha = if (i == tasksPerDay.lastIndex) 1f else 0.6f)
                            selectedIndex == i -> selectedColor
                            else -> barMutedColor
                        }
                        if (barH > 0f) {
                            drawRoundRect(
                                color = color,
                                topLeft = Offset(x, size.height - barH),
                                size = Size(w, barH),
                                cornerRadius = CornerRadius(4.dp.toPx())
                            )
                        }
                    }
                }
            }

            // Day labels
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 6.dp)
            ) {
                dayShort.forEachIndexed { i, label ->
                    Text(
                        text = label,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center,
                        fontSize = 8.5.sp,
                        fontWeight = if (selectedIndex == i) FontWeight.Bold else FontWeight.Normal,
                        color = if (selectedIndex == i)
                            MaterialTheme.colorScheme.onBackground
                        else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    )
                }
            }
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
