package org.setu.electriccar

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.setu.electriccar.models.ChargeSession
import org.setu.electriccar.models.ChargingPoint
import org.setu.electriccar.models.ChargingStation
import org.setu.electriccar.models.Route

class ListActivity : AppCompatActivity() {

    // Which list this screen shows: "stations", "points", "vehicles" or "bookings"
    private var mode = "stations"
    private var stationId = -1L

    private lateinit var titleText: TextView
    private lateinit var emptyText: TextView
    private lateinit var connectorSpinner: Spinner
    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        enableEdgeToEdge()
        setContentView(R.layout.activity_list)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.listRoot)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        mode = intent.getStringExtra("mode") ?: "stations"
        stationId = intent.getLongExtra("stationId", -1L)

        titleText = findViewById(R.id.listTitle)
        emptyText = findViewById(R.id.emptyText)
        connectorSpinner = findViewById(R.id.connectorSpinner)
        recyclerView = findViewById(R.id.listRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val hintText = findViewById<TextView>(R.id.hintText)
        val filterButton = findViewById<Button>(R.id.filterButton)
        val addButton = findViewById<Button>(R.id.addButton)

        when (mode) {
            "stations" -> {
                titleText.text = "Charging Stations"
                hintText.text = "Tap a station to view points – hold for a route"
                setupConnectorFilter()
                connectorSpinner.visibility = View.VISIBLE
                filterButton.visibility = View.VISIBLE
                filterButton.setOnClickListener { showList() }
            }
            "points" -> {
                titleText.text = AppData.store.findStation(stationId)?.name ?: "Charging Points"
                hintText.text = "Tap an available point to book – tap your booking to cancel"
            }
            "vehicles" -> {
                titleText.text = "My Vehicles"
                hintText.text = "Tap a vehicle to edit – hold to delete"
                addButton.visibility = View.VISIBLE
                addButton.setOnClickListener {
                    val intent = Intent(this, AddEditActivity::class.java)
                    intent.putExtra("mode", "vehicle")
                    startActivity(intent)
                }
            }
            "bookings" -> {
                titleText.text = "My Bookings"
                hintText.text = "Tap a booking to cancel – hold to delete"
            }
        }
    }

    override fun onResume() {
        super.onResume()
        showList()
    }

    private fun showList() {
        when (mode) {
            "stations" -> showStations()
            "points" -> showPoints()
            "vehicles" -> showVehicles()
            "bookings" -> showBookings()
        }
    }

    // Same adapter for every list
    private fun setRows(rows: List<Row>, onClick: (Int) -> Unit, onLongClick: (Int) -> Unit) {
        recyclerView.adapter = ItemAdapter(rows, onClick, onLongClick)
        emptyText.visibility = if (rows.isEmpty()) View.VISIBLE else View.GONE
    }

    // ---------- Stations ----------
    private fun setupConnectorFilter() {
        val types = ArrayList<String>()
        types.add("All")
        for (station in AppData.store.stations) {
            for (point in station.chargingPoints) {
                val type = point.connector?.connectorType ?: continue
                if (!types.contains(type)) {
                    types.add(type)
                }
            }
        }
        connectorSpinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, types)
    }

    private fun showStations() {
        val selected = connectorSpinner.selectedItem?.toString() ?: "All"
        var stations: List<ChargingStation> = AppData.store.stations
        if (selected != "All") {
            stations = stations.filter { station ->
                station.chargingPoints.any { it.connector?.connectorType == selected }
            }
        }

        val rows = stations.map { station ->
            val available = station.chargingPoints.count { it.status == "Available" }
            Row(
                station.name,
                station.address,
                "Open: ${station.openingHours} – $available of ${station.chargingPoints.size} points available"
            )
        }

        setRows(rows,
            { i ->
                val intent = Intent(this, ListActivity::class.java)
                intent.putExtra("mode", "points")
                intent.putExtra("stationId", stations[i].stationId)
                startActivity(intent)
            },
            { i -> showRoute(stations[i]) }
        )
    }

    // ---------- Charging points ----------
    private fun showPoints() {
        val station = AppData.store.findStation(stationId) ?: return
        val points = station.chargingPoints

        val rows = points.map { point ->
            val booking = AppData.store.findBookingForPoint(point)
            val status = if (booking != null && booking.userId == AppData.currentUser?.userId) {
                "Your booking – ${booking.startTime}"
            } else {
                point.status
            }
            Row(
                "Point ${point.pointId} – ${point.connector?.connectorType ?: "none"}",
                "${point.powerOutput} kW – €${point.pricePerKwh}/kWh",
                status
            )
        }

        setRows(rows,
            { i -> pointTapped(station, points[i]) },
            { i -> pointTapped(station, points[i]) }
        )
    }

    private fun pointTapped(station: ChargingStation, point: ChargingPoint) {
        val booking = AppData.store.findBookingForPoint(point)
        if (point.status == "Available") {
            val intent = Intent(this, AddEditActivity::class.java)
            intent.putExtra("mode", "booking")
            intent.putExtra("stationId", station.stationId)
            intent.putExtra("pointId", point.pointId)
            startActivity(intent)
        } else if (booking != null && booking.userId == AppData.currentUser?.userId) {
            confirm("Cancel booking", "Cancel your booking for ${booking.startTime}?") {
                cancelBooking(booking)
            }
        } else {
            showMessage("Not available", "This point is already booked.")
        }
    }

    // ---------- Vehicles ----------
    private fun showVehicles() {
        val vehicles = AppData.currentUser?.vehicles ?: ArrayList()

        val rows = vehicles.map { vehicle ->
            Row(
                "${vehicle.make} ${vehicle.model}",
                "Battery: ${vehicle.batteryCapacity} kWh – Range: ~${vehicle.getRange()} km",
                "Connector: ${vehicle.connectorType}"
            )
        }

        setRows(rows,
            { i ->
                val intent = Intent(this, AddEditActivity::class.java)
                intent.putExtra("mode", "vehicle")
                intent.putExtra("vehicleId", vehicles[i].vehicleId)
                startActivity(intent)
            },
            { i ->
                val vehicle = vehicles[i]
                confirm("Delete vehicle", "Delete ${vehicle.make} ${vehicle.model}?") {
                    vehicles.remove(vehicle)
                    AppData.store.save()
                    showList()
                }
            }
        )
    }

    // ---------- Bookings ----------
    private fun showBookings() {
        val userId = AppData.currentUser?.userId
        val bookings = AppData.store.sessions
            .filter { it.userId == userId }
            .sortedBy { it.status != "Booked" }

        val upcoming = bookings.count { it.status == "Booked" }
        titleText.text = "My Bookings ($upcoming upcoming)"

        val rows = bookings.map { booking ->
            Row(
                booking.stationName,
                "Point ${booking.chargingPoint?.pointId} – ${booking.vehicle?.make} ${booking.vehicle?.model}",
                "${booking.startTime} – ${booking.status}"
            )
        }

        setRows(rows,
            { i ->
                val booking = bookings[i]
                if (booking.status == "Booked") {
                    confirm("Cancel booking", "Cancel your booking at ${booking.stationName}?") {
                        cancelBooking(booking)
                    }
                }
            },
            { i ->
                val booking = bookings[i]
                confirm("Delete booking", "Delete this booking from your list?") {
                    if (booking.status == "Booked") {
                        booking.chargingPoint?.status = "Available"
                    }
                    AppData.store.deleteBooking(booking.sessionId)
                    showList()
                }
            }
        )
    }

    private fun cancelBooking(booking: ChargeSession) {
        booking.status = "Cancelled"
        booking.chargingPoint?.status = "Available"
        AppData.store.save()
        showList()
    }

    // ---------- Route (pop-up) ----------
    private fun showRoute(station: ChargingStation) {
        val startInput = EditText(this).apply { hint = "Start location" }
        val distanceInput = EditText(this).apply {
            hint = "Distance (km)"
            inputType = InputType.TYPE_CLASS_NUMBER
        }
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(48, 16, 48, 0)
            addView(startInput)
            addView(distanceInput)
        }

        AlertDialog.Builder(this)
            .setTitle("Route to ${station.name}")
            .setView(layout)
            .setPositiveButton("Calculate") { _, _ ->
                val start = startInput.text.toString().trim()
                val distance = distanceInput.text.toString().toIntOrNull()
                if (start.isEmpty() || distance == null || distance <= 0) {
                    showMessage("Route", "Enter a start location and a distance above 0.")
                } else {
                    val route = Route(station.stationId.toInt(), start, station.name, distance, 0.0)
                    route.calculateRoute()
                    showMessage(
                        "Route to ${station.name}",
                        "From: ${route.startLocation}\n" +
                                "To: ${route.destination}\n" +
                                "Distance: ${route.distance} km\n" +
                                "Estimated time: ${(route.estTime * 60).toInt()} minutes"
                    )
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    // ---------- Pop-up helpers ----------
    private fun confirm(title: String, message: String, onYes: () -> Unit) {
        AlertDialog.Builder(this)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("Yes") { _, _ -> onYes() }
            .setNegativeButton("No", null)
            .show()
    }

    private fun showMessage(title: String, message: String) {
        AlertDialog.Builder(this)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("OK", null)
            .show()
    }
}