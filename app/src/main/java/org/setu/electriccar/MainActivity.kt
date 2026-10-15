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
        AppData.setup(applicationContext)

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

        findViewById<Button>(R.id.viewStationsButton).setOnClickListener { openList("stations") }
        findViewById<Button>(R.id.viewVehiclesButton).setOnClickListener { openList("vehicles") }
        findViewById<Button>(R.id.myBookingsButton).setOnClickListener { openList("bookings") }

        findViewById<Button>(R.id.logoutButton).setOnClickListener {
            AppData.logout()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    // One list screen, told which list to show
    private fun openList(mode: String) {
        val intent = Intent(this, ListActivity::class.java)
        intent.putExtra("mode", mode)
        startActivity(intent)
    }
}