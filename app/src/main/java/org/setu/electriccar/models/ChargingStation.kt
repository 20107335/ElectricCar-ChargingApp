package org.setu.electriccar.models

data class ChargingStation(
    var stationId: Long = 0,
    var name: String = "",
    var address: String = "",
    var openingHours: String = "",
    var chargingPoints: ArrayList<ChargingPoint> = ArrayList()
)