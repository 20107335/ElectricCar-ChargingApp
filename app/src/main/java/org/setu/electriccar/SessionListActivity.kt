package org.setu.electriccar

import android.os.Bundle
import android.view.Gravity
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.button.MaterialButton

class SessionListActivity : AppCompatActivity() {

    private lateinit var summaryText: TextView
    private lateinit var listLayout: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        enableEdgeToEdge()

        val scroll = createUserInterface()

        ViewCompat.setOnApplyWindowInsetsListener(scroll) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    override fun onResume() {
        super.onResume()
        if (::listLayout.isInitialized) {
            displayBookings()
        }
    }

    private fun createUserInterface(): ScrollView {

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(32, 32, 32, 32)
        }

        val title = TextView(this).apply {
            text = "My Bookings"
            textSize = 28f
            gravity = Gravity.CENTER
        }

        summaryText = TextView(this).apply {
            textSize = 18f
            gravity = Gravity.CENTER
            setPadding(0, 16, 0, 16)
        }


        listLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
        }

        root.addView(title)
        root.addView(summaryText)
        root.addView(listLayout)

        val scroll = ScrollView(this)
        scroll.addView(root)
        setContentView(scroll)

        displayBookings()
        return scroll
    }

    private fun displayBookings() {

        listLayout.removeAllViews()

        val bookings = AppData.sessions.findAll()
            .filter { it.userId == AppData.currentUser?.userId }
            .sortedBy { if (it.status == "Booked") 0 else 1 }

        val upcoming = bookings.count { it.status == "Booked" }
        summaryText.text = "Upcoming bookings: $upcoming"

        if (bookings.isEmpty()) {
            listLayout.addView(TextView(this).apply {
                text = "No bookings yet."
                textSize = 18f
                setPadding(0, 40, 0, 40)
            })
            return
        }

        for (booking in bookings) {

            val bookingLayout = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                setPadding(0, 20, 0, 20)
            }

            val bookingTitle = TextView(this).apply {
                text = "Booking ${booking.sessionId} – ${booking.status}"
                textSize = 20f
            }

            val placeText = TextView(this).apply {
                val point = booking.chargingPoint
                text = if (point != null) {
                    "${booking.stationName} – Point ${point.pointId} – €${point.pricePerKwh}/kWh"
                } else {
                    booking.stationName
                }
                textSize = 14f
            }

            val vehicleText = TextView(this).apply {
                val vehicle = booking.vehicle
                text = if (vehicle != null) {
                    "Vehicle: ${vehicle.make} ${vehicle.model}"
                } else {
                    ""
                }
                textSize = 14f
            }

            val timeText = TextView(this).apply {
                text = if (booking.status == "Booked") {
                    "Booked for ${booking.startTime}"
                } else {
                    "Was booked for ${booking.startTime}"
                }
                textSize = 16f
            }

            bookingLayout.addView(bookingTitle)
            bookingLayout.addView(placeText)
            bookingLayout.addView(vehicleText)
            bookingLayout.addView(timeText)

            if (booking.status == "Booked") {
                val cancelButton = MaterialButton(this).apply {
                    text = "Cancel Booking"
                    setOnClickListener {
                        booking.status = "Cancelled"
                        booking.chargingPoint?.status = "Available"
                        AppData.sessions.save()
                        displayBookings()
                    }
                }
                bookingLayout.addView(cancelButton)
            } else {
                val deleteButton = MaterialButton(this).apply {
                    text = "Delete"
                    setOnClickListener {
                        AppData.sessions.delete(booking.sessionId)
                        displayBookings()
                    }
                }
                bookingLayout.addView(deleteButton)
            }

            listLayout.addView(bookingLayout)
        }
    }
}