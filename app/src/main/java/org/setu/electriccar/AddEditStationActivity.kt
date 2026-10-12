package org.setu.electriccar

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import org.setu.electriccar.models.ChargingStation

class AddEditStationActivity : AppCompatActivity() {

    private lateinit var nameInput: EditText
    private lateinit var addressInput: EditText
    private lateinit var hoursInput: EditText

    private var editingId: Long = -1L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val scroll = createUserInterface()

        ViewCompat.setOnApplyWindowInsetsListener(scroll) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        editingId = intent.getLongExtra("id", -1L)

        if (editingId != -1L) {
            loadExistingStation(editingId)
        }
    }

    private fun createUserInterface(): ScrollView {

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(32, 32, 32, 32)
        }

        nameInput = EditText(this).apply {
            hint = "Station name"
        }

        addressInput = EditText(this).apply {
            hint = "Address"
        }

        hoursInput = EditText(this).apply {
            hint = "Opening hours (e.g. 24/7)"
        }

        val saveButton = Button(this).apply {
            text = "Save"
            setOnClickListener {
                saveStation()
            }
        }

        val cancelButton = Button(this).apply {
            text = "Cancel"
            setOnClickListener {
                finish()
            }
        }

        root.addView(nameInput)
        root.addView(addressInput)
        root.addView(hoursInput)
        root.addView(saveButton)
        root.addView(cancelButton)

        val scroll = ScrollView(this)
        scroll.addView(root)
        setContentView(scroll)

        return scroll
    }

    private fun loadExistingStation(id: Long) {

        val station = AppData.stations.findOne(id)

        if (station == null) {
            Toast.makeText(this, "Station not found", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        nameInput.setText(station.name)
        addressInput.setText(station.address)
        hoursInput.setText(station.openingHours)
    }

    private fun saveStation() {

        val name = nameInput.text.toString().trim()
        val address = addressInput.text.toString().trim()
        val hours = hoursInput.text.toString().trim()

        if (name.isEmpty()) {
            nameInput.error = "Name is required"
            return
        }

        if (address.isEmpty()) {
            addressInput.error = "Address is required"
            return
        }

        if (editingId == -1L) {

            val station = ChargingStation(
                name = name,
                address = address,
                openingHours = hours
            )

            AppData.stations.create(station)

            Toast.makeText(this, "Station created", Toast.LENGTH_SHORT).show()

        } else {

            val existing = AppData.stations.findOne(editingId)

            val station = ChargingStation(
                stationId = editingId,
                name = name,
                address = address,
                openingHours = hours,
                chargingPoints = existing?.chargingPoints ?: ArrayList()
            )

            AppData.stations.update(station)

            Toast.makeText(this, "Station updated", Toast.LENGTH_SHORT).show()
        }

        finish()
    }
}