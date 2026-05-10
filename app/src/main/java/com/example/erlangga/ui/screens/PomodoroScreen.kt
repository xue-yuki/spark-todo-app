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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.erlangga.viewmodels.PomodoroViewModel
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun PomodoroScreen(
    onNavigateBack: () -> Unit,
    pomodoroViewModel: PomodoroViewModel = viewModel()
) {
    val isRunning by pomodoroViewModel.isRunning.collectAsState()
    val timeLeft by pomodoroViewModel.timeLeft.collectAsState()
    val selectedMode by pomodoroViewModel.selectedMode.collectAsState()
    val customMinutes by pomodoroViewModel.customMinutes.collectAsState()
    val totalTime = pomodoroViewModel.totalTime
    val progress = 1f - (timeLeft.toFloat() / totalTime)

    var showCustomDialog by remember { mutableStateOf(false) }

    // Custom duration dialog
    if (showCustomDialog) {
        var inputText by remember { mutableStateOf(customMinutes.toString()) }
        AlertDialog(
            onDismissRequest = { showCustomDialog = false },
            title = { Text("Custom Duration", fontWeight = FontWeight.SemiBold) },
            text = {
                Column {
                    Text("Enter duration in minutes (1–120):", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = inputText,
                        onValueChange = { if (it.length <= 3 && it.all { c -> c.isDigit() }) inputText = it },
                        label = { Text("Minutes") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val mins = inputText.toIntOrNull() ?: customMinutes
                        pomodoroViewModel.setCustomMinutes(mins)
                        pomodoroViewModel.setMode(3)
                        showCustomDialog = false
                    },
                    shape = RoundedCornerShape(10.dp)
                ) { Text("Set") }
            },
            dismissButton = {
                TextButton(onClick = { showCustomDialog = false }) { Text("Cancel") }
            },
            shape = RoundedCornerShape(20.dp)
        )
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
        Spacer(modifier = Modifier.height(14.dp))
        Surface(modifier = Modifier.fillMaxWidth(), color = MaterialTheme.colorScheme.surfaceVariant, shape = RoundedCornerShape(16.dp)) {
            Row(modifier = Modifier.padding(4.dp), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                val modes = listOf(
                    Triple(0, "Focus", "25m"),
                    Triple(1, "Short", "5m"),
                    Triple(2, "Long", "15m"),
                    Triple(3, "Custom", "${customMinutes}m")
                )
                modes.forEach { (index, label, duration) ->
                    Box(
                        modifier = Modifier.weight(1f).clip(RoundedCornerShape(12.dp))
                            .background(if (selectedMode == index) MaterialTheme.colorScheme.onBackground else Color.Transparent)
                            .clickable {
                                if (index == 3) showCustomDialog = true
                                else pomodoroViewModel.setMode(index)
                            }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(label, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = if (selectedMode == index) MaterialTheme.colorScheme.background else MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(duration, fontSize = 10.sp, color = if (selectedMode == index) MaterialTheme.colorScheme.background.copy(alpha = 0.6f) else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f))
                        }
                    }
                }
            }
        }
        Box(modifier = Modifier.weight(1f).fillMaxWidth().padding(vertical = 8.dp), contentAlignment = Alignment.Center) {
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
                onClick = { pomodoroViewModel.reset() },
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
                onClick = { pomodoroViewModel.toggleTimer() },
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
