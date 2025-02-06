package com.example.projectgrup6.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.projectgrup6.R
import com.example.projectgrup6.model.Location

class LocationAdapter(
    private val locationList: List<Location>,
    private val onItemClick: (Location) -> Unit
) : RecyclerView.Adapter<LocationAdapter.LocationViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocationViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_location, parent, false)
        return LocationViewHolder(view)
    }

    override fun onBindViewHolder(holder: LocationViewHolder, position: Int) {
        val location = locationList[position]
        holder.bind(location)
        holder.itemView.setOnClickListener { onItemClick(location) }
    }

    override fun getItemCount(): Int = locationList.size

    inner class LocationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val locationNameTextView: TextView = itemView.findViewById(R.id.locationName)

        fun bind(location: Location) {
            locationNameTextView.text = location.lokasi
        }
    }
}
