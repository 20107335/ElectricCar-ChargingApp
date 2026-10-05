package org.setu.electriccar.models

data class User(
    var userId: Long = 0,
    var name: String = "",
    var email: String = "",
    var password: String = "",
    var vehicles: ArrayList<Vehicle> = ArrayList()
)