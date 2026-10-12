package org.setu.electriccar

import org.setu.electriccar.stores.ChargingStationMemStore
import org.setu.electriccar.stores.ChargingStationStore

object AppData {
    val stations: ChargingStationStore = ChargingStationMemStore()
}