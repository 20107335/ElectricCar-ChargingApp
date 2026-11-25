package org.setu.electriccar

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

// One row of any list: stations, points, vehicles or bookings
data class Row(val title: String, val subtitle: String, val detail: String)

class ItemAdapter(
    private val rows: List<Row>,
    private val onClick: (Int) -> Unit,
    private val onLongClick: (Int) -> Unit
) : RecyclerView.Adapter<ItemAdapter.MainHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_row, parent, false)
        return MainHolder(view)
    }

    override fun onBindViewHolder(holder: MainHolder, position: Int) {
        holder.bind(rows[position])
        holder.itemView.setOnClickListener {
            onClick(position)
        }
        holder.itemView.setOnLongClickListener {
            onLongClick(position)
            true
        }
    }

    override fun getItemCount(): Int {
        return rows.size
    }

    class MainHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val titleText: TextView = view.findViewById(R.id.titleText)
        private val subtitleText: TextView = view.findViewById(R.id.subtitleText)
        private val detailText: TextView = view.findViewById(R.id.detailText)

        fun bind(row: Row) {
            titleText.text = row.title
            subtitleText.text = row.subtitle
            detailText.text = row.detail
        }
    }
}