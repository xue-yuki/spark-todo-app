package com.example.erlangga.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun PomodoroScreen(
    onNavigateBack: () -> Unit
) {
    var isRunning by remember { mutableStateOf(false) }
    var timeLeft by remember { mutableStateOf(1500) }
    var selectedMode by remember { mutableStateOf(0) }
    val totalTime = when (selectedMode) {
        0 -> 1500 // 25 minutes
        1 -> 300  // 5 minutes
        2 -> 900  // 15 minutes
        else -> 1500
    }
    val progress = 1f - (timeLeft.toFloat() / totalTime)

    // Countdown timer effect
    LaunchedEffect(isRunning, selectedMode) {
        if (!isRunning) return@LaunchedEffect

        while (isRunning && timeLeft > 0) {
            kotlinx.coroutines.delay(1000L)
            timeLeft--
        }

        if (timeLeft == 0) {
            isRunning = false
            // Timer completed - could add notification here
        }
    }

    // Reset timer when mode changes
    LaunchedEffect(selectedMode) {
        timeLeft = totalTime
        isRunning = false
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 20.dp, vertical = 14.dp)
            .padding(bottom = 24.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Column {
                Text("Focus session · 1/4", fontSize = 12.sp, letterSpacing = 0.1.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("Pomodoro", fontSize = 30.sp, fontWeight = FontWeight.Medium, letterSpacing = (-0.6).sp, lineHeight = 31.5.sp, color = MaterialTheme.colorScheme.onBackground)
            }
            TextButton(
                onClick = { },
                colors = ButtonDefaults.textButtonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant, contentColor = MaterialTheme.colorScheme.onSurface),
                shape = RoundedCornerShape(99.dp),
                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp)
            ) {
                Icon(Icons.Default.Star, contentDescription = null, modifier = Modifier.size(12.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Immerse", fontSize = 12.sp, fontWeight = FontWeight.Medium)
            }
        }
        Spacer(modifier = Modifier.height(18.dp))
        Surface(modifier = Modifier.fillMaxWidth(), color = MaterialTheme.colorScheme.surfaceVariant, shape = RoundedCornerShape(99.dp)) {
            Row(modifier = Modifier.padding(4.dp), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                listOf("Focus" to "25 min", "Short" to "5 min", "Long" to "15 min").forEachIndexed { index, (label, duration) ->
                    Box(
                        modifier = Modifier.weight(1f).clip(RoundedCornerShape(99.dp))
                            .background(if (selectedMode == index) MaterialTheme.colorScheme.onBackground else Color.Transparent)
                            .clickable { selectedMode = index }.padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                            Text(label, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = if (selectedMode == index) MaterialTheme.colorScheme.background else MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(duration, fontSize = 10.sp, color = if (selectedMode == index) MaterialTheme.colorScheme.background.copy(alpha = 0.5f) else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f))
                        }
                    }
                }
            }
        }
        Box(modifier = Modifier.weight(1f).fillMaxWidth().padding(vertical = 20.dp), contentAlignment = Alignment.Center) {
            Canvas(modifier = Modifier.size(300.dp)) {
                val radius = 130.dp.toPx()
                val center = Offset(size.width / 2, size.height / 2)
                drawCircle(color = Color(0xFFF0EBDE), radius = radius, center = center, style = Stroke(width = 4.dp.toPx()))
                drawArc(color = Color(0xFF1A1A1A), startAngle = -90f, sweepAngle = 360f * progress, useCenter = false, style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round), topLeft = Offset(center.x - radius, center.y - radius), size = androidx.compose.ui.geometry.Size(radius * 2, radius * 2))
                for (i in 0 until 60) {
                    val angle = (i / 60.0) * 2 * PI - PI / 2
                    val isMajor = i % 5 == 0
                    val tickStart = radius - if (isMajor) 12.dp.toPx() else 6.dp.toPx()
                    val tickEnd = radius
                    drawLine(color = Color(0xFF1A1A1A).copy(alpha = if (isMajor) 0.2f else 0.1f), start = Offset(center.x + cos(angle).toFloat() * tickStart, center.y + sin(angle).toFloat() * tickStart), end = Offset(center.x + cos(angle).toFloat() * tickEnd, center.y + sin(angle).toFloat() * tickEnd), strokeWidth = if (isMajor) 2.dp.toPx() else 1.dp.toPx())
                }
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(String.format("%02d:%02d", timeLeft / 60, timeLeft % 60), fontFamily = FontFamily.Monospace, fontSize = 72.sp, fontWeight = FontWeight.Light, letterSpacing = (-2).sp, color = MaterialTheme.colorScheme.onBackground)
                Text("minutes left", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(top = 8.dp))
            }
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedButton(
                onClick = {
                    isRunning = false
                    timeLeft = totalTime
                },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = Color.Transparent,
                    contentColor = MaterialTheme.colorScheme.onSurface
                ),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                shape = RoundedCornerShape(16.dp),
                contentPadding = PaddingValues(vertical = 16.dp)
            ) {
                Text("Reset", fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
            }
            Button(
                onClick = { isRunning = !isRunning },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.onBackground,
                    contentColor = MaterialTheme.colorScheme.background
                ),
                shape = RoundedCornerShape(16.dp),
                contentPadding = PaddingValues(vertical = 16.dp)
            ) {
                Icon(
                    if (isRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    if (isRunning) "Pause" else "Start",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}
