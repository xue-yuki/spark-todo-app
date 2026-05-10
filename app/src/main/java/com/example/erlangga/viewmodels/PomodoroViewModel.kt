package com.example.erlangga.viewmodels

import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.RingtoneManager
import android.os.Build
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import com.example.erlangga.service.PomodoroService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class PomodoroViewModel(private val app: Application) : AndroidViewModel(app) {

    private val _selectedMode = MutableStateFlow(0) // 0=Focus 1=Short 2=Long 3=Custom
    val selectedMode: StateFlow<Int> = _selectedMode

    private val _isRunning = MutableStateFlow(false)
    val isRunning: StateFlow<Boolean> = _isRunning

    private val _timeLeft = MutableStateFlow(1500)
    val timeLeft: StateFlow<Int> = _timeLeft

    private val _customMinutes = MutableStateFlow(25)
    val customMinutes: StateFlow<Int> = _customMinutes

    val totalTime: Int get() = when (_selectedMode.value) {
        0 -> 1500
        1 -> 300
        2 -> 900
        3 -> _customMinutes.value * 60
        else -> 1500
    }

    fun setCustomMinutes(minutes: Int) {
        _customMinutes.value = minutes.coerceIn(1, 120)
        if (_selectedMode.value == 3) {
            _timeLeft.value = _customMinutes.value * 60
        }
    }

    private val tickReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.action) {
                PomodoroService.BROADCAST_TICK -> {
                    _timeLeft.value = intent.getIntExtra(PomodoroService.EXTRA_TIME_LEFT, _timeLeft.value)
                }
                PomodoroService.BROADCAST_PAUSED -> {
                    _isRunning.value = false
                    _timeLeft.value = intent.getIntExtra(PomodoroService.EXTRA_TIME_LEFT, _timeLeft.value)
                }
                PomodoroService.BROADCAST_DONE -> {
                    _isRunning.value = false
                    _timeLeft.value = totalTime
                    playCompletionSound()
                }
                PomodoroService.BROADCAST_STOPPED -> {
                    _isRunning.value = false
                }
            }
        }
    }

    init {
        val filter = IntentFilter().apply {
            addAction(PomodoroService.BROADCAST_TICK)
            addAction(PomodoroService.BROADCAST_PAUSED)
            addAction(PomodoroService.BROADCAST_DONE)
            addAction(PomodoroService.BROADCAST_STOPPED)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            app.registerReceiver(tickReceiver, filter, Context.RECEIVER_NOT_EXPORTED)
        } else {
            app.registerReceiver(tickReceiver, filter)
        }
    }

    private fun playCompletionSound() {
        try {
            val uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            val ringtone = RingtoneManager.getRingtone(app, uri)
            ringtone?.play()
        } catch (_: Exception) {}
    }

    fun setMode(mode: Int) {
        if (mode == _selectedMode.value) return
        sendToService(PomodoroService.ACTION_STOP)
        _selectedMode.value = mode
        _isRunning.value = false
        _timeLeft.value = when (mode) {
            0 -> 1500
            1 -> 300
            2 -> 900
            3 -> _customMinutes.value * 60
            else -> 1500
        }
    }

    fun toggleTimer() {
        if (_isRunning.value) {
            // Pause — let service broadcast back to update state
            sendToService(PomodoroService.ACTION_PAUSE)
        } else {
            _isRunning.value = true
            startService()
        }
    }

    fun reset() {
        sendToService(PomodoroService.ACTION_STOP)
        _isRunning.value = false
        _timeLeft.value = totalTime
    }

    private fun startService() {
        val intent = Intent(app, PomodoroService::class.java).apply {
            action = PomodoroService.ACTION_START
            putExtra(PomodoroService.EXTRA_TIME_LEFT, _timeLeft.value)
        }
        ContextCompat.startForegroundService(app, intent)
    }

    private fun sendToService(action: String) {
        app.startService(Intent(app, PomodoroService::class.java).apply {
            this.action = action
        })
    }

    override fun onCleared() {
        super.onCleared()
        app.unregisterReceiver(tickReceiver)
    }
}
