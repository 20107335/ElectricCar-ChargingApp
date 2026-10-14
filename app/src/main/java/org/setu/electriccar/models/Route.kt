package org.setu.electriccar.models

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