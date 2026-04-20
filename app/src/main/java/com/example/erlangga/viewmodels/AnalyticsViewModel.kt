package com.example.erlangga.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.erlangga.data.api.AnalyticsData
import com.example.erlangga.data.api.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AnalyticsViewModel : ViewModel() {
    private val _analyticsState = MutableStateFlow<AnalyticsState>(AnalyticsState.Idle)
    val analyticsState: StateFlow<AnalyticsState> = _analyticsState

    private val _analyticsData = MutableStateFlow(
        AnalyticsData(
            total_shipped = 0,
            total_tasks = 0,
            completion_rate = 0,
            streak_days = 0,
            focus_time_hours = 0.0,
            tasks_per_day = emptyList(),
            heatmap_data = emptyList()
        )
    )
    val analyticsData: StateFlow<AnalyticsData> = _analyticsData

    sealed class AnalyticsState {
        object Idle : AnalyticsState()
        object Loading : AnalyticsState()
        object Success : AnalyticsState()
        data class Error(val message: String) : AnalyticsState()
    }

    fun loadAnalytics() {
        viewModelScope.launch {
            try {
                _analyticsState.value = AnalyticsState.Loading

                val response = RetrofitClient.apiService.getAnalytics()

                if (response.success && response.data != null) {
                    _analyticsData.value = response.data
                    _analyticsState.value = AnalyticsState.Success
                    Log.d("AnalyticsViewModel", "Loaded analytics: ${response.data.total_shipped} shipped")
                } else {
                    _analyticsState.value = AnalyticsState.Error(response.message ?: "Failed to load analytics")
                    Log.e("AnalyticsViewModel", "Failed to load analytics: ${response.message}")
                }
            } catch (e: Exception) {
                _analyticsState.value = AnalyticsState.Error(e.message ?: "Unknown error")
                Log.e("AnalyticsViewModel", "Error loading analytics", e)
            }
        }
    }
}
