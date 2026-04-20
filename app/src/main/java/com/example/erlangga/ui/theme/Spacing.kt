package com.example.erlangga.ui.theme

import androidx.compose.ui.unit.dp

// Spark Density Values
data class Density(
    val taskHeight: Int,
    val taskPadding: Int,
    val cardPadding: Int,
    val gap: Int,
    val titleSize: Int
)

val CompactDensity = Density(
    taskHeight = 56,
    taskPadding = 14,
    cardPadding = 16,
    gap = 10,
    titleSize = 15
)

val ComfyDensity = Density(
    taskHeight = 68,
    taskPadding = 18,
    cardPadding = 20,
    gap = 14,
    titleSize = 16
)

// Common spacing values
object Spacing {
    val extraSmall = 4.dp
    val small = 8.dp
    val medium = 12.dp
    val large = 16.dp
    val extraLarge = 20.dp
    val xxLarge = 24.dp
}
