package org.setu.electriccar.stores
import org.setu.electriccar.models.ChargingStation
import java.util.concurrent.atomic.AtomicLong

class ChargingStationMemStore : ChargingStationStore {

    private val stations = ArrayList<ChargingStation>()
    private val lastId = AtomicLong()

    override fun findAll(): List<ChargingStation> {
        return stations
    }

    override fun findOne(id: Long): ChargingStation? {
        return stations.find { station -> station.stationId == id }
    }

    override fun create(station: ChargingStation) {
        station.stationId = lastId.incrementAndGet()
        stations.add(station)
    }

    override fun update(station: ChargingStation): Boolean {
        val foundStation = findOne(station.stationId)

        if (foundStation != null) {
            foundStation.name = station.name
            foundStation.address = station.address
            foundStation.openingHours = station.openingHours
            foundStation.chargingPoints = station.chargingPoints
            return true
        }

        return false
    }

    override fun delete(id: Long): Boolean {
        val foundStation = findOne(id)

        if (foundStation != null) {
            stations.remove(foundStation)
            return true
        }

        return false
    }

    override fun findByConnectorType(connectorType: String): List<ChargingStation> {
        return stations.filter { station ->
            station.chargingPoints.any { point ->
                point.connector?.connectorType.equals(
                    connectorType,
                    ignoreCase = true
                )
            }
        }
    }
}