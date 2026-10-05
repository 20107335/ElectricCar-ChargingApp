package org.setu.electriccar.models

data class ChargingPoint(
    var pointId: Long = 0,
    var status: String = "",
    var powerOutput: Double = 0.0,
    var pricePerKwh: Double = 0.0,
    var connector: Connector? = null
)