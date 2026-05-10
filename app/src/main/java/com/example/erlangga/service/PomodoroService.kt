package com.example.erlangga.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.erlangga.MainActivity
import com.example.erlangga.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class PomodoroService : Service() {

    private var timerJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.Main)

    private var timeLeft = 1500
    private var isRunning = false

    companion object {
        const val CHANNEL_ID = "pomodoro_timer"
        const val NOTIFICATION_ID = 1001

        const val ACTION_START = "ACTION_START"
        const val ACTION_PAUSE = "ACTION_PAUSE"
        const val ACTION_STOP = "ACTION_STOP"

        const val EXTRA_TIME_LEFT = "extra_time_left"

        // Broadcast to update ViewModel
        const val BROADCAST_TICK = "com.example.erlangga.POMODORO_TICK"
        const val BROADCAST_DONE = "com.example.erlangga.POMODORO_DONE"
        const val BROADCAST_PAUSED = "com.example.erlangga.POMODORO_PAUSED"
        const val BROADCAST_STOPPED = "com.example.erlangga.POMODORO_STOPPED"
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> {
                timeLeft = intent.getIntExtra(EXTRA_TIME_LEFT, 1500)
                startTimer()
            }
            ACTION_PAUSE -> pauseTimer()
            ACTION_STOP -> stopTimer()
        }
        return START_NOT_STICKY
    }

    private fun startTimer() {
        isRunning = true
        startForeground(NOTIFICATION_ID, buildNotification())

        timerJob?.cancel()
        timerJob = scope.launch {
            while (isRunning && timeLeft > 0) {
                delay(1000L)
                timeLeft--
                updateNotification()
                sendBroadcast(Intent(BROADCAST_TICK).apply {
                    putExtra(EXTRA_TIME_LEFT, timeLeft)
                    setPackage(packageName)
                })
            }
            if (timeLeft == 0) {
                isRunning = false
                sendBroadcast(Intent(BROADCAST_DONE).apply { setPackage(packageName) })
                stopSelf()
            }
        }
    }

    private fun pauseTimer() {
        isRunning = false
        timerJob?.cancel()
        sendBroadcast(Intent(BROADCAST_PAUSED).apply {
            putExtra(EXTRA_TIME_LEFT, timeLeft)
            setPackage(packageName)
        })
        stopSelf()
    }

    private fun stopTimer() {
        isRunning = false
        timerJob?.cancel()
        sendBroadcast(Intent(BROADCAST_STOPPED).apply { setPackage(packageName) })
        stopSelf()
    }

    private fun updateNotification() {
        val manager = getSystemService(NotificationManager::class.java)
        manager.notify(NOTIFICATION_ID, buildNotification())
    }

    private fun buildNotification(): Notification {
        val mins = timeLeft / 60
        val secs = timeLeft % 60
        val timeStr = String.format("%02d:%02d", mins, secs)
        val totalTime = 1500
        val progress = ((totalTime - timeLeft).toFloat() / totalTime * 100).toInt()

        val openIntent = PendingIntent.getActivity(
            this, 0,
            Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val pauseIntent = PendingIntent.getService(
            this, 1,
            Intent(this, PomodoroService::class.java).apply { action = ACTION_PAUSE },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val stopIntent = PendingIntent.getService(
            this, 2,
            Intent(this, PomodoroService::class.java).apply { action = ACTION_STOP },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("⚡ Focus — $timeStr remaining")
            .setContentText(buildProgressBar(progress) + "  $progress%")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentIntent(openIntent)
            .addAction(android.R.drawable.ic_media_pause, "⏸ Pause", pauseIntent)
            .addAction(android.R.drawable.ic_menu_close_clear_cancel, "⏹ Stop", stopIntent)
            .setOngoing(true)
            .setSilent(true)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setCategory(NotificationCompat.CATEGORY_PROGRESS)
            .setProgress(100, progress, false)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText("${buildProgressBar(progress)}  $progress% complete\nStay focused — you got this! 🔥")
                    .setBigContentTitle("⚡ Focus session — $timeStr left")
            )
            .build()
    }

    private fun buildProgressBar(progress: Int): String {
        val filled = (progress / 5)
        val empty = 20 - filled
        return "▓".repeat(filled) + "░".repeat(empty)
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Pomodoro Timer",
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = "Shows active focus session timer"
            setShowBadge(true)
            enableLights(false)
            enableVibration(false)
        }
        getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
