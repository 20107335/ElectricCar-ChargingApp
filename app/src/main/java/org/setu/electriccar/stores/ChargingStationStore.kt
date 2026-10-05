package org.setu.electriccar.stores

import org.setu.electriccar.models.ChargingStation

interface ChargingStationStore {
    fun findAll(): List<ChargingStation>
    fun findOne(id: Long): ChargingStation?
    fun create(station: ChargingStation)
    fun update(station: ChargingStation): Boolean
    fun delete(id: Long): Boolean
    fun findByConnectorType(connectorType: String): List<ChargingStation>
}