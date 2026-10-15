package org.setu.electriccar

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.setu.electriccar.models.ChargeSession
import org.setu.electriccar.models.ChargingPoint
import org.setu.electriccar.models.ChargingStation
import org.setu.electriccar.models.User
import java.io.File

class MemStore(context: Context) {

    private val usersFile = File(context.filesDir, "users.json")
    private val sessionsFile = File(context.filesDir, "sessions.json")

    var users = ArrayList<User>()          // each user holds their own vehicles
    var sessions = ArrayList<ChargeSession>()
    val stations = ArrayList<ChargingStation>()

    init {
        if (usersFile.exists()) {
            val type = object : TypeToken<ArrayList<User>>() {}.type
            users = Gson().fromJson<ArrayList<User>>(usersFile.readText(), type) ?: ArrayList()
        }
        if (sessionsFile.exists()) {
            val type = object : TypeToken<ArrayList<ChargeSession>>() {}.type
            sessions = Gson().fromJson<ArrayList<ChargeSession>>(sessionsFile.readText(), type) ?: ArrayList()
        }
    }

    fun save() {
        usersFile.writeText(Gson().toJson(users))
        sessionsFile.writeText(Gson().toJson(sessions))
    }

    // ---------- Users ----------
    fun findUser(email: String): User? {
        return users.find { it.email.equals(email, ignoreCase = true) }
    }

    fun createUser(user: User) {
        user.userId = (users.maxOfOrNull { it.userId } ?: 0L) + 1
        users.add(user)
        save()
    }

    // ---------- Stations ----------
    fun createStation(station: ChargingStation) {
        station.stationId = stations.size + 1L
        stations.add(station)
    }

    fun findStation(id: Long): ChargingStation? {
        return stations.find { it.stationId == id }
    }

    // ---------- Bookings ----------
    fun createBooking(session: ChargeSession) {
        session.sessionId = (sessions.maxOfOrNull { it.sessionId } ?: 0L) + 1
        sessions.add(session)
        save()
    }

    fun deleteBooking(id: Long) {
        sessions.removeAll { it.sessionId == id }
        save()
    }

    fun findBookingForPoint(point: ChargingPoint): ChargeSession? {
        return sessions.find { it.status == "Booked" && it.chargingPoint === point }
    }

    // After loading, point each booking at the real station point again
    fun relink() {
        for (session in sessions) {
            val station = stations.find { it.name == session.stationName } ?: continue
            val point = station.chargingPoints.find { it.pointId == session.chargingPoint?.pointId } ?: continue
            session.chargingPoint = point
            if (session.status == "Booked") {
                point.status = "Booked"
            }
        }
    }
}