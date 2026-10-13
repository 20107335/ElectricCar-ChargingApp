package org.setu.electriccar

import org.setu.electriccar.stores.ChargingStationMemStore
import org.setu.electriccar.stores.ChargingStationStore
import org.setu.electriccar.stores.VehicleMemStore
import org.setu.electriccar.stores.VehicleStore

object AppData {
    val stations: ChargingStationStore = ChargingStationMemStore()
    val vehicles: VehicleStore = VehicleMemStore()
}