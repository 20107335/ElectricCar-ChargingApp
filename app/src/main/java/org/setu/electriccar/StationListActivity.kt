package org.setu.electriccar

import android.content.Intent
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

class StationListActivity : AppCompatActivity() {

    private lateinit var listLayout: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(32, 32, 32, 32)
        }

        val title = TextView(this).apply {
            text = "Charging Stations"
            textSize = 28f
            gravity = Gravity.CENTER
            setPadding(0, 0, 0, 16)
        }

        val backButton = Button(this).apply {
            text = "Back"
            setOnClickListener { finish() }
        }

        listLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
        }

        root.addView(title)
        root.addView(backButton)
        root.addView(listLayout)

        val scroll = ScrollView(this)
        scroll.addView(root)
        setContentView(scroll)

        ViewCompat.setOnApplyWindowInsetsListener(scroll) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        displayStations()
    }

    override fun onResume() {
        super.onResume()
        if (::listLayout.isInitialized) {
            displayStations()
        }
    }

    private fun displayStations() {

        listLayout.removeAllViews()

        val stations = AppData.stations.findAll()

        if (stations.isEmpty()) {
            listLayout.addView(TextView(this).apply {
                text = "No charging stations available."
                textSize = 18f
                setPadding(0, 40, 0, 40)
            })
            return
        }

        for (station in stations) {

            val stationLayout = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                setPadding(0, 32, 0, 32)
            }

            val nameText = TextView(this).apply {
                text = station.name
                textSize = 20f
            }

            val addressText = TextView(this).apply {
                text = station.address
                textSize = 16f
            }

            val available = station.chargingPoints.count { it.status == "Available" }

            val infoText = TextView(this).apply {
                text = "Open: ${station.openingHours} – " +
                        "$available of ${station.chargingPoints.size} points available"
                textSize = 14f
            }

            val buttonRow = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
            }

            val halfWidth = LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1f
            )

            val viewPointsButton = Button(this).apply {
                text = "View Points"
                setOnClickListener {
                    val intent = Intent(this@StationListActivity, ChargingPointsActivity::class.java)
                    intent.putExtra("stationId", station.stationId)
                    startActivity(intent)
                }
            }

            val routeButton = Button(this).apply {
                text = "Route Here"
                setOnClickListener {
                    val intent = Intent(this@StationListActivity, RouteActivity::class.java)
                    intent.putExtra("stationId", station.stationId)
                    startActivity(intent)
                }
            }

            buttonRow.addView(viewPointsButton, halfWidth)
            buttonRow.addView(routeButton, halfWidth)

            stationLayout.addView(nameText)
            stationLayout.addView(addressText)
            stationLayout.addView(infoText)
            stationLayout.addView(buttonRow)

            listLayout.addView(stationLayout)
        }
    }
}