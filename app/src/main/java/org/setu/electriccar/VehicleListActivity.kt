package org.setu.electriccar

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.setu.electriccar.models.Vehicle

class VehicleListActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var emptyText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        enableEdgeToEdge()
        setContentView(R.layout.activity_vehicle_list)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.vehicleListRoot)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        recyclerView = findViewById(R.id.vehiclesRecyclerView)
        emptyText = findViewById(R.id.emptyText)

        recyclerView.layoutManager = LinearLayoutManager(this)

        val vehicles = AppData.currentUser?.vehicles ?: ArrayList()

        recyclerView.adapter = VehicleAdapter(
            vehicles,
            { vehicle -> editVehicle(vehicle) },
            { vehicle -> confirmDelete(vehicle) }
        )

        findViewById<Button>(R.id.addVehicleButton).setOnClickListener {
            startActivity(Intent(this, AddEditVehicleActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        refreshList()
    }

    private fun refreshList() {
        recyclerView.adapter?.notifyDataSetChanged()

        val vehicles = AppData.currentUser?.vehicles ?: ArrayList()
        if (vehicles.isEmpty()) {
            emptyText.visibility = View.VISIBLE
        } else {
            emptyText.visibility = View.GONE
        }
    }

    private fun editVehicle(vehicle: Vehicle) {
        val intent = Intent(this, AddEditVehicleActivity::class.java)
        intent.putExtra("id", vehicle.vehicleId)
        startActivity(intent)
    }

    private fun confirmDelete(vehicle: Vehicle) {
        AlertDialog.Builder(this)
            .setTitle("Delete vehicle")
            .setMessage("Are you sure you want to delete ${vehicle.make} ${vehicle.model}?")
            .setPositiveButton("Delete") { _, _ ->
                AppData.currentUser?.vehicles?.removeAll { it.vehicleId == vehicle.vehicleId }
                AppData.saveCurrentUser()
                refreshList()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}