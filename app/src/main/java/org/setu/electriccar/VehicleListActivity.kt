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

class VehicleListActivity : AppCompatActivity() {

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
            displayVehicles()
        }
    }

    private fun createUserInterface(): ScrollView {

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(32, 32, 32, 32)
        }

        val title = TextView(this).apply {
            text = "My Vehicles"
            textSize = 28f
            gravity = Gravity.CENTER
        }

        val addButton = Button(this).apply {
            text = "Add Vehicle"
            setOnClickListener {
                val intent = Intent(this@VehicleListActivity, AddEditVehicleActivity::class.java)
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

        root.addView(title)
        root.addView(addButton)
        root.addView(backButton)
        root.addView(listLayout)

        val scroll = ScrollView(this)
        scroll.addView(root)
        setContentView(scroll)

        displayVehicles()
        return scroll
    }

    private fun displayVehicles() {

        listLayout.removeAllViews()

        val vehicles = AppData.vehicles.findAll()

        if (vehicles.isEmpty()) {
            listLayout.addView(TextView(this).apply {
                text = "No vehicles yet."
                textSize = 18f
                setPadding(0, 40, 0, 40)
            })
            return
        }

        for (vehicle in vehicles) {

            val vehicleLayout = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                setPadding(0, 20, 0, 20)
            }

            val vehicleName = TextView(this).apply {
                text = "${vehicle.vehicleId}: ${vehicle.make} ${vehicle.model}"
                textSize = 20f
            }

            val vehicleBattery = TextView(this).apply {
                text = "Battery: ${vehicle.batteryCapacity} kWh – Range: ~${vehicle.getRange()} km"
                textSize = 16f
            }

            val vehicleConnector = TextView(this).apply {
                text = "Connector: ${vehicle.connectorType}"
                textSize = 14f
            }

            val editButton = Button(this).apply {
                text = "Edit"
                setOnClickListener {
                    val intent = Intent(this@VehicleListActivity, AddEditVehicleActivity::class.java)
                    intent.putExtra("id", vehicle.vehicleId)
                    startActivity(intent)
                }
            }

            val deleteButton = Button(this).apply {
                text = "Delete"
                setOnClickListener {
                    AppData.vehicles.delete(vehicle.vehicleId)
                    displayVehicles()
                }
            }

            vehicleLayout.addView(vehicleName)
            vehicleLayout.addView(vehicleBattery)
            vehicleLayout.addView(vehicleConnector)
            vehicleLayout.addView(editButton)
            vehicleLayout.addView(deleteButton)

            listLayout.addView(vehicleLayout)
        }
    }
}