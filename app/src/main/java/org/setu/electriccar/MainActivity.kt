package org.setu.electriccar

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppData.setup(applicationContext)

        if (AppData.currentUser == null) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.viewStationsButton).setOnClickListener {
            startActivity(Intent(this, StationListActivity::class.java))
        }

        findViewById<Button>(R.id.viewVehiclesButton).setOnClickListener {
            startActivity(Intent(this, VehicleListActivity::class.java))
        }

        findViewById<Button>(R.id.myBookingsButton).setOnClickListener {
            startActivity(Intent(this, SessionListActivity::class.java))
        }

        findViewById<Button>(R.id.logoutButton).setOnClickListener {
            AppData.logout()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
}