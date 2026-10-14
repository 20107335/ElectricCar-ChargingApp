package org.setu.electriccar

import org.setu.electriccar.models.ChargingPoint
import org.setu.electriccar.models.ChargingStation
import org.setu.electriccar.models.Connector
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

    init {
        addStation("SETU Cork Road Charger", "Cork Road, Waterford", "24/7",
            point(1, 50.0, 0.45, "CCS", 50),
            point(2, 22.0, 0.35, "Type 2", 22))

        addStation("City Centre – The Quay", "The Quay, Waterford", "07:00–23:00",
            point(1, 22.0, 0.39, "Type 2", 22),
            point(2, 22.0, 0.39, "Type 2", 22))

        addStation("Ardkeen Shopping Centre", "Dunmore Road, Waterford", "08:00–22:00",
            point(1, 150.0, 0.59, "CCS", 150),
            point(2, 50.0, 0.49, "CHAdeMO", 50))

        addStation("Tramore Promenade", "Strand Road, Tramore", "24/7",
            point(1, 50.0, 0.45, "CCS", 50))

        addStation("Dunmore East Harbour", "Harbour Road, Dunmore East", "08:00–20:00",
            point(1, 22.0, 0.35, "Type 2", 22))
    }

    private fun addStation(
        name: String,
        address: String,
        hours: String,
        vararg points: ChargingPoint
    ) {
        stations.create(
            ChargingStation(
                name = name,
                address = address,
                openingHours = hours,
                chargingPoints = arrayListOf(*points)
            )
        )
    }

    private fun point(
        id: Long,
        power: Double,
        price: Double,
        connectorType: String,
        maxPower: Int
    ): ChargingPoint {
        return ChargingPoint(
            pointId = id,
            status = "Available",
            powerOutput = power,
            pricePerKwh = price,
            connector = Connector(
                connectorId = id,
                connectorType = connectorType,
                maxPower = maxPower
            )
        )
    }
}