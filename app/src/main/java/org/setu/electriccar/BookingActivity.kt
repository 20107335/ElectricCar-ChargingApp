package org.setu.electriccar

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.Gravity
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.Spinner
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import org.setu.electriccar.models.ChargeSession
import org.setu.electriccar.models.ChargingPoint
import org.setu.electriccar.models.ChargingStation
import org.setu.electriccar.models.Vehicle
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import com.google.android.material.button.MaterialButton

class BookingActivity : AppCompatActivity() {

    private lateinit var station: ChargingStation
    private lateinit var point: ChargingPoint

    private lateinit var vehicleSpinner: Spinner
    private lateinit var dateInput: EditText
    private lateinit var timeInput: EditText
    private lateinit var messageText: TextView

    private var vehicles: List<Vehicle> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        enableEdgeToEdge()

        val stationId = intent.getLongExtra("stationId", -1L)
        val pointId = intent.getLongExtra("pointId", -1L)

        val foundStation = AppData.stations.findOne(stationId)
        val foundPoint = foundStation?.chargingPoints?.find { it.pointId == pointId }

        if (foundStation == null || foundPoint == null) {
            finish()
            return
        }

        station = foundStation
        point = foundPoint

        val scroll = createUserInterface()

        ViewCompat.setOnApplyWindowInsetsListener(scroll) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun createUserInterface(): ScrollView {

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(32, 32, 32, 32)
        }

        val heading = TextView(this).apply {
            text = "Book a Charging Point"
            textSize = 28f
            gravity = Gravity.CENTER
        }

        val infoText = TextView(this).apply {
            text = "${station.name} – Point ${point.pointId}\n" +
                    "${point.powerOutput} kW – €${point.pricePerKwh}/kWh\n" +
                    "Connector: ${point.connector?.connectorType ?: "none"}"
            textSize = 16f
            setPadding(0, 16, 0, 24)
        }

        vehicles = AppData.currentUser?.vehicles ?: emptyList()

        val vehicleLabel = TextView(this).apply {
            text = "Vehicle"
            textSize = 14f
        }

        val vehicleNames = vehicles.map { "${it.make} ${it.model} (${it.connectorType})" }

        vehicleSpinner = Spinner(this).apply {
            adapter = ArrayAdapter(
                this@BookingActivity,
                android.R.layout.simple_spinner_dropdown_item,
                vehicleNames
            )
        }

        val today = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())

        // Date box: can't type in it, tapping it opens the calendar
        dateInput = EditText(this).apply {
            hint = "Tap to pick a date"
            setText(today)
            isFocusable = false
            setOnClickListener {
                showDatePicker()
            }
        }

        // Time box: can't type in it, tapping it opens the clock
        timeInput = EditText(this).apply {
            hint = "Tap to pick a time"
            isFocusable = false
            setOnClickListener {
                showTimePicker()
            }
        }

        messageText = TextView(this).apply {
            textSize = 14f
            setPadding(0, 16, 0, 16)
        }

        val bookButton = MaterialButton(this).apply {
            text = "Confirm Booking"
            setOnClickListener {
                confirmBooking()
            }
        }

        val cancelButton = MaterialButton(this).apply {
            text = "Cancel"
            setOnClickListener {
                finish()
            }
        }

        if (vehicles.isEmpty()) {
            messageText.text = "Add a vehicle in My Vehicles before booking."
            bookButton.isEnabled = false
        }

        root.addView(heading)
        root.addView(infoText)
        root.addView(vehicleLabel)
        root.addView(vehicleSpinner)
        root.addView(dateInput)
        root.addView(timeInput)
        root.addView(messageText)
        root.addView(bookButton)
        root.addView(cancelButton)

        val scroll = ScrollView(this)
        scroll.addView(root)
        setContentView(scroll)

        return scroll
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()

        val dialog = DatePickerDialog(
            this,
            { _, year, month, day ->
                // month starts at 0 (January = 0), so add 1
                dateInput.setText(String.format(Locale.getDefault(), "%02d/%02d/%04d", day, month + 1, year))
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )

        // Can't pick a day in the past
        dialog.datePicker.minDate = System.currentTimeMillis()
        dialog.show()
    }

    private fun showTimePicker() {
        val calendar = Calendar.getInstance()

        val dialog = TimePickerDialog(
            this,
            { _, hour, minute ->
                timeInput.setText(String.format(Locale.getDefault(), "%02d:%02d", hour, minute))
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            true
        )

        dialog.show()
    }

    private fun confirmBooking() {

        val vehicle = vehicles[vehicleSpinner.selectedItemPosition]

        val date = dateInput.text.toString().trim()
        val time = timeInput.text.toString().trim()

        val format = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        format.isLenient = false

        val bookedDate = try {
            format.parse("$date $time")
        } catch (e: ParseException) {
            null
        }

        if (bookedDate == null) {
            messageText.text = "Pick a date and a time."
            return
        }

        if (bookedDate.before(Date())) {
            messageText.text = "Pick a time in the future."
            return
        }

        val pointConnector = point.connector?.connectorType ?: ""
        if (!pointConnector.equals(vehicle.connectorType, ignoreCase = true)) {
            messageText.text = "This point uses $pointConnector but your " +
                    "${vehicle.make} ${vehicle.model} uses ${vehicle.connectorType}."
            return
        }

        if (point.status != "Available") {
            messageText.text = "This point is no longer available."
            return
        }

        val booking = ChargeSession(
            startTime = format.format(bookedDate),
            status = "Booked",
            chargingPoint = point,
            userId = AppData.currentUser?.userId ?: 0,
            vehicle = vehicle,
            stationName = station.name
        )

        AppData.sessions.create(booking)
        point.status = "Booked"

        finish()
    }
}