package com.example.minuteur

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
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

        requestNotificationPermissionIfNeeded()
    }

    private fun requestNotificationPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT >= 33) {
            if (checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS), 100)
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

        val serviceIntent = Intent(this, AlarmForegroundService::class.java).apply {
            putExtra(AlarmForegroundService.EXTRA_TOTAL_SECONDS, totalSeconds)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(serviceIntent)
        } else {
            startService(serviceIntent)
        }

        Toast.makeText(this, "Alarme programmée dans ${minutes} min ${seconds} s", Toast.LENGTH_SHORT).show()
    }
}
