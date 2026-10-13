package org.setu.electriccar

import org.setu.electriccar.models.User
import org.setu.electriccar.stores.ChargeSessionMemStore
import org.setu.electriccar.stores.ChargeSessionStore
import org.setu.electriccar.stores.ChargingStationMemStore
import org.setu.electriccar.stores.ChargingStationStore
import org.setu.electriccar.stores.UserMemStore
import org.setu.electriccar.stores.UserStore
import org.setu.electriccar.stores.VehicleMemStore
import org.setu.electriccar.stores.VehicleStore

object AppData {
    val stations: ChargingStationStore = ChargingStationMemStore()
    val vehicles: VehicleStore = VehicleMemStore()
    val sessions: ChargeSessionStore = ChargeSessionMemStore()
    val users: UserStore = UserMemStore()

    var currentUser: User? = null
}