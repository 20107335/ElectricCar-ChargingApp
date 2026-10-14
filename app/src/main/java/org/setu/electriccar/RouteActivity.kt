package org.setu.electriccar

import android.os.Bundle
import android.text.InputType
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import org.setu.electriccar.models.Route

class RouteActivity : AppCompatActivity() {

    companion object {
        var nextRouteId = 1
    }

    private lateinit var startInput: EditText
    private lateinit var distanceInput: EditText
    private lateinit var resultText: TextView

    private var stationName = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)


        val stationId = intent.getLongExtra("stationId", -1L)
        stationName = AppData.stations.findOne(stationId)?.name ?: "Station"

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(32, 160, 32, 32)
        }

        val heading = TextView(this).apply {
            text = "Route to $stationName"
            textSize = 26f
        }

        startInput = EditText(this).apply {
            hint = "Starting from (e.g. Tramore)"
        }

        distanceInput = EditText(this).apply {
            hint = "Distance (km)"
            inputType = InputType.TYPE_CLASS_NUMBER
        }

        val calculateButton = Button(this).apply {
            text = "Calculate Route"
            setOnClickListener { calculateRoute() }
        }


        resultText = TextView(this).apply {
            textSize = 18f
            setPadding(0, 32, 0, 0)
        }

        root.addView(heading)
        root.addView(startInput)
        root.addView(distanceInput)
        root.addView(calculateButton)
        root.addView(resultText)

        setContentView(root)
    }

    private fun calculateRoute() {

        val start = startInput.text.toString().trim()
        val distance = distanceInput.text.toString().toIntOrNull()

        if (start.isEmpty()) {
            startInput.error = "Required"
            return
        }

        if (distance == null) {
            distanceInput.error = "Enter a number"
            return
        }

        val route = Route(
            routeId = nextRouteId,
            startLocation = start,
            destination = stationName,
            distance = distance
        )

        route.calculateRoute()
        nextRouteId++

        val minutes = (route.estTime * 60).toInt()

        resultText.text = "Route ${route.routeId}\n\n" +
                "From: ${route.startLocation}\n" +
                "To: ${route.destination}\n" +
                "Distance: ${route.distance} km\n" +
                "Estimated time: $minutes min"
    }
}