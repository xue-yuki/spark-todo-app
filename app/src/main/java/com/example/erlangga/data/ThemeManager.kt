package com.example.erlangga.data

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

object ThemeManager {
    private const val PREFS_NAME = "spark_theme_prefs"
    private const val KEY_THEME = "theme_mode"

    // "light", "dark", "system"
    private val _themeMode = MutableStateFlow("system")
    val themeMode: StateFlow<String> = _themeMode

    fun init(context: Context) {
        val saved = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getString(KEY_THEME, "system") ?: "system"
        _themeMode.value = saved
    }

    fun setTheme(context: Context, mode: String) {
        _themeMode.value = mode
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY_THEME, mode)
            .apply()
    }
}
