package org.setu.electriccar

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import org.setu.electriccar.models.Vehicle

class AddEditVehicleActivity : AppCompatActivity() {

    private lateinit var makeInput: EditText
    private lateinit var modelInput: EditText
    private lateinit var batteryInput: EditText
    private lateinit var connectorInput: EditText

    private var editingId: Long = -1L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        createUserInterface()

        editingId = intent.getLongExtra("id", -1L)

        if (editingId != -1L) {
            loadVehicle(editingId)
        }
    }

    private fun createUserInterface() {

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(32, 160, 32, 32)
        }

        makeInput = EditText(this).apply { hint = "Make" }
        modelInput = EditText(this).apply { hint = "Model" }
        batteryInput = EditText(this).apply { hint = "Battery (kWh)" }
        connectorInput = EditText(this).apply { hint = "Connector type" }

        val saveButton = Button(this).apply {
            text = "Save"
            setOnClickListener { saveVehicle() }
        }

        val cancelButton = Button(this).apply {
            text = "Cancel"
            setOnClickListener { finish() }
        }

        root.addView(makeInput)
        root.addView(modelInput)
        root.addView(batteryInput)
        root.addView(connectorInput)
        root.addView(saveButton)
        root.addView(cancelButton)

        setContentView(root)
    }

    private fun loadVehicle(id: Long) {

        val vehicle = AppData.vehicles.findOne(id) ?: return

        makeInput.setText(vehicle.make)
        modelInput.setText(vehicle.model)
        batteryInput.setText(vehicle.batteryCapacity.toString())
        connectorInput.setText(vehicle.connectorType)
    }

    private fun saveVehicle() {

        val make = makeInput.text.toString().trim()
        val model = modelInput.text.toString().trim()
        val connector = connectorInput.text.toString().trim()
        val battery = batteryInput.text.toString().toIntOrNull()

        if (make.isEmpty()) {
            makeInput.error = "Required"
            return
        }

        if (battery == null) {
            batteryInput.error = "Enter a number"
            return
        }

        val vehicle = Vehicle(
            make = make,
            model = model,
            batteryCapacity = battery,
            connectorType = connector
        )

        if (editingId == -1L) {
            AppData.vehicles.create(vehicle)
            AppData.currentUser?.vehicles?.add(vehicle)
        } else {
            vehicle.vehicleId = editingId
            AppData.vehicles.update(vehicle)
        }

        finish()
    }
}