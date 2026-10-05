package org.setu.electriccar.models

data class Vehicle(
    var vehicleId: Long = 0,
    var make: String = "",
    var model: String = "",
    var batteryCapacity: Int = 0,
    var connectorType: String = ""
)