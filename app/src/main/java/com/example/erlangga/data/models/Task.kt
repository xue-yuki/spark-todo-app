package com.example.erlangga.data.models

import com.google.gson.annotations.SerializedName

data class Task(
    val id: Int,
    val title: String,
    val tag: String,
    val time: String,
    @SerializedName("due_date")
    val dueDate: String? = null,
    val done: Boolean,
    val priority: TaskPriority,
    val notes: String? = null
)

enum class TaskPriority {
    @SerializedName("low")
    LOW,

    @SerializedName("med")
    MEDIUM,

    @SerializedName("high")
    HIGH
}

// Mock data for development
val mockTasks = listOf(
    Task(1, "Review Q2 roadmap draft", "Work", "10:00", "2026-04-20", false, TaskPriority.HIGH),
    Task(2, "Buy oat milk & sourdough", "Errands", "12:30", "2026-04-20", false, TaskPriority.LOW),
    Task(3, "CS 312 — finish problem set", "Study", "14:00", "2026-04-21", false, TaskPriority.MEDIUM),
    Task(4, "Run 5k along the river", "Health", "18:00", "2026-04-22", false, TaskPriority.LOW),
    Task(5, "Call mom", "Personal", "20:00", "2026-04-20", true, TaskPriority.LOW),
    Task(6, "Draft retro notes for team", "Work", "09:00", "2026-04-19", true, TaskPriority.MEDIUM),
)
