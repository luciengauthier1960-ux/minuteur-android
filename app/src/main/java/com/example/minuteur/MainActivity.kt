package com.example.minuteur

import android.app.Activity
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.text.InputType
import android.view.Gravity
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast

class MainActivity : Activity() {

    private lateinit var minutesInput: EditText
    private lateinit var secondsInput: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(64, 64, 64, 64)
        }

        val title = TextView(this).apply {
            text = "Minuteur"
            textSize = 28f
            gravity = Gravity.CENTER
            setPadding(0, 0, 0, 48)
        }

        val inputRow = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER
        }

        val columnParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f).apply {
            marginStart = 8
            marginEnd = 8
        }

        val minutesColumn = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
        }
        val minutesLabel = TextView(this).apply {
            text = "Minutes"
            gravity = Gravity.CENTER
            setPadding(0, 0, 0, 8)
        }
        minutesInput = EditText(this).apply {
            inputType = InputType.TYPE_CLASS_NUMBER
            setText("5")
            gravity = Gravity.CENTER
        }
        minutesColumn.addView(minutesLabel)
        minutesColumn.addView(minutesInput)

        val secondsColumn = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
        }
        val secondsLabel = TextView(this).apply {
            text = "Secondes"
            gravity = Gravity.CENTER
            setPadding(0, 0, 0, 8)
        }
        secondsInput = EditText(this).apply {
            inputType = InputType.TYPE_CLASS_NUMBER
            setText("0")
            gravity = Gravity.CENTER
        }
        secondsColumn.addView(secondsLabel)
        secondsColumn.addView(secondsInput)

        inputRow.addView(minutesColumn, columnParams)
        inputRow.addView(secondsColumn, columnParams)

        val startButton = Button(this).apply {
            text = "Démarrer l'alarme"
            setOnClickListener { scheduleAlarm() }
        }

        val chronometerButton = Button(this).apply {
            text = "Chronomètre"
            setOnClickListener {
                startActivity(Intent(this@MainActivity, ChronometerActivity::class.java))
            }
        }

        root.addView(title)
        root.addView(
            inputRow,
            LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        )
        root.addView(startButton)
        root.addView(chronometerButton)

        setContentView(root)

        checkExactAlarmPermission()
    }

    private fun checkExactAlarmPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
            if (!alarmManager.canScheduleExactAlarms()) {
                val intent = Intent(
                    Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM,
                    Uri.parse("package:$packageName")
                )
                startActivity(intent)
            }
        }
    }

    private fun scheduleAlarm() {
        val minutes = minutesInput.text.toString().toIntOrNull() ?: 0
        val seconds = secondsInput.text.toString().toIntOrNull() ?: 0
        val totalSeconds = minutes * 60 + seconds

        if (totalSeconds <= 0) {
            Toast.makeText(this, "Entre une durée valide", Toast.LENGTH_SHORT).show()
            return
        }

        val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !alarmManager.canScheduleExactAlarms()) {
            Toast.makeText(this, "Autorise d'abord les alarmes exactes dans les réglages", Toast.LENGTH_LONG).show()
            checkExactAlarmPermission()
            return
        }

        val triggerAt = System.currentTimeMillis() + totalSeconds * 1000L

        val intent = Intent(this, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            this, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAt, pendingIntent)

        Toast.makeText(this, "Alarme programmée dans ${minutes} min ${seconds} s", Toast.LENGTH_SHORT).show()
    }
}
