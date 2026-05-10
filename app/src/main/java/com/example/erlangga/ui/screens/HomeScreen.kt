package com.example.erlangga.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.erlangga.data.models.mockTasks
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.DrawScope
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.erlangga.viewmodels.TaskViewModel
import com.example.erlangga.viewmodels.AnalyticsViewModel
import com.example.erlangga.viewmodels.PomodoroViewModel

@Composable
fun HomeScreen(
    userName: String = "User",
    onNavigateToTasks: () -> Unit,
    onNavigateToPomodoro: () -> Unit,
    onNavigateToFocus: () -> Unit,
    onNavigateToAnalytics: () -> Unit,
    taskViewModel: TaskViewModel = viewModel(),
    analyticsViewModel: AnalyticsViewModel = viewModel(),
    pomodoroViewModel: PomodoroViewModel = viewModel()
) {
    val tasks by taskViewModel.tasks.collectAsState()
    val analyticsData by analyticsViewModel.analyticsData.collectAsState()
    val topTasks = tasks.filter { !it.done }.take(3)
    val doneToday = tasks.count { it.done }
    val total = tasks.size

    LaunchedEffect(Unit) {
        analyticsViewModel.loadAnalytics()
    }

    val pomoRunning by pomodoroViewModel.isRunning.collectAsState()
    val pomoTimeLeft by pomodoroViewModel.timeLeft.collectAsState()
    val pomoDisplay = String.format("%02d:%02d", pomoTimeLeft / 60, pomoTimeLeft % 60)

    val hour = java.time.LocalTime.now().hour
    val greeting = when {
        hour in 5..10 -> "Morning"
        hour in 11..14 -> "Good afternoon"
        hour in 15..17 -> "Afternoon"
        hour in 18..20 -> "Good evening"
        else -> "Good night"
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 18.dp)
            .padding(top = 8.dp, bottom = 24.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Greeting Section
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp, horizontal = 2.dp)
                .padding(bottom = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = LocalDate.now()
                        .format(DateTimeFormatter.ofPattern("EEEE, MMMM d")),
                    fontSize = 13.sp,
                    letterSpacing = 0.1.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                Text(
                    text = "$greeting, ${userName.split(" ").firstOrNull() ?: userName}.",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Medium,
                    letterSpacing = (-0.5).sp,
                    lineHeight = 31.2.sp,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "${topTasks.size} things await.",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Medium,
                    letterSpacing = (-0.5).sp,
                    lineHeight = 31.2.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Image(
                painter = painterResource(com.example.erlangga.R.drawable.sparky),
                contentDescription = "Sparky",
                contentScale = ContentScale.Fit,
                modifier = Modifier.size(130.dp)
            )

            IconButton(
                onClick = { /* TODO: Search */ },
                modifier = Modifier
                    .size(42.dp)
                    .background(
                        MaterialTheme.colorScheme.surfaceVariant,
                        CircleShape
                    )
            ) {
                Icon(
                    Icons.Default.Search,
                    contentDescription = "Search",
                    modifier = Modifier.size(20.dp),
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Bento Grid
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Big "Next task" card
            if (topTasks.isNotEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(28.dp))
                        .background(MaterialTheme.colorScheme.onBackground)
                        .clickable(onClick = onNavigateToTasks)
                ) {
                    // Decorative gradient dot
                    Box(
                        modifier = Modifier
                            .size(120.dp)
                            .offset(x = (-40).dp, y = (-40).dp)
                            .align(Alignment.TopEnd)
                            .background(
                                brush = Brush.radialGradient(
                                    colors = listOf(
                                        MaterialTheme.colorScheme.primary.copy(alpha = 0.13f),
                                        Color.Transparent
                                    ),
                                    center = Offset.Zero,
                                    radius = 200f
                                ),
                                shape = CircleShape
                            )
                    )

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(22.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "NEXT UP · ${topTasks[0].time}",
                                fontSize = 11.sp,
                                letterSpacing = 0.18.sp,
                                color = MaterialTheme.colorScheme.background.copy(alpha = 0.6f),
                                fontWeight = FontWeight.Normal
                            )
                            Surface(
                                color = Color.White.copy(alpha = 0.12f),
                                shape = RoundedCornerShape(99.dp)
                            ) {
                                Text(
                                    text = topTasks[0].tag,
                                    modifier = Modifier.padding(
                                        horizontal = 10.dp,
                                        vertical = 4.dp
                                    ),
                                    fontSize = 11.sp,
                                    letterSpacing = 0.1.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.background
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = topTasks[0].title,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Medium,
                            letterSpacing = (-0.4).sp,
                            lineHeight = 28.8.sp,
                            color = MaterialTheme.colorScheme.background,
                            modifier = Modifier.padding(bottom = if (topTasks[0].notes.isNullOrBlank()) 20.dp else 8.dp)
                        )

                        if (!topTasks[0].notes.isNullOrBlank()) {
                            Text(
                                text = topTasks[0].notes ?: "",
                                fontSize = 13.sp,
                                lineHeight = 18.2.sp,
                                color = MaterialTheme.colorScheme.background.copy(alpha = 0.7f),
                                modifier = Modifier.padding(bottom = 16.dp),
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        Spacer(modifier = Modifier.weight(1f))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = {
                                    topTasks.firstOrNull()?.let { task ->
                                        taskViewModel.toggleTask(task.id)
                                    }
                                },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.background,
                                    contentColor = MaterialTheme.colorScheme.onBackground
                                ),
                                shape = RoundedCornerShape(16.dp),
                                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp)
                            ) {
                                Icon(
                                    Icons.Default.Check,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    "Mark done",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }

                            Button(
                                onClick = onNavigateToPomodoro,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color.White.copy(alpha = 0.12f),
                                    contentColor = MaterialTheme.colorScheme.background
                                ),
                                shape = RoundedCornerShape(16.dp),
                                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp)
                            ) {
                                Icon(
                                    Icons.Default.PlayArrow,
                                    contentDescription = null,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    "Focus",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }

            // Pomodoro + Weather cards row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Pomodoro card
                BentoCardWithBorder(
                    modifier = Modifier.weight(1f),
                    onClick = onNavigateToPomodoro,
                    hasBorder = true
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.padding(bottom = 14.dp)
                    ) {
                        Icon(
                            Icons.Default.Timer,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "FOCUS",
                            fontSize = 11.sp,
                            letterSpacing = 0.14.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Text(
                        text = pomoDisplay,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Light,
                        letterSpacing = (-1).sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(bottom = 2.dp)
                    )

                    Text(
                        text = if (pomoRunning) "Running — session 2 of 4" else "Tap to start session",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(bottom = 14.dp)
                    )

                    Button(
                        onClick = { pomodoroViewModel.toggleTimer() },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant,
                            contentColor = MaterialTheme.colorScheme.onSurface
                        ),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(vertical = 10.dp)
                    ) {
                        Icon(
                            if (pomoRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            if (pomoRunning) "Pause" else "Start 25:00",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                // Weather card
                BentoCardAlt(
                    modifier = Modifier.weight(1f),
                    onClick = { /* TODO */ }
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.padding(bottom = 14.dp)
                    ) {
                        Icon(
                            Icons.Default.Cloud,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "PURWOKERTO",
                            fontSize = 11.sp,
                            letterSpacing = 0.14.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Text(
                        text = "28°",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Light,
                        letterSpacing = (-1).sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(bottom = 2.dp)
                    )

                    Text(
                        text = "Partly cloudy · H 32° L 24°",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 14.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        listOf("10a 26°", "12p 30°", "2p 32°").forEach { forecast ->
                            Text(
                                text = forecast,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 11.sp,
                                letterSpacing = 0.1.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            // Streak + Today progress row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Streak card
                BentoCardWithBorder(
                    modifier = Modifier.weight(1f),
                    onClick = onNavigateToAnalytics,
                    hasBorder = true
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.padding(bottom = 14.dp)
                    ) {
                        Icon(
                            Icons.Default.Whatshot,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "STREAK",
                            fontSize = 11.sp,
                            letterSpacing = 0.14.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.Bottom,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.padding(bottom = 4.dp)
                    ) {
                        Text(
                            text = "${analyticsData.streak_days}",
                            fontSize = 38.sp,
                            fontWeight = FontWeight.Medium,
                            letterSpacing = (-1.5).sp,
                            lineHeight = 38.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "days",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(bottom = 2.dp)
                        )
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 10.dp),
                        horizontalArrangement = Arrangement.spacedBy(3.dp)
                    ) {
                        listOf(1, 1, 1, 1, 1, 1, 0).forEach { active ->
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(20.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(
                                        if (active == 1) MaterialTheme.colorScheme.primary
                                        else MaterialTheme.colorScheme.surfaceVariant
                                    )
                            )
                        }
                    }
                }

                // Today progress card
                BentoCardWithBorder(
                    modifier = Modifier.weight(1f),
                    onClick = onNavigateToAnalytics,
                    hasBorder = true
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.padding(bottom = 14.dp)
                    ) {
                        Icon(
                            Icons.Default.Star,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "TODAY",
                            fontSize = 11.sp,
                            letterSpacing = 0.14.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Box(
                        contentAlignment = Alignment.BottomCenter,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // Arc progress indicator
                        androidx.compose.foundation.Canvas(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(72.dp)
                        ) {
                            val progress = if (total > 0) doneToday.toFloat() / total.toFloat() else 0f
                            val arcWidth = size.width
                            val arcHeight = size.height
                            val radius = arcWidth / 2f * 0.85f
                            val centerX = arcWidth / 2f
                            val centerY = arcHeight

                            // Background arc
                            drawArc(
                                color = Color(0xFFF0EBDE),
                                startAngle = 180f,
                                sweepAngle = 180f,
                                useCenter = false,
                                style = Stroke(width = 6.dp.toPx(), cap = StrokeCap.Round),
                                topLeft = Offset(centerX - radius, centerY - radius),
                                size = androidx.compose.ui.geometry.Size(radius * 2, radius * 2)
                            )

                            // Progress arc
                            if (progress > 0) {
                                drawArc(
                                    color = Color(0xFF1A1A1A),
                                    startAngle = 180f,
                                    sweepAngle = 180f * progress,
                                    useCenter = false,
                                    style = Stroke(width = 6.dp.toPx(), cap = StrokeCap.Round),
                                    topLeft = Offset(centerX - radius, centerY - radius),
                                    size = androidx.compose.ui.geometry.Size(radius * 2, radius * 2)
                                )
                            }
                        }

                        // Number centered above arc baseline
                        Row(
                            verticalAlignment = Alignment.Bottom,
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            modifier = Modifier.padding(bottom = 4.dp)
                        ) {
                            Text(
                                text = "$doneToday",
                                fontSize = 38.sp,
                                fontWeight = FontWeight.Medium,
                                letterSpacing = (-1.5).sp,
                                lineHeight = 38.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "/ $total",
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(bottom = 4.dp)
                            )
                        }
                    }
                }
            }

            // Upcoming tasks list
            BentoCardWithBorder(
                modifier = Modifier.fillMaxWidth(),
                onClick = onNavigateToTasks,
                hasBorder = true
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            Icons.Default.CalendarToday,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "UP NEXT",
                            fontSize = 11.sp,
                            letterSpacing = 0.14.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    TextButton(
                        onClick = onNavigateToTasks,
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text(
                            text = "All",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Icon(
                            Icons.Default.ChevronRight,
                            contentDescription = null,
                            modifier = Modifier.size(12.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                val upcomingTasks = topTasks.drop(1)

                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    if (upcomingTasks.isEmpty() && topTasks.isNotEmpty()) {
                        // Only show when there's 1 task (shown in big card) but no more upcoming
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Icon(
                                Icons.Default.CheckCircle,
                                contentDescription = null,
                                modifier = Modifier.size(32.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
                            )
                            Text(
                                text = "All caught up!",
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontWeight = FontWeight.Medium
                            )
                            OutlinedButton(
                                onClick = onNavigateToTasks,
                                shape = RoundedCornerShape(12.dp),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                            ) {
                                Icon(
                                    Icons.Default.Add,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Add more",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    } else if (upcomingTasks.isNotEmpty()) {
                        upcomingTasks.forEach { task ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(20.dp)
                                    .border(
                                        width = 1.5.dp,
                                        color = MaterialTheme.colorScheme.outlineVariant,
                                        shape = CircleShape
                                    )
                                    .clickable { taskViewModel.toggleTask(task.id) }
                            )

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = task.title,
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    maxLines = 1
                                )
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = task.time,
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 11.sp,
                                        letterSpacing = 0.1.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Box(
                                        modifier = Modifier
                                            .size(3.dp)
                                            .clip(CircleShape)
                                            .background(
                                                MaterialTheme.colorScheme.onSurfaceVariant.copy(
                                                    alpha = 0.5f
                                                )
                                            )
                                    )
                                    Text(
                                        text = task.tag,
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )

                                    if (!task.notes.isNullOrBlank()) {
                                        Box(
                                            modifier = Modifier
                                                .size(3.dp)
                                                .clip(CircleShape)
                                                .background(
                                                    MaterialTheme.colorScheme.onSurfaceVariant.copy(
                                                        alpha = 0.5f
                                                    )
                                                )
                                        )
                                        Icon(
                                            Icons.Default.Description,
                                            contentDescription = "Has notes",
                                            modifier = Modifier.size(11.dp),
                                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }

                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(
                                        when (task.priority) {
                                            com.example.erlangga.data.models.TaskPriority.HIGH -> MaterialTheme.colorScheme.error
                                            com.example.erlangga.data.models.TaskPriority.MEDIUM -> MaterialTheme.colorScheme.primary
                                            else -> MaterialTheme.colorScheme.onSurfaceVariant.copy(
                                                alpha = 0.4f
                                            )
                                        }
                                    )
                            )
                        }
                    }
                    }
                }
            }
        }
    }
}

@Composable
fun BentoCardWithBorder(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    hasBorder: Boolean = false,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier
            .height(200.dp)
            .then(
                if (hasBorder) Modifier.border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.outline,
                    shape = RoundedCornerShape(24.dp)
                ) else Modifier
            )
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(18.dp),
            content = content
        )
    }
}

@Composable
fun BentoCardAlt(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier
            .height(200.dp)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(18.dp),
            content = content
        )
    }
}
