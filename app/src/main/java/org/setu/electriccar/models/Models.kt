package org.setu.electriccar.models

data class User(
    var userId: Long = 0,
    var name: String = "",
    var email: String = "",
    var password: String = "",
    var vehicles: ArrayList<Vehicle> = ArrayList()
)

data class Vehicle(
    var vehicleId: Long = 0,
    var make: String = "",
    var model: String = "",
    var batteryCapacity: Int = 0,
    var connectorType: String = ""
) {
    fun getRange(): Int = batteryCapacity * 6
}

data class Connector(
    var connectorId: Long = 0,
    var connectorType: String = "",
    var maxPower: Int = 0
)

data class ChargingPoint(
    var pointId: Long = 0,
    var status: String = "Available",
    var powerOutput: Double = 0.0,
    var pricePerKwh: Double = 0.0,
    var connector: Connector? = null
)

data class ChargingStation(
    var stationId: Long = 0,
    var name: String = "",
    var address: String = "",
    var openingHours: String = "",
    var chargingPoints: ArrayList<ChargingPoint> = ArrayList()
)

data class ChargeSession(
    var sessionId: Long = 0,
    var startTime: String = "",
    var status: String = "",
    var chargingPoint: ChargingPoint? = null,
    var userId: Long = 0,
    var vehicle: Vehicle? = null,
    var stationName: String = ""
)

data class Route(
    var routeId: Int = 0,
    var startLocation: String = "",
    var destination: String = "",
    var distance: Int = 0,
    var estTime: Double = 0.0
) {
    fun calculateRoute() {
        estTime = distance / 50.0
    }
}