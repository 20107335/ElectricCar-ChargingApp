package org.setu.electriccar.models

data class ChargeSession(
    var sessionId: Long = 0,
    var startTime: String = "",
    var status: String = "",
    var chargingPoint: ChargingPoint? = null,
    var userId: Long = 0,
    var vehicle: Vehicle? = null,
    var stationName: String = ""
)