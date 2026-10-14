package org.setu.electriccar

import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (AppData.currentUser == null) {
            goToLogin()
            return
        }

        enableEdgeToEdge()

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(48, 48, 48, 48)
        }

        val title = TextView(this).apply {
            text = "EV Charging"
            textSize = 32f
            gravity = Gravity.CENTER
        }

        val welcome = TextView(this).apply {
            text = "Welcome, ${AppData.currentUser?.name}"
            textSize = 18f
            gravity = Gravity.CENTER
            setPadding(0, 8, 0, 48)
        }

        val stationsButton = Button(this).apply {
            text = "View Stations"
            setOnClickListener {
                startActivity(Intent(this@MainActivity, StationListActivity::class.java))
            }
        }

        val vehiclesButton = Button(this).apply {
            text = "My Vehicles"
            setOnClickListener {
                startActivity(Intent(this@MainActivity, VehicleListActivity::class.java))
            }
        }

        val bookingsButton = Button(this).apply {
            text = "My Bookings"
            setOnClickListener {
                startActivity(Intent(this@MainActivity, SessionListActivity::class.java))
            }
        }

        val logoutButton = Button(this).apply {
            text = "Log Out"
            setOnClickListener {
                AppData.currentUser = null
                goToLogin()
            }
        }

        val buttonParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            setMargins(0, 12, 0, 12)
        }

        root.addView(title)
        root.addView(welcome)
        root.addView(stationsButton, buttonParams)
        root.addView(vehiclesButton, buttonParams)
        root.addView(bookingsButton, buttonParams)
        root.addView(logoutButton, buttonParams)

        setContentView(root)

        ViewCompat.setOnApplyWindowInsetsListener(root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(
                systemBars.left + 48,
                systemBars.top + 48,
                systemBars.right + 48,
                systemBars.bottom + 48
            )
            insets
        }
    }

    private fun goToLogin() {
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }
}