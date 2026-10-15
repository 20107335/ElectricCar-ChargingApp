package org.setu.electriccar

import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.Spinner
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.button.MaterialButton

class StationListActivity : AppCompatActivity() {

    private lateinit var listLayout: LinearLayout
    private lateinit var connectorSpinner: Spinner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

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

        // Dropdown of connector types: "All" + every type used by the stations
        val connectorTypes = ArrayList<String>()
        connectorTypes.add("All")
        for (station in AppData.stations.findAll()) {
            for (point in station.chargingPoints) {
                val type = point.connector?.connectorType ?: continue
                if (!connectorTypes.contains(type)) {
                    connectorTypes.add(type)
                }
            }
        }

        connectorSpinner = Spinner(this)
        connectorSpinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, connectorTypes)

        val filterButton = MaterialButton(this).apply {
            text = "Filter"
            setOnClickListener {
                displayStations()
            }
        }

        listLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
        }

        root.addView(title)
        root.addView(connectorSpinner)
        root.addView(filterButton)
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

    private fun displayStations() {

        listLayout.removeAllViews()

        val selectedType = connectorSpinner.selectedItem.toString()

        var stations = AppData.stations.findAll()

        if (selectedType != "All") {
            stations = stations.filter { station ->
                station.chargingPoints.any { it.connector?.connectorType == selectedType }
            }
        }

        if (stations.isEmpty()) {
            listLayout.addView(TextView(this).apply {
                text = "No stations found for $selectedType."
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

            val viewPointsButton = MaterialButton(this).apply {
                text = "View Points"
                setOnClickListener {
                    val intent = Intent(this@StationListActivity, ChargingPointsActivity::class.java)
                    intent.putExtra("stationId", station.stationId)
                    startActivity(intent)
                }
            }

            val routeButton = MaterialButton(this).apply {
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