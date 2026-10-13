package org.setu.electriccar.stores

import org.setu.electriccar.models.ChargeSession
import org.setu.electriccar.models.ChargingPoint
import java.util.concurrent.atomic.AtomicLong

class ChargeSessionMemStore : ChargeSessionStore {

    private val sessions = ArrayList<ChargeSession>()
    private val lastId = AtomicLong()

    override fun findAll(): List<ChargeSession> {
        return sessions
    }

    override fun findOne(id: Long): ChargeSession? {
        return sessions.find { session -> session.sessionId == id }
    }

    override fun create(session: ChargeSession) {
        session.sessionId = lastId.incrementAndGet()
        sessions.add(session)
    }

    override fun update(session: ChargeSession): Boolean {
        val found = findOne(session.sessionId)

        if (found != null) {
            found.startTime = session.startTime
            found.status = session.status
            found.chargingPoint = session.chargingPoint
            found.userId = session.userId
            found.vehicle = session.vehicle
            found.stationName = session.stationName
            return true
        }

        return false
    }

    override fun delete(id: Long): Boolean {
        val found = findOne(id)

        if (found != null) {
            sessions.remove(found)
            return true
        }

        return false
    }

    override fun findBookedForPoint(point: ChargingPoint): ChargeSession? {
        return sessions.find { session ->
            session.status == "Booked" && session.chargingPoint === point
        }
    }
}