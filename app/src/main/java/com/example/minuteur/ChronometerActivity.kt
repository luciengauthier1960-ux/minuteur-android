package com.example.minuteur

import android.app.Activity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.view.Gravity
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import java.util.Locale

class ChronometerActivity : Activity() {

    private lateinit var minutesDisplay: TextView
    private lateinit var secondsDisplay: TextView
    private lateinit var startPauseButton: Button

    private val handler = Handler(Looper.getMainLooper())
    private var running = false
    private var startBase = 0L
    private var elapsedBeforePause = 0L

    private val ticker = object : Runnable {
        override fun run() {
            val elapsed = elapsedBeforePause + (SystemClock.elapsedRealtime() - startBase)
            updateDisplay(elapsed)
            handler.postDelayed(this, 50)
        }
    }

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

        val displayRow = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER
        }

        val columnParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f).apply {
            marginStart = 8
            marginEnd = 8
        }

        // Colonne "Minutes"
        val minutesColumn = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
        }
        val minutesLabel = TextView(this).apply {
            text = "Minutes"
            gravity = Gravity.CENTER
            setPadding(0, 0, 0, 8)
        }
        minutesDisplay = TextView(this).apply {
            text = "00"
            textSize = 40f
            gravity = Gravity.CENTER
        }
        minutesColumn.addView(minutesLabel)
        minutesColumn.addView(minutesDisplay)

        // Colonne "Secondes"
        val secondsColumn = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
        }
        val secondsLabel = TextView(this).apply {
            text = "Secondes"
            gravity = Gravity.CENTER
            setPadding(0, 0, 0, 8)
        }
        secondsDisplay = TextView(this).apply {
            text = "00.00"
            textSize = 40f
            gravity = Gravity.CENTER
        }
        secondsColumn.addView(secondsLabel)
        secondsColumn.addView(secondsDisplay)

        displayRow.addView(minutesColumn, columnParams)
        displayRow.addView(secondsColumn, columnParams)

        startPauseButton = Button(this).apply {
            text = "Démarrer"
            setOnClickListener { toggle() }
        }

        val resetButton = Button(this).apply {
            text = "Réinitialiser"
            setOnClickListener { reset() }
        }

        root.addView(title)
        root.addView(
            displayRow,
            LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT).apply {
                bottomMargin = 48
            }
        )
        root.addView(startPauseButton)
        root.addView(resetButton)

        setContentView(root)
    }

    private fun toggle() {
        if (!running) {
            startBase = SystemClock.elapsedRealtime()
            running = true
            startPauseButton.text = "Pause"
            handler.post(ticker)
        } else {
            elapsedBeforePause += SystemClock.elapsedRealtime() - startBase
            running = false
            startPauseButton.text = "Reprendre"
            handler.removeCallbacks(ticker)
        }
    }

    private fun reset() {
        handler.removeCallbacks(ticker)
        running = false
        startBase = 0L
        elapsedBeforePause = 0L
        startPauseButton.text = "Démarrer"
        updateDisplay(0L)
    }

    private fun updateDisplay(elapsedMs: Long) {
        val minutes = (elapsedMs / 60000) % 60
        val seconds = (elapsedMs / 1000) % 60
        val hundredths = (elapsedMs % 1000) / 10
        minutesDisplay.text = String.format(Locale.FRANCE, "%02d", minutes)
        secondsDisplay.text = String.format(Locale.FRANCE, "%02d.%02d", seconds, hundredths)
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(ticker)
    }
}
