package org.setu.electriccar

import android.content.Context
import android.content.SharedPreferences
import org.setu.electriccar.models.ChargingPoint
import org.setu.electriccar.models.ChargingStation
import org.setu.electriccar.models.Connector
import org.setu.electriccar.models.User
import org.setu.electriccar.stores.ChargeSessionMemStore
import org.setu.electriccar.stores.ChargingStationMemStore
import org.setu.electriccar.stores.UserMemStore

object AppData {
    val stations = ChargingStationMemStore()
    lateinit var sessions: ChargeSessionMemStore
    lateinit var users: UserMemStore

    var currentUser: User? = null

    private lateinit var prefs: SharedPreferences

    fun setup(context: Context) {
        if (::prefs.isInitialized) {
            return
        }

        users = UserMemStore(context)
        sessions = ChargeSessionMemStore(context)
        sessions.relink(stations)
        prefs = context.getSharedPreferences("ev_prefs", Context.MODE_PRIVATE)

        val savedEmail = prefs.getString("loggedInEmail", null)
        if (savedEmail != null) {
            currentUser = users.findByEmail(savedEmail)
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

    fun saveCurrentUser() {
        users.save()
    }

    init {
        addStation("SETU Cork Road Charger", "Cork Road, Waterford", "24/7",
            point(1, 50.0, 0.45, "CCS", 50),
            point(2, 22.0, 0.35, "Type 2", 22))

        addStation("City Centre – The Quay", "The Quay, Waterford", "07:00–23:00",
            point(1, 22.0, 0.39, "Type 2", 22),
            point(2, 22.0, 0.39, "Type 2", 22))

        addStation("Ardkeen Shopping Centre", "Dunmore Road, Waterford", "08:00–22:00",
            point(1, 150.0, 0.59, "CCS", 150),
            point(2, 50.0, 0.49, "CHAdeMO", 50))

        addStation("University Hospital Waterford", "Dunmore Road, Ardkeen", "24/7",
            point(1, 22.0, 0.35, "Type 2", 22),
            point(2, 22.0, 0.35, "Type 2", 22))

        addStation("Ballybeg Community Centre", "Ballybeg Drive, Ballybeg", "08:00–21:00",
            point(1, 22.0, 0.33, "Type 2", 22))

        addStation("Kilbarry Business Park", "Kilbarry, Waterford", "07:00–19:00",
            point(1, 50.0, 0.45, "CCS", 50),
            point(2, 50.0, 0.45, "CHAdeMO", 50))

        addStation("Ferrybank Shopping Centre", "Abbey Road, Ferrybank", "08:00–22:00",
            point(1, 50.0, 0.47, "CCS", 50),
            point(2, 22.0, 0.37, "Type 2", 22))

        addStation("Butlerstown Service Station", "N25, Butlerstown", "24/7",
            point(1, 150.0, 0.59, "CCS", 150),
            point(2, 50.0, 0.49, "CHAdeMO", 50))

        addStation("Waterford Airport", "Killowen, Waterford", "06:00–22:00",
            point(1, 22.0, 0.39, "Type 2", 22))

        addStation("Tramore Promenade", "Strand Road, Tramore", "24/7",
            point(1, 50.0, 0.45, "CCS", 50))

        addStation("Dunmore East Harbour", "Harbour Road, Dunmore East", "08:00–20:00",
            point(1, 22.0, 0.35, "Type 2", 22))

        addStation("Passage East Ferry", "Passage East", "07:00–21:00",
            point(1, 22.0, 0.35, "Type 2", 22))
    }

    private fun addStation(
        name: String,
        address: String,
        hours: String,
        vararg points: ChargingPoint
    ) {
        stations.create(
            ChargingStation(
                name = name,
                address = address,
                openingHours = hours,
                chargingPoints = arrayListOf(*points)
            )
        )
    }

    private fun point(
        id: Long,
        power: Double,
        price: Double,
        connectorType: String,
        maxPower: Int
    ): ChargingPoint {
        return ChargingPoint(
            pointId = id,
            status = "Available",
            powerOutput = power,
            pricePerKwh = price,
            connector = Connector(
                connectorId = id,
                connectorType = connectorType,
                maxPower = maxPower
            )
        )
    }
}