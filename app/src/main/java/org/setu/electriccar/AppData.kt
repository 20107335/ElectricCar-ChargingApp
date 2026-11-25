package org.setu.electriccar

import android.content.Context
import android.content.SharedPreferences
import org.setu.electriccar.models.ChargingPoint
import org.setu.electriccar.models.ChargingStation
import org.setu.electriccar.models.Connector
import org.setu.electriccar.models.User

object AppData {

    lateinit var store: MemStore
    var currentUser: User? = null
    private lateinit var prefs: SharedPreferences

    fun setup(context: Context) {
        if (::store.isInitialized) {
            return
        }
        store = MemStore(context)
        addStations()
        store.relink()

        prefs = context.getSharedPreferences("ev_prefs", Context.MODE_PRIVATE)
        val savedEmail = prefs.getString("loggedInEmail", null)
        if (savedEmail != null) {
            currentUser = store.findUser(savedEmail)
        }
    }

    fun login(user: User) {
        currentUser = user
        prefs.edit().putString("loggedInEmail", user.email).apply()
    }

    fun logout() {
        currentUser = null
        prefs.edit().remove("loggedInEmail").apply()
    }

    private fun addStations() {
        addStation("SETU Cork Road Charger", "Cork Road, Waterford", "24 hours",
            point(1, 22.0, 0.35, "Type 2", 22),
            point(2, 50.0, 0.45, "CCS", 50))

        addStation("City Centre – The Quay", "The Quay, Waterford City", "07:00 – 23:00",
            point(1, 22.0, 0.35, "Type 2", 22),
            point(2, 22.0, 0.35, "Type 2", 22),
            point(3, 50.0, 0.45, "CHAdeMO", 50))

        addStation("Ardkeen Shopping Centre", "Dunmore Road, Waterford", "08:00 – 22:00",
            point(1, 22.0, 0.35, "Type 2", 22),
            point(2, 50.0, 0.45, "CCS", 50))

        addStation("University Hospital Waterford", "Dunmore Road, Waterford", "24 hours",
            point(1, 7.0, 0.30, "Type 2", 7),
            point(2, 22.0, 0.35, "Type 2", 22))

        addStation("Ballybeg Community Centre", "Ballybeg, Waterford", "09:00 – 21:00",
            point(1, 7.0, 0.30, "Type 2", 7))

        addStation("Kilbarry Business Park", "Kilbarry, Waterford", "07:00 – 20:00",
            point(1, 22.0, 0.35, "Type 2", 22),
            point(2, 150.0, 0.55, "CCS", 150))

        addStation("Ferrybank Shopping Centre", "Ferrybank, Waterford", "08:00 – 22:00",
            point(1, 22.0, 0.35, "Type 2", 22),
            point(2, 50.0, 0.45, "CHAdeMO", 50))

        addStation("Butlerstown Service Station", "N25, Butlerstown", "24 hours",
            point(1, 150.0, 0.55, "CCS", 150),
            point(2, 50.0, 0.45, "CHAdeMO", 50))

        addStation("Waterford Airport", "Killowen, Waterford", "06:00 – 22:00",
            point(1, 22.0, 0.35, "Type 2", 22))

        addStation("Tramore Promenade", "Strand Road, Tramore", "24 hours",
            point(1, 22.0, 0.35, "Type 2", 22),
            point(2, 50.0, 0.45, "CCS", 50))

        addStation("Dunmore East Harbour", "Harbour Road, Dunmore East", "08:00 – 20:00",
            point(1, 7.0, 0.30, "Type 2", 7))

        addStation("Passage East Ferry", "Passage East, Co. Waterford", "07:00 – 21:00",
            point(1, 22.0, 0.35, "Type 2", 22))
    }

    private fun addStation(name: String, address: String, openingHours: String, vararg points: ChargingPoint) {
        store.createStation(
            ChargingStation(
                name = name,
                address = address,
                openingHours = openingHours,
                chargingPoints = arrayListOf(*points)
            )
        )
    }

    private fun point(id: Long, power: Double, price: Double, connectorType: String, maxPower: Int): ChargingPoint {
        return ChargingPoint(
            pointId = id,
            status = "Available",
            powerOutput = power,
            pricePerKwh = price,
            connector = Connector(connectorId = id, connectorType = connectorType, maxPower = maxPower)
        )
    }
}