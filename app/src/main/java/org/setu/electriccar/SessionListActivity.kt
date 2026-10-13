package org.setu.electriccar

import android.os.Bundle
import android.view.Gravity
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class SessionListActivity : AppCompatActivity() {

    private lateinit var totalText: TextView
    private lateinit var listLayout: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val scroll = createUserInterface()

        ViewCompat.setOnApplyWindowInsetsListener(scroll) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    override fun onResume() {
        super.onResume()
        if (::listLayout.isInitialized) {
            displaySessions()
        }
    }

    private fun createUserInterface(): ScrollView {

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(32, 32, 32, 32)
        }

        val title = TextView(this).apply {
            text = "My Sessions"
            textSize = 28f
            gravity = Gravity.CENTER
        }

        totalText = TextView(this).apply {
            textSize = 18f
            gravity = Gravity.CENTER
            setPadding(0, 16, 0, 16)
        }

        val backButton = Button(this).apply {
            text = "Back to Stations"
            setOnClickListener {
                finish()
            }
        }

        listLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
        }

        root.addView(title)
        root.addView(totalText)
        root.addView(backButton)
        root.addView(listLayout)

        val scroll = ScrollView(this)
        scroll.addView(root)
        setContentView(scroll)

        displaySessions()
        return scroll
    }

    private fun displaySessions() {

        listLayout.removeAllViews()

        val sessions = AppData.sessions.findAll()

        val totalCost = sessions
            .filter { it.status == "Completed" }
            .sumOf { it.cost }

        val totalEnergy = sessions
            .filter { it.status == "Completed" }
            .sumOf { it.energyUsed }

        totalText.text = "Total: %.1f kWh – €%.2f".format(totalEnergy, totalCost)

        if (sessions.isEmpty()) {
            listLayout.addView(TextView(this).apply {
                text = "No charging sessions yet."
                textSize = 18f
                setPadding(0, 40, 0, 40)
            })
            return
        }

        for (session in sessions.reversed()) {

            val sessionLayout = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                setPadding(0, 20, 0, 20)
            }

            val sessionTitle = TextView(this).apply {
                text = "Session ${session.sessionId} – ${session.status}"
                textSize = 20f
            }

            val pointText = TextView(this).apply {
                val point = session.chargingPoint
                text = if (point != null) {
                    "Point ${point.pointId} – €${point.pricePerKwh}/kWh"
                } else {
                    "Point: unknown"
                }
                textSize = 14f
            }

            val timeText = TextView(this).apply {
                text = if (session.status == "Completed") {
                    "${session.startTime} → ${session.endTime}"
                } else {
                    "Started ${session.startTime}"
                }
                textSize = 14f
            }

            val costText = TextView(this).apply {
                text = if (session.status == "Completed") {
                    "%.1f kWh – €%.2f".format(session.energyUsed, session.cost)
                } else {
                    "Charging in progress…"
                }
                textSize = 16f
            }

            sessionLayout.addView(sessionTitle)
            sessionLayout.addView(pointText)
            sessionLayout.addView(timeText)
            sessionLayout.addView(costText)

            if (session.status == "Completed") {
                val deleteButton = Button(this).apply {
                    text = "Delete"
                    setOnClickListener {
                        AppData.sessions.delete(session.sessionId)
                        displaySessions()
                    }
                }
                sessionLayout.addView(deleteButton)
            }

            listLayout.addView(sessionLayout)
        }
    }
}