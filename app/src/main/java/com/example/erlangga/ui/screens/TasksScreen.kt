package com.example.erlangga.ui.screens

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.erlangga.data.models.Task
import com.example.erlangga.data.models.mockTasks
import androidx.compose.ui.zIndex
import kotlin.math.abs
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.erlangga.viewmodels.TaskViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TasksScreen(
    taskViewModel: TaskViewModel = viewModel()
) {
    val tasks by taskViewModel.tasks.collectAsState()
    val tasksState by taskViewModel.tasksState.collectAsState()
    var filter by remember { mutableStateOf("today") }
    var showAddTaskModal by remember { mutableStateOf(false) }

    // Add LazyListState to manage scroll position
    val listState = rememberLazyListState()

    val activeTasks = remember(tasks) { tasks.filter { !it.done } }
    val completedTasks = remember(tasks) { tasks.filter { it.done } }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Header
            Column(
                modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 14.dp, bottom = 8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column {
                        Text(
                            text = "Saturday · ${tasks.size} tasks",
                            fontSize = 12.sp,
                            letterSpacing = 0.1.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "Today",
                            fontSize = 30.sp,
                            fontWeight = FontWeight.Medium,
                            letterSpacing = (-0.6).sp,
                            lineHeight = 31.5.sp,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }

                    IconButton(
                        onClick = { /* TODO: More options */ },
                        modifier = Modifier
                            .size(38.dp)
                            .background(
                                MaterialTheme.colorScheme.surfaceVariant,
                                CircleShape
                            )
                    ) {
                        Icon(
                            Icons.Default.MoreVert,
                            contentDescription = "More",
                            modifier = Modifier.size(18.dp),
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Filter chips
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(bottom = 8.dp)
                ) {
                    listOf("today", "week", "all").forEach { filterOption ->
                        FilterChip(
                            selected = filter == filterOption,
                            onClick = { filter = filterOption },
                            label = {
                                Text(
                                    text = filterOption.replaceFirstChar { it.uppercase() },
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    letterSpacing = 0.1.sp
                                )
                            },
                            enabled = true,
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.onBackground,
                                selectedLabelColor = MaterialTheme.colorScheme.background,
                                containerColor = Color.Transparent,
                                labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                            ),
                            border = if (filter == filterOption) null else FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = filter == filterOption,
                                borderColor = MaterialTheme.colorScheme.outline
                            ),
                            shape = RoundedCornerShape(99.dp)
                        )
                    }
                }
            }

            // Scrollable list
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(top = 8.dp, bottom = 80.dp),
                verticalArrangement = Arrangement.spacedBy(0.dp)
            ) {
                // Active tasks section
                item(key = "active_header", contentType = "header") {
                    Text(
                        text = "ACTIVE · ${activeTasks.size}",
                        fontSize = 10.5.sp,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 0.14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(start = 4.dp, top = 8.dp, bottom = 10.dp)
                    )
                }

                itemsIndexed(
                    items = activeTasks,
                    key = { _, task -> "active_${task.id}" }
                ) { _, task ->
                    SwipeableTaskRow(
                        task = task,
                        onToggleDone = { taskId ->
                            taskViewModel.toggleTask(taskId)
                        },
                        onDelete = { taskId ->
                            taskViewModel.deleteTask(taskId)
                        },
                        modifier = Modifier.padding(bottom = 10.dp)
                    )
                }

                // Completed tasks section
                if (completedTasks.isNotEmpty()) {
                    item(key = "completed_header", contentType = "header") {
                        Text(
                            text = "COMPLETED · ${completedTasks.size}",
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.SemiBold,
                            letterSpacing = 0.14.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(start = 4.dp, top = 12.dp, bottom = 10.dp)
                        )
                    }

                    itemsIndexed(
                        items = completedTasks,
                        key = { _, task -> "completed_${task.id}" }
                    ) { _, task ->
                        SwipeableTaskRow(
                            task = task,
                            onToggleDone = { taskId ->
                                taskViewModel.toggleTask(taskId)
                            },
                            onDelete = { taskId ->
                                taskViewModel.deleteTask(taskId)
                            },
                            modifier = Modifier.padding(bottom = 10.dp)
                        )
                    }
                }
            }
        }

        // FAB
        FloatingActionButton(
            onClick = { showAddTaskModal = true },
            containerColor = MaterialTheme.colorScheme.onBackground,
            contentColor = MaterialTheme.colorScheme.background,
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(20.dp)
                .size(60.dp)
        ) {
            Icon(
                Icons.Default.Add,
                contentDescription = "Add Task",
                modifier = Modifier.size(26.dp)
            )
        }

        // Add Task Modal
        if (showAddTaskModal) {
            AddTaskScreen(
                onTaskAdded = {
                    showAddTaskModal = false
                },
                onNavigateBack = { showAddTaskModal = false },
                taskViewModel = taskViewModel
            )
        }
    }
}

@Composable
fun SwipeableTaskRow(
    task: Task,
    onToggleDone: (Int) -> Unit,
    onDelete: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var offsetX by remember(task.id) { mutableFloatStateOf(0f) }
    var isDragging by remember(task.id) { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
    ) {
        // Reveal layer (background)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(68.dp)
                .background(
                    color = when {
                        offsetX > 20 -> MaterialTheme.colorScheme.tertiary // Success green
                        offsetX < -20 -> MaterialTheme.colorScheme.error // Danger red
                        else -> Color.Transparent
                    },
                    shape = RoundedCornerShape(16.dp)
                )
                .padding(horizontal = 20.dp),
            contentAlignment = if (offsetX > 20) Alignment.CenterStart else Alignment.CenterEnd
        ) {
            if (offsetX > 20) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Check,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                        tint = Color.White
                    )
                    Text(
                        text = "Done",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                }
            } else if (offsetX < -20) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Archive",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                        tint = Color.White
                    )
                }
            }
        }

        // Task card (foreground)
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .offset(x = offsetX.dp)
                .pointerInput(task.id) {
                    detectHorizontalDragGestures(
                        onDragStart = { isDragging = true },
                        onDragEnd = {
                            isDragging = false
                            when {
                                offsetX > 100 -> {
                                    onToggleDone(task.id)
                                    offsetX = 0f
                                }
                                offsetX < -100 -> {
                                    onDelete(task.id)
                                    offsetX = 0f
                                }
                                else -> offsetX = 0f
                            }
                        },
                        onDragCancel = {
                            isDragging = false
                            offsetX = 0f
                        },
                        onHorizontalDrag = { _, dragAmount ->
                            val newOffset = offsetX + dragAmount / 2
                            offsetX = newOffset.coerceIn(-160f, 160f)
                        }
                    )
                },
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(68.dp)
                    .padding(horizontal = 16.dp, vertical = 18.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Checkbox
                Box(
                    modifier = Modifier
                        .size(22.dp)
                        .clip(CircleShape)
                        .background(
                            if (task.done) MaterialTheme.colorScheme.onBackground
                            else Color.Transparent
                        )
                        .border(
                            width = if (task.done) 0.dp else 1.5.dp,
                            color = if (task.done) Color.Transparent
                            else MaterialTheme.colorScheme.outlineVariant,
                            shape = CircleShape
                        )
                        .clickable { onToggleDone(task.id) },
                    contentAlignment = Alignment.Center
                ) {
                    if (task.done) {
                        Icon(
                            Icons.Default.Check,
                            contentDescription = null,
                            modifier = Modifier.size(12.dp),
                            tint = MaterialTheme.colorScheme.background
                        )
                    }
                }

                // Task info
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = task.title,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        letterSpacing = (-0.1).sp,
                        color = if (task.done) MaterialTheme.colorScheme.onSurfaceVariant
                        else MaterialTheme.colorScheme.onSurface,
                        textDecoration = if (task.done) TextDecoration.LineThrough else null,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(top = 2.dp)
                    ) {
                        Text(
                            text = task.time,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.5.sp,
                            letterSpacing = 0.1.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Box(
                            modifier = Modifier
                                .size(3.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f))
                        )
                        Text(
                            text = task.tag,
                            fontSize = 11.5.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        if (!task.notes.isNullOrBlank()) {
                            Box(
                                modifier = Modifier
                                    .size(3.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f))
                            )
                            Icon(
                                Icons.Default.Description,
                                contentDescription = "Has notes",
                                modifier = Modifier.size(12.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                // Priority badge
                Surface(
                    color = when (task.priority) {
                        com.example.erlangga.data.models.TaskPriority.HIGH -> MaterialTheme.colorScheme.error.copy(alpha = 0.13f)
                        com.example.erlangga.data.models.TaskPriority.MEDIUM -> MaterialTheme.colorScheme.primary.copy(alpha = 0.27f)
                        else -> MaterialTheme.colorScheme.surfaceVariant
                    },
                    shape = RoundedCornerShape(99.dp)
                ) {
                    Text(
                        text = task.priority.name,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        fontSize = 10.5.sp,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 0.2.sp,
                        color = when (task.priority) {
                            com.example.erlangga.data.models.TaskPriority.HIGH -> MaterialTheme.colorScheme.error
                            com.example.erlangga.data.models.TaskPriority.MEDIUM -> MaterialTheme.colorScheme.onSurfaceVariant
                            else -> MaterialTheme.colorScheme.onSurfaceVariant
                        }
                    )
                }
            }
        }
    }
}
