package org.setu.electriccar

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
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

class AddEditActivity : AppCompatActivity() {

    // "vehicle" = add/edit a vehicle, "booking" = book a charging point
    private var mode = "vehicle"

    private var editingVehicle: Vehicle? = null
    private var station: ChargingStation? = null
    private var point: ChargingPoint? = null
    private var vehicles: List<Vehicle> = emptyList()

    private lateinit var formTitle: TextView
    private lateinit var infoText: TextView
    private lateinit var messageText: TextView
    private lateinit var saveButton: Button
    private lateinit var makeInput: EditText
    private lateinit var modelInput: EditText
    private lateinit var batteryInput: EditText
    private lateinit var connectorInput: EditText
    private lateinit var vehicleSpinner: Spinner
    private lateinit var dateInput: EditText
    private lateinit var timeInput: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_add_edit)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.addEditRoot)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        formTitle = findViewById(R.id.formTitle)
        infoText = findViewById(R.id.infoText)
        messageText = findViewById(R.id.messageText)
        saveButton = findViewById(R.id.saveButton)
        makeInput = findViewById(R.id.makeInput)
        modelInput = findViewById(R.id.modelInput)
        batteryInput = findViewById(R.id.batteryInput)
        connectorInput = findViewById(R.id.connectorInput)
        vehicleSpinner = findViewById(R.id.vehicleSpinner)
        dateInput = findViewById(R.id.dateInput)
        timeInput = findViewById(R.id.timeInput)

        mode = intent.getStringExtra("mode") ?: "vehicle"

        if (mode == "booking") {
            setupBooking()
        } else {
            setupVehicle()
        }

        saveButton.setOnClickListener {
            if (mode == "booking") {
                saveBooking()
            } else {
                saveVehicle()
            }
        }

        findViewById<Button>(R.id.cancelButton).setOnClickListener {
            finish()
        }
    }

    // ---------- Vehicle ----------
    private fun setupVehicle() {
        findViewById<LinearLayout>(R.id.vehicleFields).visibility = View.VISIBLE

        val vehicleId = intent.getLongExtra("vehicleId", -1L)
        editingVehicle = AppData.currentUser?.vehicles?.find { it.vehicleId == vehicleId }

        val vehicle = editingVehicle
        if (vehicle != null) {
            formTitle.text = "Edit Vehicle"
            saveButton.text = "Save Changes"
            makeInput.setText(vehicle.make)
            modelInput.setText(vehicle.model)
            batteryInput.setText(vehicle.batteryCapacity.toString())
            connectorInput.setText(vehicle.connectorType)
        } else {
            formTitle.text = "Add Vehicle"
            saveButton.text = "Add Vehicle"
        }
    }

    private fun saveVehicle() {
        val make = makeInput.text.toString().trim()
        val model = modelInput.text.toString().trim()
        val battery = batteryInput.text.toString().toIntOrNull()
        val connector = connectorInput.text.toString().trim()

        if (make.isEmpty() || model.isEmpty() || connector.isEmpty()) {
            messageText.text = "Fill in every field."
            return
        }
        if (battery == null || battery <= 0) {
            messageText.text = "Battery capacity must be a number above 0."
            return
        }

        val user = AppData.currentUser ?: return
        val vehicle = editingVehicle

        if (vehicle != null) {
            vehicle.make = make
            vehicle.model = model
            vehicle.batteryCapacity = battery
            vehicle.connectorType = connector
        } else {
            val newId = (user.vehicles.maxOfOrNull { it.vehicleId } ?: 0L) + 1
            user.vehicles.add(Vehicle(newId, make, model, battery, connector))
        }

        AppData.store.save()
        finish()
    }

    // ---------- Booking ----------
    private fun setupBooking() {
        val stationId = intent.getLongExtra("stationId", -1L)
        val pointId = intent.getLongExtra("pointId", -1L)
        station = AppData.store.findStation(stationId)
        point = station?.chargingPoints?.find { it.pointId == pointId }

        val currentStation = station
        val currentPoint = point
        if (currentStation == null || currentPoint == null) {
            finish()
            return
        }

        findViewById<LinearLayout>(R.id.bookingFields).visibility = View.VISIBLE
        formTitle.text = "Book a Charging Point"
        saveButton.text = "Confirm Booking"

        infoText.visibility = View.VISIBLE
        infoText.text = "${currentStation.name} – Point ${currentPoint.pointId}\n" +
                "${currentPoint.powerOutput} kW – €${currentPoint.pricePerKwh}/kWh\n" +
                "Connector: ${currentPoint.connector?.connectorType ?: "none"}"

        vehicles = AppData.currentUser?.vehicles ?: emptyList()
        val vehicleNames = vehicles.map { "${it.make} ${it.model} (${it.connectorType})" }
        vehicleSpinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, vehicleNames)

        dateInput.setText(SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date()))
        dateInput.setOnClickListener { showDatePicker() }
        timeInput.setOnClickListener { showTimePicker() }

        if (vehicles.isEmpty()) {
            messageText.text = "Add a vehicle in My Vehicles before booking."
            saveButton.isEnabled = false
        }
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

    private fun saveBooking() {
        val currentStation = station ?: return
        val currentPoint = point ?: return
        if (vehicles.isEmpty()) return

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

        val pointConnector = currentPoint.connector?.connectorType ?: ""
        if (!pointConnector.equals(vehicle.connectorType, ignoreCase = true)) {
            messageText.text = "This point uses $pointConnector but your " +
                    "${vehicle.make} ${vehicle.model} uses ${vehicle.connectorType}."
            return
        }
        if (currentPoint.status != "Available") {
            messageText.text = "This point is no longer available."
            return
        }

        currentPoint.status = "Booked"
        AppData.store.createBooking(
            ChargeSession(
                startTime = format.format(bookedDate),
                status = "Booked",
                chargingPoint = currentPoint,
                userId = AppData.currentUser?.userId ?: 0,
                vehicle = vehicle,
                stationName = currentStation.name
            )
        )
        finish()
    }
}