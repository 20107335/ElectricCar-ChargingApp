package org.setu.electriccar.main

import org.setu.electriccar.models.ChargingPoint
import org.setu.electriccar.models.ChargingStation
import org.setu.electriccar.models.Connector
import org.setu.electriccar.stores.ChargingStationMemStore
val store = ChargingStationMemStore()

fun main() {
    println("=== Electric Car Charging Station App ===")
    var input: Int
    do {
        input = menu()
        when (input) {
            1 -> addStation()
            2 -> listStations()
            3 -> updateStation()
            4 -> deleteStation()
            5 -> searchStation()
            6 -> filterByConnector()
            0 -> println("\nExiting application. Goodbye!")
            else -> println("\nInvalid option. Please try again.")
        }

    } while (input != 0)
}
fun menu(): Int {
    println()
    println("------------------------------------------")
    println(" ELECTRIC CAR CHARGING STATION APP")
    println("------------------------------------------")
    println(" 1. Add Charging Station")
    println(" 2. List All Charging Stations")
    println(" 3. Update a Charging Station")
    println(" 4. Delete a Charging Station")
    println(" 5. Search Charging Station by ID")
    println(" 6. Filter by Connector Type")
    println(" 0. Exit")
    print("\nEnter option: ")
    return readlnOrNull()?.toIntOrNull() ?: -1
}
fun addStation() {
    println("\n--- Add Charging Station ---")
    print("Enter Station Name: ")
    val name = readlnOrNull()?.trim().orEmpty()
    print("Enter Address: ")
    val address = readlnOrNull()?.trim().orEmpty()
    print("Enter Opening Hours: ")
    val openingHours = readlnOrNull()?.trim().orEmpty()
    print("Enter Connector Type (CCS, CHAdeMO, Type 2): ")
    val connectorType = readlnOrNull()?.trim().orEmpty()
    print("Enter Maximum Power (kW): ")
    val maxPower = readlnOrNull()?.toIntOrNull() ?: 0
    print("Enter Price per kWh: ")
    val pricePerKwh = readlnOrNull()?.toDoubleOrNull() ?: 0.0
    if (name.isNotEmpty()) {
        val connector = Connector(
            connectorType = connectorType,
            maxPower = maxPower
        )
        val chargingPoint = ChargingPoint(
            status = "Available",
            powerOutput = maxPower.toDouble(),
            pricePerKwh = pricePerKwh,
            connector = connector
        )
        val station = ChargingStation(
            name = name,
            address = address,
            openingHours = openingHours
        )
        station.chargingPoints.add(chargingPoint)
        store.create(station)
        println("Charging station added successfully with ID: ${station.stationId}")
    } else {
        println("Station name cannot be empty. Creation cancelled.")
    }
}
fun listStations() {
    println("\n--- All Charging Stations ---")
    val stations = store.findAll()
    if (stations.isEmpty()) {
        println("No charging stations stored yet.")
    } else {
        stations.forEach { station ->
            println()
            println("ID: ${station.stationId}")
            println("Name: ${station.name}")
            println("Address: ${station.address}")
            println("Opening Hours: ${station.openingHours}")
            station.chargingPoints.forEach { point ->
                println("Status: ${point.status}")
                println("Power: ${point.powerOutput} kW")
                println("Price: €${point.pricePerKwh} per kWh")
                println("Connector: ${point.connector?.connectorType}")
            }
            println("------------------------------------------")
        }
    }
}

fun searchStation() {

    println("\n--- Search Charging Station ---")

    print("Enter Station ID: ")

    val id = readlnOrNull()?.toLongOrNull()

    if (id != null) {

        val station = store.findOne(id)
        if (station != null) {
            println()
            println("Station Found:")
            println("ID: ${station.stationId}")
            println("Name: ${station.name}")
            println("Address: ${station.address}")
            println("Opening Hours: ${station.openingHours}")
            station.chargingPoints.forEach { point ->
                println("Connector: ${point.connector?.connectorType}")
                println("Power: ${point.powerOutput} kW")
                println("Price: €${point.pricePerKwh} per kWh")
                println("Status: ${point.status}")
            }
        } else {
            println("No charging station found with ID $id.")
        }
    } else {
        println("Invalid ID entered.")
    }
}
fun deleteStation() {
    println("\n--- Delete Charging Station ---")
    listStations()
    if (store.findAll().isEmpty()) {
        return }
    print("\nEnter ID of Charging Station to delete: ")
    val id = readlnOrNull()?.toLongOrNull()
    if (id != null) {
        val deleted = store.delete(id)
        if (deleted) {
            println("Charging station with ID $id deleted successfully.")
        } else {
            println("Charging station with ID $id not found.")
        }
    } else {
        println("Invalid ID entered.")
    }
}
fun updateStation() {

    println("\n--- Update Charging Station ---")

    listStations()

    if (store.findAll().isEmpty()) {
        return
    }
    print("\nEnter ID of Charging Station to update: ")
    val id = readlnOrNull()?.toLongOrNull()
    if (id != null) {
        val station = store.findOne(id)
        if (station != null) {
            print("Enter New Station Name: ")
            val name = readlnOrNull()?.trim().orEmpty()
            print("Enter New Address: ")
            val address = readlnOrNull()?.trim().orEmpty()
            print("Enter New Opening Hours: ")
            val openingHours = readlnOrNull()?.trim().orEmpty()
            if (name.isNotEmpty()) {
                val updatedStation = ChargingStation(
                    stationId = id,
                    name = name,
                    address = address,
                    openingHours = openingHours,
                    chargingPoints = station.chargingPoints
                )
                val updated = store.update(updatedStation)
                if (updated) {
                    println("Charging station updated successfully.")
                } else {
                    println("Charging station update failed.")
                }
            } else {
                println("Station name cannot be empty. Update cancelled.")
            }
        } else {
            println("Charging station with ID $id not found.")
        }
    } else {
        println("Invalid ID entered.")
    }
}
fun filterByConnector() {
    println("\n--- Filter by Connector Type ---")
    print("Enter Connector Type (CCS, CHAdeMO, Type 2): ")
    val connectorType = readlnOrNull()?.trim().orEmpty()
    val results = store.findByConnectorType(connectorType)
    if (results.isEmpty()) {
        println("No charging stations found with connector type: $connectorType")
    } else {
        println()
        println("Charging stations supporting $connectorType:")
        results.forEach { station ->
            println(
                "ID: ${station.stationId} | " +
                        "Name: ${station.name} | " +
                        "Address: ${station.address}"
            )
        }
    }
}