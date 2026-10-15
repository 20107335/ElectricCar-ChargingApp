package org.setu.electriccar

import android.os.Bundle
import android.text.InputType
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.Spinner
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.button.MaterialButton
import org.setu.electriccar.models.ChargingPoint
import org.setu.electriccar.models.ChargingStation
import org.setu.electriccar.models.Connector

class AddEditPointActivity : AppCompatActivity() {

    private val statuses = arrayOf("Available", "Out of Order")

    private lateinit var statusSpinner: Spinner
    private lateinit var powerInput: EditText
    private lateinit var priceInput: EditText
    private lateinit var connectorTypeInput: EditText
    private lateinit var maxPowerInput: EditText

    private lateinit var station: ChargingStation
    private var editingPointId: Long = -1L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        enableEdgeToEdge()

        val stationId = intent.getLongExtra("stationId", -1L)
        val foundStation = AppData.stations.findOne(stationId)

        if (foundStation == null) {
            finish()
            return
        }
        station = foundStation

        val scroll = createUserInterface()

        ViewCompat.setOnApplyWindowInsetsListener(scroll) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        editingPointId = intent.getLongExtra("pointId", -1L)

        if (editingPointId != -1L) {
            loadExistingPoint(editingPointId)
        }
    }

    private fun createUserInterface(): ScrollView {

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(32, 32, 32, 32)
        }

        val heading = TextView(this).apply {
            text = "Point for ${station.name}"
            textSize = 22f
            setPadding(0, 0, 0, 24)
        }

        val statusLabel = TextView(this).apply {
            text = "Status"
            textSize = 14f
        }

        statusSpinner = Spinner(this).apply {
            adapter = ArrayAdapter(
                this@AddEditPointActivity,
                android.R.layout.simple_spinner_dropdown_item,
                statuses
            )
        }

        val decimalInput = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL

        powerInput = EditText(this).apply {
            hint = "Power output (kW)"
            inputType = decimalInput
        }

        priceInput = EditText(this).apply {
            hint = "Price per kWh (€)"
            inputType = decimalInput
        }

        connectorTypeInput = EditText(this).apply {
            hint = "Connector type (e.g. CCS, Type 2, CHAdeMO)"
        }

        maxPowerInput = EditText(this).apply {
            hint = "Connector max power (kW)"
            inputType = InputType.TYPE_CLASS_NUMBER
        }

        val saveButton = MaterialButton(this).apply {
            text = "Save"
            setOnClickListener {
                savePoint()
            }
        }

        val cancelButton = MaterialButton(this).apply {
            text = "Cancel"
            setOnClickListener {
                finish()
            }
        }

        root.addView(heading)
        root.addView(statusLabel)
        root.addView(statusSpinner)
        root.addView(powerInput)
        root.addView(priceInput)
        root.addView(connectorTypeInput)
        root.addView(maxPowerInput)
        root.addView(saveButton)
        root.addView(cancelButton)

        val scroll = ScrollView(this)
        scroll.addView(root)
        setContentView(scroll)

        return scroll
    }

    private fun loadExistingPoint(pointId: Long) {

        val point = station.chargingPoints.find { it.pointId == pointId }

        if (point == null) {
            finish()
            return
        }

        val statusIndex = statuses.indexOf(point.status)
        if (statusIndex >= 0) {
            statusSpinner.setSelection(statusIndex)
        }

        powerInput.setText(point.powerOutput.toString())
        priceInput.setText(point.pricePerKwh.toString())
        connectorTypeInput.setText(point.connector?.connectorType ?: "")
        maxPowerInput.setText(point.connector?.maxPower?.toString() ?: "")
    }

    private fun savePoint() {

        val status = statusSpinner.selectedItem.toString()
        val connectorType = connectorTypeInput.text.toString().trim()

        val power = powerInput.text.toString().toDoubleOrNull()
        if (power == null || power <= 0) {
            powerInput.error = "Enter a power above 0"
            return
        }

        val price = priceInput.text.toString().toDoubleOrNull()
        if (price == null || price < 0) {
            priceInput.error = "Enter a valid price"
            return
        }

        if (connectorType.isEmpty()) {
            connectorTypeInput.error = "Connector type is required"
            return
        }

        val maxPower = maxPowerInput.text.toString().toIntOrNull()
        if (maxPower == null || maxPower <= 0) {
            maxPowerInput.error = "Enter a max power above 0"
            return
        }

        if (editingPointId == -1L) {

            val newId = (station.chargingPoints.maxOfOrNull { it.pointId } ?: 0L) + 1

            val point = ChargingPoint(
                pointId = newId,
                status = status,
                powerOutput = power,
                pricePerKwh = price,
                connector = Connector(
                    connectorId = newId,
                    connectorType = connectorType,
                    maxPower = maxPower
                )
            )

            station.chargingPoints.add(point)

        } else {

            val point = station.chargingPoints.find { it.pointId == editingPointId }

            if (point != null) {
                if (point.status != "Booked") {
                    point.status = status
                }
                point.powerOutput = power
                point.pricePerKwh = price
                point.connector = Connector(
                    connectorId = point.pointId,
                    connectorType = connectorType,
                    maxPower = maxPower
                )
            }
        }

        finish()
    }
}