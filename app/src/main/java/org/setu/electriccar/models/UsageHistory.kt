package org.setu.electriccar.models

data class UsageHistory(
    var historyId: Long = 0,
    var totalEnergy: Double = 0.0,
    var totalCost: Double = 0.0,
    var sessions: ArrayList<ChargeSession> = ArrayList()
)