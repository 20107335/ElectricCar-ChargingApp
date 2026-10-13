package org.setu.electriccar.stores

import org.setu.electriccar.models.ChargeSession
import org.setu.electriccar.models.ChargingPoint

interface ChargeSessionStore {
    fun findAll(): List<ChargeSession>
    fun findOne(id: Long): ChargeSession?
    fun create(session: ChargeSession)
    fun update(session: ChargeSession): Boolean
    fun delete(id: Long): Boolean
    fun findActiveForPoint(point: ChargingPoint): ChargeSession?
}