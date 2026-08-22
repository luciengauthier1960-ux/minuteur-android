package com.example.minuteur

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.CountDownTimer
import android.os.IBinder
import androidx.core.app.NotificationCompat

class AlarmForegroundService : Service() {

    private var countDownTimer: CountDownTimer? = null

    companion object {
        const val EXTRA_TOTAL_SECONDS = "extra_total_seconds"
        private const val CHANNEL_ID = "minuteur_service"
        private const val NOTIFICATION_ID = 2
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val totalSeconds = intent?.getIntExtra(EXTRA_TOTAL_SECONDS, 0) ?: 0

        if (totalSeconds <= 0) {
            stopSelf()
            return START_NOT_STICKY
        }

        createChannelIfNeeded()
        startForeground(NOTIFICATION_ID, buildNotification(totalSeconds))

        countDownTimer?.cancel()
        countDownTimer = object : CountDownTimer(totalSeconds * 1000L, 1000L) {
            override fun onTick(millisUntilFinished: Long) {
                val remaining = (millisUntilFinished / 1000L).toInt()
                updateNotification(remaining)
            }

            override fun onFinish() {
                AlarmSound.start(this@AlarmForegroundService)
                val alarmIntent = Intent(this@AlarmForegroundService, AlarmActivity::class.java).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
                }
                startActivity(alarmIntent)
                stopForeground(STOP_FOREGROUND_REMOVE)
                stopSelf()
            }
        }.start()

        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        countDownTimer?.cancel()
    }

    private fun buildNotification(totalSeconds: Int) =
        NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .setContentTitle("Alarme active")
            .setContentText(formatRemaining(totalSeconds))
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()

    private fun updateNotification(remainingSeconds: Int) {
        val notification = buildNotification(remainingSeconds)
        val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(NOTIFICATION_ID, notification)
    }

    private fun formatRemaining(totalSeconds: Int): String {
        val m = totalSeconds / 60
        val s = totalSeconds % 60
        return "Sonnera dans %02d:%02d".format(m, s)
    }

    private fun createChannelIfNeeded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            if (manager.getNotificationChannel(CHANNEL_ID) == null) {
                val channel = NotificationChannel(
                    CHANNEL_ID,
                    "Service minuteur",
                    NotificationManager.IMPORTANCE_LOW
                )
                manager.createNotificationChannel(channel)
            }
        }
    }
}
