package com.example.erlangga.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// Spark uses Space Grotesk and JetBrains Mono
// TODO: Download and add these fonts to res/font/ for exact match
// For now, using system fonts as fallback
val SparkSansFont = FontFamily.SansSerif
val SparkMonoFont = FontFamily.Monospace

val Typography = Typography(
    // Display styles
    displayLarge = TextStyle(
        fontFamily = SparkSansFont,
        fontWeight = FontWeight.Medium,
        fontSize = 26.sp,
        lineHeight = 31.2.sp,
        letterSpacing = (-0.5).sp
    ),

    // Title styles
    titleLarge = TextStyle(
        fontFamily = SparkSansFont,
        fontWeight = FontWeight.Medium,
        fontSize = 24.sp,
        lineHeight = 28.8.sp,
        letterSpacing = (-0.4).sp
    ),
    titleMedium = TextStyle(
        fontFamily = SparkSansFont,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        lineHeight = 19.2.sp,
        letterSpacing = 0.1.sp
    ),

    // Body styles
    bodyLarge = TextStyle(
        fontFamily = SparkSansFont,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = SparkSansFont,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.sp
    ),
    bodySmall = TextStyle(
        fontFamily = SparkSansFont,
        fontWeight = FontWeight.Normal,
        fontSize = 13.sp,
        lineHeight = 18.sp,
        letterSpacing = 0.1.sp
    ),

    // Label styles
    labelLarge = TextStyle(
        fontFamily = SparkSansFont,
        fontWeight = FontWeight.Medium,
        fontSize = 13.sp,
        lineHeight = 18.sp,
        letterSpacing = 0.1.sp
    ),
    labelMedium = TextStyle(
        fontFamily = SparkSansFont,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.12.sp
    ),
    labelSmall = TextStyle(
        fontFamily = SparkSansFont,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.18.sp
    )
)