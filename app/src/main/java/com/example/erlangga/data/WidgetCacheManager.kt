package com.example.erlangga.data

import android.content.Context
import com.example.erlangga.data.models.Task
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

data class CachedTask(
    val id: Int,
    val title: String,
    val tag: String,
    val time: String,
    val priority: String
)

object WidgetCacheManager {
    private const val PREFS_NAME = "spark_widget_prefs"
    private const val KEY_TASKS = "widget_tasks"
    private const val KEY_TASK_COUNT = "widget_task_count"
    private val gson = Gson()

    fun saveTasks(context: Context, tasks: List<Task>) {
        val activeTasks = tasks.filter { !it.done }.take(4).map { task ->
            CachedTask(
                id = task.id,
                title = task.title,
                tag = task.tag,
                time = task.time,
                priority = task.priority.name
            )
        }
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY_TASKS, gson.toJson(activeTasks))
            .putInt(KEY_TASK_COUNT, tasks.filter { !it.done }.size)
            .apply()
    }

    fun getTasks(context: Context): List<CachedTask> {
        val json = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getString(KEY_TASKS, null) ?: return emptyList()
        return try {
            val type = object : TypeToken<List<CachedTask>>() {}.type
            gson.fromJson(json, type)
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun getTaskCount(context: Context): Int {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getInt(KEY_TASK_COUNT, 0)
    }
}
