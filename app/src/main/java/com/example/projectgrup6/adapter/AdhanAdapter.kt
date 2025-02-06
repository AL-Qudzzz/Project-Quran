package com.example.projectgrup6.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.projectgrup6.R
import com.example.projectgrup6.model.Adhan

class AdhanAdapter(private val adhanList: List<Adhan>) :
    RecyclerView.Adapter<AdhanAdapter.AdhanViewHolder>() {

    inner class AdhanViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val prayerName: TextView = view.findViewById(R.id.prayerName)
        val prayerTime: TextView = view.findViewById(R.id.prayerTime)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdhanViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_adhan, parent, false)
        return AdhanViewHolder(view)
    }

    override fun onBindViewHolder(holder: AdhanViewHolder, position: Int) {
        val adhan = adhanList[position]
        holder.prayerName.text = adhan.name
        holder.prayerTime.text = adhan.time
    }

    override fun getItemCount() = adhanList.size
}
