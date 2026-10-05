package org.setu.electriccar.models

data class ChargeSession(
    var sessionId: Long = 0,
    var startTime: String = "",
    var endTime: String = "",
    var energyUsed: Double = 0.0,
    var cost: Double = 0.0,
    var status: String = "",
    var chargingPoint: ChargingPoint? = null
)