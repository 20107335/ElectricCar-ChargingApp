package org.setu.electriccar

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (AppData.currentUser == null) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val viewStationsButton = findViewById<Button>(R.id.viewStationsButton)
        val viewVehiclesButton = findViewById<Button>(R.id.viewVehiclesButton)
        val myBookingsButton = findViewById<Button>(R.id.myBookingsButton)
        val logoutButton = findViewById<Button>(R.id.logoutButton)

        viewStationsButton.setOnClickListener {
            startActivity(Intent(this, StationListActivity::class.java))
        }

        viewVehiclesButton.setOnClickListener {
            startActivity(Intent(this, VehicleListActivity::class.java))
        }

        myBookingsButton.setOnClickListener {
            startActivity(Intent(this, SessionListActivity::class.java))
        }

        logoutButton.setOnClickListener {
            AppData.currentUser = null
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
}