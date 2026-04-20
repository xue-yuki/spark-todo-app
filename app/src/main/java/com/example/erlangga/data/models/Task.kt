package com.example.erlangga.data.models

import com.google.gson.annotations.SerializedName

data class Task(
    val id: Int,
    val title: String,
    val tag: String,
    val time: String,
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
    Task(1, "Review Q2 roadmap draft", "Work", "10:00", false, TaskPriority.HIGH),
    Task(2, "Buy oat milk & sourdough", "Errands", "12:30", false, TaskPriority.LOW),
    Task(3, "CS 312 — finish problem set", "Study", "14:00", false, TaskPriority.MEDIUM),
    Task(4, "Run 5k along the river", "Health", "18:00", false, TaskPriority.LOW),
    Task(5, "Call mom", "Personal", "20:00", true, TaskPriority.LOW),
    Task(6, "Draft retro notes for team", "Work", "09:00", true, TaskPriority.MEDIUM),
)
