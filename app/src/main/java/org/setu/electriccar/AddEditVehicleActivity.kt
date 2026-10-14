package org.setu.electriccar

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import org.setu.electriccar.models.Vehicle

class AddEditVehicleActivity : AppCompatActivity() {

    private lateinit var makeInput: EditText
    private lateinit var modelInput: EditText
    private lateinit var batteryInput: EditText
    private lateinit var connectorInput: EditText

    private var editingId: Long? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_add_edit_vehicle)

        makeInput = findViewById(R.id.makeInput)
        modelInput = findViewById(R.id.modelInput)
        batteryInput = findViewById(R.id.batteryInput)
        connectorInput = findViewById(R.id.connectorInput)

        val saveButton = findViewById<Button>(R.id.saveButton)
        val cancelButton = findViewById<Button>(R.id.cancelButton)

        editingId = intent.getLongExtra("id", -1L).takeIf { it != -1L }

        if (editingId != null) {
            findViewById<TextView>(R.id.formTitle).text = "Edit Vehicle"
            loadExistingVehicle(editingId!!)
        }

        saveButton.setOnClickListener {
            saveVehicle()
        }

        cancelButton.setOnClickListener {
            finish()
        }
    }

    private fun myVehicles(): ArrayList<Vehicle> {
        return AppData.currentUser?.vehicles ?: ArrayList()
    }

    private fun loadExistingVehicle(id: Long) {
        val vehicle = myVehicles().find { it.vehicleId == id }

        if (vehicle == null) {
            finish()
            return
        }

        makeInput.setText(vehicle.make)
        modelInput.setText(vehicle.model)
        batteryInput.setText(vehicle.batteryCapacity.toString())
        connectorInput.setText(vehicle.connectorType)
    }

    private fun saveVehicle() {
        val make = makeInput.text.toString().trim()
        val model = modelInput.text.toString().trim()
        val connector = connectorInput.text.toString().trim()

        if (make.isEmpty()) {
            makeInput.error = "Make is required"
            return
        }

        val battery = batteryInput.text.toString().toIntOrNull()

        if (battery == null) {
            batteryInput.error = "Enter a valid number"
            return
        }

        if (editingId == null) {
            val newId = (myVehicles().maxOfOrNull { it.vehicleId } ?: 0L) + 1

            myVehicles().add(
                Vehicle(
                    vehicleId = newId,
                    make = make,
                    model = model,
                    batteryCapacity = battery,
                    connectorType = connector
                )
            )
        } else {
            val vehicle = myVehicles().find { it.vehicleId == editingId }

            if (vehicle != null) {
                vehicle.make = make
                vehicle.model = model
                vehicle.batteryCapacity = battery
                vehicle.connectorType = connector
            }
        }

        finish()
    }
}