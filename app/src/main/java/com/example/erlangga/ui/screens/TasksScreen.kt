package com.example.erlangga.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.ui.zIndex
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Locale
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
    var searchQuery by remember { mutableStateOf("") }
    var showAddTaskModal by remember { mutableStateOf(false) }
    var taskToEdit by remember { mutableStateOf<Task?>(null) }
    var showCalendar by remember { mutableStateOf(false) }
    var isCalendarExpanded by remember { mutableStateOf(false) }
    var selectedCalendarDate by remember { mutableStateOf<LocalDate?>(null) }

    val listState = rememberLazyListState()

    val today = LocalDate.now()
    val filteredTasks = remember(tasks, filter, searchQuery, selectedCalendarDate) {
        when {
            selectedCalendarDate != null -> tasks.filter { task ->
                task.dueDate?.substringBefore("T")?.let {
                    runCatching { LocalDate.parse(it) }.getOrNull()
                } == selectedCalendarDate
            }
            filter == "today" -> tasks.filter { task ->
                val date = task.dueDate?.substringBefore("T")?.let {
                    runCatching { LocalDate.parse(it) }.getOrNull()
                }
                date == today
            }
            filter == "week" -> tasks.filter { task ->
                val date = task.dueDate?.substringBefore("T")?.let {
                    runCatching { LocalDate.parse(it) }.getOrNull()
                }
                date != null && !date.isBefore(today) && !date.isAfter(today.plusDays(6))
            }
            else -> tasks
        }.let { list ->
            if (searchQuery.isBlank()) list
            else list.filter { task ->
                task.title.contains(searchQuery, ignoreCase = true) ||
                task.tag?.contains(searchQuery, ignoreCase = true) == true ||
                task.notes?.contains(searchQuery, ignoreCase = true) == true
            }
        }
    }
    val activeTasks = remember(filteredTasks) { filteredTasks.filter { !it.done } }
    val completedTasks = remember(filteredTasks) { filteredTasks.filter { it.done } }

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
                            text = if (selectedCalendarDate != null)
                                selectedCalendarDate!!.format(DateTimeFormatter.ofPattern("EEEE, d MMM", Locale.ENGLISH))
                            else
                                "${today.format(DateTimeFormatter.ofPattern("EEEE", Locale.ENGLISH))} · ${tasks.size} tasks",
                            fontSize = 12.sp,
                            letterSpacing = 0.1.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = if (selectedCalendarDate != null) "Selected" else "Tasks",
                            fontSize = 30.sp,
                            fontWeight = FontWeight.Medium,
                            letterSpacing = (-0.6).sp,
                            lineHeight = 31.5.sp,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        if (selectedCalendarDate != null) {
                            IconButton(
                                onClick = { selectedCalendarDate = null },
                                modifier = Modifier
                                    .size(38.dp)
                                    .background(MaterialTheme.colorScheme.surfaceVariant, CircleShape)
                            ) {
                                Icon(
                                    Icons.Default.Close,
                                    contentDescription = "Clear date",
                                    modifier = Modifier.size(16.dp),
                                    tint = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                        IconButton(
                            onClick = { showCalendar = !showCalendar },
                            modifier = Modifier
                                .size(38.dp)
                                .background(
                                    if (showCalendar) MaterialTheme.colorScheme.onBackground
                                    else MaterialTheme.colorScheme.surfaceVariant,
                                    CircleShape
                                )
                        ) {
                            Icon(
                                Icons.Default.CalendarToday,
                                contentDescription = "Calendar",
                                modifier = Modifier.size(18.dp),
                                tint = if (showCalendar) MaterialTheme.colorScheme.background
                                       else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }

                // Calendar strip
                AnimatedVisibility(
                    visible = showCalendar,
                    enter = expandVertically(spring(stiffness = Spring.StiffnessMedium)) + fadeIn(),
                    exit = shrinkVertically(spring(stiffness = Spring.StiffnessMedium)) + fadeOut()
                ) {
                    Column {
                        Spacer(modifier = Modifier.height(14.dp))
                        CalendarStrip(
                            tasks = tasks,
                            selectedDate = selectedCalendarDate,
                            isExpanded = isCalendarExpanded,
                            onExpandedChange = { isCalendarExpanded = it },
                            onDateSelected = { date ->
                                selectedCalendarDate = if (selectedCalendarDate == date) null else date
                            }
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Filter chips — hidden when calendar expanded or date selected
                AnimatedVisibility(visible = !isCalendarExpanded && selectedCalendarDate == null) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(bottom = 10.dp)
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

                // Search bar — hidden when calendar expanded
                AnimatedVisibility(visible = !isCalendarExpanded) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 4.dp),
                    placeholder = {
                        Text(
                            "Search tasks...",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                        )
                    },
                    leadingIcon = {
                        Icon(
                            Icons.Default.Search,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(
                                    Icons.Default.Close,
                                    contentDescription = "Clear",
                                    modifier = Modifier.size(16.dp),
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.onBackground,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface
                    ),
                    textStyle = androidx.compose.ui.text.TextStyle(
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                )
                } // end AnimatedVisibility search bar
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
                        onToggleDone = { taskId -> taskViewModel.toggleTask(taskId) },
                        onDelete = { taskId -> taskViewModel.deleteTask(taskId) },
                        onEdit = { taskToEdit = task },
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
                            onToggleDone = { taskId -> taskViewModel.toggleTask(taskId) },
                            onDelete = { taskId -> taskViewModel.deleteTask(taskId) },
                            onEdit = { taskToEdit = task },
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
                onTaskAdded = { showAddTaskModal = false },
                onNavigateBack = { showAddTaskModal = false },
                taskViewModel = taskViewModel
            )
        }

        // Edit Task Modal
        taskToEdit?.let { task ->
            EditTaskScreen(
                task = task,
                onTaskUpdated = { taskToEdit = null },
                onNavigateBack = { taskToEdit = null },
                taskViewModel = taskViewModel
            )
        }
    }
}

@Composable
fun CalendarStrip(
    tasks: List<Task>,
    selectedDate: LocalDate?,
    isExpanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    onDateSelected: (LocalDate) -> Unit,
    modifier: Modifier = Modifier
) {
    val today = LocalDate.now()
    var displayMonth by remember { mutableStateOf(YearMonth.from(selectedDate ?: today)) }

    val taskCountByDate = remember(tasks) {
        tasks.groupBy { task ->
            task.dueDate?.substringBefore("T")?.let {
                runCatching { LocalDate.parse(it) }.getOrNull()
            }
        }.filterKeys { it != null }
            .mapKeys { it.key!! }
            .mapValues { it.value.size }
    }

    Column(modifier = modifier) {
        // Strip header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = displayMonth.format(DateTimeFormatter.ofPattern("MMMM yyyy", Locale.ENGLISH)),
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                AnimatedVisibility(visible = isExpanded) {
                    Row {
                        IconButton(
                            onClick = { displayMonth = displayMonth.minusMonths(1) },
                            modifier = Modifier.size(30.dp)
                        ) {
                            Icon(Icons.Default.ChevronLeft, null,
                                modifier = Modifier.size(16.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        IconButton(
                            onClick = { displayMonth = displayMonth.plusMonths(1) },
                            modifier = Modifier.size(30.dp)
                        ) {
                            Icon(Icons.Default.ChevronRight, null,
                                modifier = Modifier.size(16.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
                IconButton(
                    onClick = { onExpandedChange(!isExpanded) },
                    modifier = Modifier.size(30.dp)
                ) {
                    Icon(
                        if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Day-of-week labels (Sun–Sat)
        Row(modifier = Modifier.fillMaxWidth()) {
            listOf("S", "M", "T", "W", "T", "F", "S").forEach { label ->
                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                    Text(
                        text = label,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.45f)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Week strip
        AnimatedVisibility(
            visible = !isExpanded,
            enter = expandVertically(spring(stiffness = Spring.StiffnessMediumLow)) + fadeIn(),
            exit = shrinkVertically(spring(stiffness = Spring.StiffnessMediumLow)) + fadeOut()
        ) {
            val anchor = selectedDate ?: today
            val dayOfWeek = anchor.dayOfWeek.value % 7  // Sun=0, Mon=1,…,Sat=6
            val weekStart = anchor.minusDays(dayOfWeek.toLong())
            Row(modifier = Modifier.fillMaxWidth()) {
                (0..6).forEach { i ->
                    val date = weekStart.plusDays(i.toLong())
                    CalendarDayCell(
                        date = date,
                        isSelected = date == selectedDate,
                        isToday = date == today,
                        taskCount = taskCountByDate[date] ?: 0,
                        onClick = { onDateSelected(date) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Full month grid
        AnimatedVisibility(
            visible = isExpanded,
            enter = expandVertically(spring(stiffness = Spring.StiffnessMediumLow)) + fadeIn(),
            exit = shrinkVertically(spring(stiffness = Spring.StiffnessMediumLow)) + fadeOut()
        ) {
            Spacer(modifier = Modifier.height(2.dp))
            MonthCalendarGrid(
                yearMonth = displayMonth,
                selectedDate = selectedDate,
                today = today,
                taskCountByDate = taskCountByDate,
                onDateSelected = { date ->
                    onDateSelected(date)
                    displayMonth = YearMonth.from(date)
                }
            )
        }
    }
}

@Composable
fun CalendarDayCell(
    date: LocalDate,
    isSelected: Boolean,
    isToday: Boolean,
    taskCount: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val heatAlpha = when (taskCount) {
        0 -> 0f; 1 -> 0.18f; 2 -> 0.38f; 3 -> 0.58f; else -> 0.78f
    }
    val primaryColor = MaterialTheme.colorScheme.primary

    Column(
        modifier = modifier
            .padding(horizontal = 2.dp, vertical = 2.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(
                when {
                    isSelected -> MaterialTheme.colorScheme.onBackground
                    taskCount > 0 -> primaryColor.copy(alpha = heatAlpha)
                    else -> Color.Transparent
                }
            )
            .then(
                if (isToday && !isSelected) Modifier.border(
                    1.dp,
                    MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f),
                    RoundedCornerShape(10.dp)
                ) else Modifier
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
            .padding(vertical = 7.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(3.dp)
    ) {
        Text(
            text = date.dayOfMonth.toString(),
            fontSize = 13.sp,
            fontWeight = if (isSelected || isToday) FontWeight.SemiBold else FontWeight.Normal,
            color = when {
                isSelected -> MaterialTheme.colorScheme.background
                isToday -> MaterialTheme.colorScheme.onBackground
                else -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f)
            }
        )
        Box(modifier = Modifier.height(5.dp), contentAlignment = Alignment.Center) {
            if (taskCount > 0 && !isSelected) {
                Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                    repeat(minOf(taskCount, 3)) {
                        Box(
                            modifier = Modifier
                                .size(4.dp)
                                .clip(CircleShape)
                                .background(
                                    if (taskCount > 2) MaterialTheme.colorScheme.onBackground
                                    else primaryColor
                                )
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MonthCalendarGrid(
    yearMonth: YearMonth,
    selectedDate: LocalDate?,
    today: LocalDate,
    taskCountByDate: Map<LocalDate, Int>,
    onDateSelected: (LocalDate) -> Unit
) {
    val firstDay = yearMonth.atDay(1)
    val startOffset = firstDay.dayOfWeek.value % 7  // Sun=0
    val daysInMonth = yearMonth.lengthOfMonth()
    val rows = (startOffset + daysInMonth + 6) / 7

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        repeat(rows) { row ->
            Row(modifier = Modifier.fillMaxWidth()) {
                repeat(7) { col ->
                    val dayNum = row * 7 + col - startOffset + 1
                    if (dayNum < 1 || dayNum > daysInMonth) {
                        Box(modifier = Modifier.weight(1f))
                    } else {
                        val date = yearMonth.atDay(dayNum)
                        CalendarDayCell(
                            date = date,
                            isSelected = date == selectedDate,
                            isToday = date == today,
                            taskCount = taskCountByDate[date] ?: 0,
                            onClick = { onDateSelected(date) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SwipeableTaskRow(
    task: Task,
    onToggleDone: (Int) -> Unit,
    onDelete: (Int) -> Unit,
    onEdit: () -> Unit = {},
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
                .height(76.dp)
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
                .clickable { onEdit() }
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
                    .padding(horizontal = 16.dp, vertical = 14.dp),
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
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(top = 4.dp)
                    ) {
                        // Tag badge
                        Surface(
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = task.tag,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }

                        // Dot separator
                        Box(
                            modifier = Modifier
                                .size(3.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f))
                        )

                        // Time
                        Text(
                            text = task.time,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.5.sp,
                            letterSpacing = 0.1.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )

                        // Due date
                        if (!task.dueDate.isNullOrBlank()) {
                            Box(
                                modifier = Modifier
                                    .size(3.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f))
                            )
                            val formattedDate = try {
                                val dateStr = task.dueDate!!.substringBefore("T")
                                val parsed = java.time.LocalDate.parse(dateStr)
                                parsed.format(java.time.format.DateTimeFormatter.ofPattern("EEE, d MMM", java.util.Locale.ENGLISH))
                            } catch (e: Exception) { task.dueDate!! }
                            Text(
                                text = formattedDate,
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                        }

                        // Notes preview
                        if (!task.notes.isNullOrBlank()) {
                            Box(
                                modifier = Modifier
                                    .size(3.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f))
                            )
                            Text(
                                text = task.notes!!,
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.weight(1f, fill = false)
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
