package org.setu.electriccar.stores

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.setu.electriccar.models.ChargeSession
import org.setu.electriccar.models.ChargingPoint
import java.io.File

class ChargeSessionMemStore(context: Context) {

    private val file = File(context.filesDir, "sessions.json")
    private var sessions = ArrayList<ChargeSession>()

    init {
        if (file.exists()) {
            val type = object : TypeToken<ArrayList<ChargeSession>>() {}.type
            sessions = Gson().fromJson(file.readText(), type)
        }
    }

    fun save() {
        file.writeText(Gson().toJson(sessions))
    }

    fun findAll(): List<ChargeSession> {
        return sessions
    }

    fun create(session: ChargeSession) {
        session.sessionId = (sessions.maxOfOrNull { it.sessionId } ?: 0L) + 1
        sessions.add(session)
        save()
    }

    fun delete(id: Long) {
        sessions.removeAll { it.sessionId == id }
        save()
    }

    fun findBookedForPoint(point: ChargingPoint): ChargeSession? {
        return sessions.find { it.status == "Booked" && it.chargingPoint === point }
    }

    fun relink(stations: ChargingStationMemStore) {
        for (session in sessions) {
            val station = stations.findAll().find { it.name == session.stationName } ?: continue
            val point = station.chargingPoints.find { it.pointId == session.chargingPoint?.pointId } ?: continue

            session.chargingPoint = point

            if (session.status == "Booked") {
                point.status = "Booked"
            }
        }
    }
}