package org.setu.electriccar

import org.junit.Assert.assertEquals
import org.junit.Test
import org.setu.electriccar.models.Route
import org.setu.electriccar.models.User
import org.setu.electriccar.models.Vehicle

class ModelsTest {

    @Test
    fun vehicleRange() {
        val vehicle = Vehicle(batteryCapacity = 60)
        assertEquals(360, vehicle.getRange())
    }

    @Test
    fun routeTime() {
        val route = Route(1, "SETU", "Tramore", 100, 0.0)
        route.calculateRoute()
        assertEquals(2.0, route.estTime, 0.0)
    }

    @Test
    fun userHasNoVehiclesAtStart() {
        val user = User(name = "Eimhin")
        assertEquals(0, user.vehicles.size)
    }

    @Test
    fun userAddVehicle() {
        val user = User(name = "Eimhin")
        user.vehicles.add(Vehicle(make = "Nissan"))
        assertEquals(1, user.vehicles.size)
    }
}