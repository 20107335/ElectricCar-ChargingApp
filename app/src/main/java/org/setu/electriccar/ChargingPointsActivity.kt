package org.setu.electriccar

import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class ChargingPointsActivity : AppCompatActivity() {

    private lateinit var titleText: TextView
    private lateinit var listLayout: LinearLayout

    private var stationId: Long = -1L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        stationId = intent.getLongExtra("stationId", -1L)

        if (AppData.stations.findOne(stationId) == null) {
            Toast.makeText(this, "Station not found", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

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
            displayPoints()
        }
    }

    private fun createUserInterface(): ScrollView {

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(32, 32, 32, 32)
        }

        titleText = TextView(this).apply {
            textSize = 26f
            gravity = Gravity.CENTER
        }

        val addButton = Button(this).apply {
            text = "Add Point"
            setOnClickListener {
                val intent = Intent(this@ChargingPointsActivity, AddEditPointActivity::class.java)
                intent.putExtra("stationId", stationId)
                startActivity(intent)
            }
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

        root.addView(titleText)
        root.addView(addButton)
        root.addView(backButton)
        root.addView(listLayout)

        val scroll = ScrollView(this)
        scroll.addView(root)
        setContentView(scroll)

        displayPoints()
        return scroll
    }

    private fun displayPoints() {

        listLayout.removeAllViews()

        val station = AppData.stations.findOne(stationId) ?: return

        titleText.text = "Points for ${station.name}"

        val points = station.chargingPoints

        if (points.isEmpty()) {
            listLayout.addView(TextView(this).apply {
                text = "No charging points yet."
                textSize = 18f
                setPadding(0, 40, 0, 40)
            })
            return
        }

        for (point in points) {

            val pointLayout = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                setPadding(0, 20, 0, 20)
            }

            val pointTitle = TextView(this).apply {
                text = "Point ${point.pointId} – ${point.status}"
                textSize = 20f
            }

            val pointDetails = TextView(this).apply {
                text = "${point.powerOutput} kW – €${point.pricePerKwh}/kWh"
                textSize = 16f
            }

            val connectorText = TextView(this).apply {
                val connector = point.connector
                text = if (connector != null) {
                    "Connector: ${connector.connectorType} (max ${connector.maxPower} kW)"
                } else {
                    "Connector: none"
                }
                textSize = 14f
            }

            val editButton = Button(this).apply {
                text = "Edit"
                setOnClickListener {
                    val intent = Intent(this@ChargingPointsActivity, AddEditPointActivity::class.java)
                    intent.putExtra("stationId", stationId)
                    intent.putExtra("pointId", point.pointId)
                    startActivity(intent)
                }
            }

            val deleteButton = Button(this).apply {
                text = "Delete"
                setOnClickListener {
                    station.chargingPoints.removeAll { it.pointId == point.pointId }
                    displayPoints()
                }
            }

            pointLayout.addView(pointTitle)
            pointLayout.addView(pointDetails)
            pointLayout.addView(connectorText)
            pointLayout.addView(editButton)
            pointLayout.addView(deleteButton)

            listLayout.addView(pointLayout)
        }
    }
}