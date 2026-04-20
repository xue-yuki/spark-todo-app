package com.example.erlangga.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val SparkDarkColorScheme = darkColorScheme(
    primary = SparkDarkAccent,
    onPrimary = SparkDarkAccentInk,
    secondary = SparkDarkInkMuted,
    onSecondary = SparkDarkBg,
    tertiary = SparkDarkSuccess,
    onTertiary = SparkDarkBg,
    background = SparkDarkBg,
    onBackground = SparkDarkInk,
    surface = SparkDarkCard,
    onSurface = SparkDarkInk,
    surfaceVariant = SparkDarkCardAlt,
    onSurfaceVariant = SparkDarkInkMuted,
    error = SparkDarkDanger,
    onError = SparkDarkBg,
    outline = SparkDarkLine,
    outlineVariant = SparkDarkLineStrong
)

private val SparkLightColorScheme = lightColorScheme(
    primary = SparkLightAccent,
    onPrimary = SparkLightAccentInk,
    secondary = SparkLightInkMuted,
    onSecondary = SparkLightBg,
    tertiary = SparkLightSuccess,
    onTertiary = SparkLightBg,
    background = SparkLightBg,
    onBackground = SparkLightInk,
    surface = SparkLightCard,
    onSurface = SparkLightInk,
    surfaceVariant = SparkLightCardAlt,
    onSurfaceVariant = SparkLightInkMuted,
    error = SparkLightDanger,
    onError = SparkLightBg,
    outline = SparkLightLine,
    outlineVariant = SparkLightLineStrong
)

@Composable
fun SparkTodoAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    // Spark uses custom design tokens, not dynamic colors
    val colorScheme = if (darkTheme) {
        SparkDarkColorScheme
    } else {
        SparkLightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}