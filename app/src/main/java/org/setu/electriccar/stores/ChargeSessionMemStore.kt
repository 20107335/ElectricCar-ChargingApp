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
            found.endTime = session.endTime
            found.energyUsed = session.energyUsed
            found.cost = session.cost
            found.status = session.status
            found.chargingPoint = session.chargingPoint
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

    override fun findActiveForPoint(point: ChargingPoint): ChargeSession? {
        return sessions.find { session ->
            session.status == "Active" && session.chargingPoint === point
        }
    }
}
