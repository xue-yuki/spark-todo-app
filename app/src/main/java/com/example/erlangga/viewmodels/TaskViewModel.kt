package com.example.erlangga.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.erlangga.data.api.CreateTaskRequest
import com.example.erlangga.data.api.RetrofitClient
import com.example.erlangga.data.api.UpdateTaskRequest
import com.example.erlangga.data.models.Task
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class TaskViewModel : ViewModel() {
    private val _tasksState = MutableStateFlow<TasksState>(TasksState.Loading)
    val tasksState: StateFlow<TasksState> = _tasksState

    private val _tasks = MutableStateFlow<List<Task>>(emptyList())
    val tasks: StateFlow<List<Task>> = _tasks

    sealed class TasksState {
        object Idle : TasksState()
        object Loading : TasksState()
        object Success : TasksState()
        data class Error(val message: String) : TasksState()
    }

    // Don't auto-load in init - wait until user is authenticated
    // Call loadTasks() after login succeeds

    fun loadTasks(filter: String? = null, tag: String? = null, date: String? = null) {
        viewModelScope.launch {
            try {
                _tasksState.value = TasksState.Loading

                val response = RetrofitClient.apiService.getTasks(filter, tag, date)

                if (response.success && response.data != null) {
                    _tasks.value = response.data
                    _tasksState.value = TasksState.Success
                    Log.d("TaskViewModel", "Loaded ${response.data.size} tasks")
                } else {
                    _tasksState.value = TasksState.Error(response.message ?: "Failed to load tasks")
                    Log.e("TaskViewModel", "Failed to load tasks: ${response.message}")
                }
            } catch (e: Exception) {
                _tasksState.value = TasksState.Error(e.message ?: "Unknown error")
                Log.e("TaskViewModel", "Error loading tasks", e)
            }
        }
    }

    fun createTask(
        title: String,
        tag: String,
        time: String,
        priority: String,
        notes: String? = null
    ) {
        viewModelScope.launch {
            try {
                val request = CreateTaskRequest(title, tag, time, priority, notes)
                val response = RetrofitClient.apiService.createTask(request)

                if (response.success && response.data != null) {
                    // Reload all tasks to get fresh data
                    loadTasks()
                    Log.d("TaskViewModel", "Task created: ${response.data.title}")
                } else {
                    _tasksState.value = TasksState.Error(response.message ?: "Failed to create task")
                    Log.e("TaskViewModel", "Failed to create task: ${response.message}")
                }
            } catch (e: Exception) {
                _tasksState.value = TasksState.Error(e.message ?: "Unknown error")
                Log.e("TaskViewModel", "Error creating task", e)
            }
        }
    }

    fun updateTask(
        taskId: Int,
        title: String,
        tag: String,
        time: String,
        done: Boolean,
        priority: String,
        notes: String? = null
    ) {
        viewModelScope.launch {
            try {
                val request = UpdateTaskRequest(title, tag, time, done, priority, notes)
                val response = RetrofitClient.apiService.updateTask(taskId, request)

                if (response.success && response.data != null) {
                    // Update task in local state
                    _tasks.value = _tasks.value.map { task ->
                        if (task.id == taskId) response.data else task
                    }
                    _tasksState.value = TasksState.Success
                    Log.d("TaskViewModel", "Task updated: ${response.data.title}")
                } else {
                    _tasksState.value = TasksState.Error(response.message ?: "Failed to update task")
                    Log.e("TaskViewModel", "Failed to update task: ${response.message}")
                }
            } catch (e: Exception) {
                _tasksState.value = TasksState.Error(e.message ?: "Unknown error")
                Log.e("TaskViewModel", "Error updating task", e)
            }
        }
    }

    fun toggleTask(taskId: Int) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.apiService.toggleTaskStatus(taskId)

                if (response.success && response.data != null) {
                    // Update local state with the response data
                    _tasks.value = _tasks.value.map { task ->
                        if (task.id == taskId) response.data else task
                    }
                    _tasksState.value = TasksState.Success
                    Log.d("TaskViewModel", "Task toggled: ${response.data.title}")
                } else {
                    _tasksState.value = TasksState.Error(response.message ?: "Failed to toggle task")
                    Log.e("TaskViewModel", "Failed to toggle task: ${response.message}")
                }
            } catch (e: Exception) {
                _tasksState.value = TasksState.Error(e.message ?: "Unknown error")
                Log.e("TaskViewModel", "Error toggling task", e)
            }
        }
    }

    fun deleteTask(taskId: Int) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.apiService.deleteTask(taskId)

                if (response.success) {
                    // Remove from local state immediately
                    _tasks.value = _tasks.value.filter { it.id != taskId }
                    Log.d("TaskViewModel", "Task deleted: $taskId")
                } else {
                    _tasksState.value = TasksState.Error(response.message ?: "Failed to delete task")
                    Log.e("TaskViewModel", "Failed to delete task: ${response.message}")
                }
            } catch (e: Exception) {
                _tasksState.value = TasksState.Error(e.message ?: "Unknown error")
                Log.e("TaskViewModel", "Error deleting task", e)
            }
        }
    }
}
