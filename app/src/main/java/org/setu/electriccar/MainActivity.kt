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

class MainActivity : AppCompatActivity() {

    private lateinit var listLayout: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (AppData.currentUser == null) {
            goToLogin()
            return
        }

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
            displayStations()
        }
    }

    private fun goToLogin() {
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }

    private fun createUserInterface(): ScrollView {

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(32, 32, 32, 32)
        }

        val userText = TextView(this).apply {
            text = "Logged in as ${AppData.currentUser?.name}"
            textSize = 14f
            gravity = Gravity.END
        }

        val logoutButton = Button(this).apply {
            text = "Log Out"
            setOnClickListener {
                AppData.currentUser = null
                goToLogin()
            }
        }

        val title = TextView(this).apply {
            text = "Charging Stations"
            textSize = 28f
            gravity = Gravity.CENTER
        }

        val addButton = Button(this).apply {
            text = "Add Station"
            setOnClickListener {
                val intent = Intent(this@MainActivity, AddEditStationActivity::class.java)
                startActivity(intent)
            }
        }

        val vehiclesButton = Button(this).apply {
            text = "My Vehicles"
            setOnClickListener {
                val intent = Intent(this@MainActivity, VehicleListActivity::class.java)
                startActivity(intent)
            }
        }

        val sessionsButton = Button(this).apply {
            text = "My Bookings"
            setOnClickListener {
                val intent = Intent(this@MainActivity, SessionListActivity::class.java)
                startActivity(intent)
            }
        }

        listLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
        }

        root.addView(userText)
        root.addView(logoutButton)
        root.addView(title)
        root.addView(addButton)
        root.addView(vehiclesButton)
        root.addView(sessionsButton)
        root.addView(listLayout)

        val scroll = ScrollView(this)
        scroll.addView(root)
        setContentView(scroll)

        displayStations()
        return scroll
    }

    private fun displayStations() {

        listLayout.removeAllViews()

        val stations = AppData.stations.findAll()

        if (stations.isEmpty()) {
            listLayout.addView(TextView(this).apply {
                text = "No charging stations yet."
                textSize = 18f
                setPadding(0, 40, 0, 40)
            })
            return
        }

        for (station in stations) {

            val stationLayout = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                setPadding(0, 20, 0, 20)
            }

            val stationName = TextView(this).apply {
                text = "${station.stationId}: ${station.name}"
                textSize = 20f
            }

            val stationAddress = TextView(this).apply {
                text = station.address
                textSize = 16f
            }

            val stationHours = TextView(this).apply {
                text = "Open: ${station.openingHours}"
                textSize = 14f
            }

            val stationPoints = TextView(this).apply {
                text = "Charging points: ${station.chargingPoints.size}"
                textSize = 14f
            }

            val editButton = Button(this).apply {
                text = "Edit"
                setOnClickListener {
                    val intent = Intent(this@MainActivity, AddEditStationActivity::class.java)
                    intent.putExtra("id", station.stationId)
                    startActivity(intent)
                }
            }

            val deleteButton = Button(this).apply {
                text = "Delete"
                setOnClickListener {
                    AppData.stations.delete(station.stationId)
                    displayStations()
                }
            }

            val viewPointsButton = Button(this).apply {
                text = "View Points"
                setOnClickListener {
                    val intent = Intent(this@MainActivity, ChargingPointsActivity::class.java)
                    intent.putExtra("stationId", station.stationId)
                    startActivity(intent)
                }
            }

            stationLayout.addView(stationName)
            stationLayout.addView(stationAddress)
            stationLayout.addView(stationHours)
            stationLayout.addView(stationPoints)
            stationLayout.addView(editButton)
            stationLayout.addView(deleteButton)
            stationLayout.addView(viewPointsButton)
            listLayout.addView(stationLayout)
        }
    }
}