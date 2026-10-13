package org.setu.electriccar.stores

import org.setu.electriccar.models.Vehicle

interface VehicleStore {
    fun findAll(): List<Vehicle>
    fun findOne(id: Long): Vehicle?
    fun create(vehicle: Vehicle)
    fun update(vehicle: Vehicle): Boolean
    fun delete(id: Long): Boolean
}