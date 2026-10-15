package org.setu.electriccar

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import org.setu.electriccar.models.Vehicle

class VehicleAdapter(
    private val vehicles: List<Vehicle>,
    private val onClick: (Vehicle) -> Unit,
    private val onLongClick: (Vehicle) -> Unit
) : RecyclerView.Adapter<VehicleAdapter.MainHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_vehicle, parent, false)
        return MainHolder(view)
    }

    override fun onBindViewHolder(holder: MainHolder, position: Int) {
        val vehicle = vehicles[position]
        holder.bind(vehicle)

        holder.itemView.setOnClickListener {
            onClick(vehicle)
        }

        holder.itemView.setOnLongClickListener {
            onLongClick(vehicle)
            true
        }
    }

    override fun getItemCount(): Int {
        return vehicles.size
    }
    class MainHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val titleText: TextView = view.findViewById(R.id.titleText)
        private val batteryText: TextView = view.findViewById(R.id.batteryText)
        private val connectorText: TextView = view.findViewById(R.id.connectorText)

        fun bind(vehicle: Vehicle) {
            titleText.text = "${vehicle.make} ${vehicle.model}"
            batteryText.text = "Battery: ${vehicle.batteryCapacity} kWh – Range: ~${vehicle.getRange()} km"
            connectorText.text = "Connector: ${vehicle.connectorType}"
        }
    }
}