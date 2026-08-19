package com.example.minuteur

import android.app.Activity
import android.os.Bundle
import android.os.SystemClock
import android.view.Gravity
import android.widget.Button
import android.widget.Chronometer
import android.widget.LinearLayout
import android.widget.TextView

class ChronometerActivity : Activity() {

    private lateinit var chronometer: Chronometer
    private lateinit var startPauseButton: Button
    private var running = false
    private var pauseOffset = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(64, 64, 64, 64)
        }

        val title = TextView(this).apply {
            text = "Chronomètre"
            textSize = 28f
            gravity = Gravity.CENTER
            setPadding(0, 0, 0, 48)
        }

        chronometer = Chronometer(this).apply {
            textSize = 40f
            gravity = Gravity.CENTER
            setPadding(0, 0, 0, 48)
        }

        startPauseButton = Button(this).apply {
            text = "Démarrer"
            setOnClickListener { toggle() }
        }

        val resetButton = Button(this).apply {
            text = "Réinitialiser"
            setOnClickListener { reset() }
        }

        root.addView(title)
        root.addView(chronometer)
        root.addView(startPauseButton)
        root.addView(resetButton)

        setContentView(root)
    }

    private fun toggle() {
        if (!running) {
            chronometer.base = SystemClock.elapsedRealtime() - pauseOffset
            chronometer.start()
            running = true
            startPauseButton.text = "Pause"
        } else {
            pauseOffset = SystemClock.elapsedRealtime() - chronometer.base
            chronometer.stop()
            running = false
            startPauseButton.text = "Reprendre"
        }
    }

    private fun reset() {
        pauseOffset = 0L
        chronometer.base = SystemClock.elapsedRealtime()
        chronometer.stop()
        running = false
        startPauseButton.text = "Démarrer"
    }
}
