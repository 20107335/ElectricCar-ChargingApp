package org.setu.electriccar.stores

import org.setu.electriccar.models.Vehicle
import java.util.concurrent.atomic.AtomicLong

class VehicleMemStore : VehicleStore {

    private val vehicles = ArrayList<Vehicle>()
    private val lastId = AtomicLong()

    override fun findAll(): List<Vehicle> {
        return vehicles
    }

    override fun findOne(id: Long): Vehicle? {
        return vehicles.find { vehicle -> vehicle.vehicleId == id }
    }

    override fun create(vehicle: Vehicle) {
        vehicle.vehicleId = lastId.incrementAndGet()
        vehicles.add(vehicle)
    }

    override fun update(vehicle: Vehicle): Boolean {
        val foundVehicle = findOne(vehicle.vehicleId)

        if (foundVehicle != null) {
            foundVehicle.make = vehicle.make
            foundVehicle.model = vehicle.model
            foundVehicle.batteryCapacity = vehicle.batteryCapacity
            foundVehicle.connectorType = vehicle.connectorType
            return true
        }

        return false
    }

    override fun delete(id: Long): Boolean {
        val foundVehicle = findOne(id)

        if (foundVehicle != null) {
            vehicles.remove(foundVehicle)
            return true
        }

        return false
    }
}