package org.setu.electriccar.models

data class Connector(
    var connectorId: Long = 0,
    var connectorType: String = "",
    var maxPower: Int = 0
)