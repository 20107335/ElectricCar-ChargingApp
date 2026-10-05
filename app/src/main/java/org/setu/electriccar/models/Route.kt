package org.setu.electriccar.models

data class Route(
    var routeId: Long = 0,
    var startLocation: String = "",
    var destination: String = "",
    var distance: Double = 0.0,
    var estimatedTime: Double = 0.0,
    var chargingStops: ArrayList<ChargingStation> = ArrayList()
)