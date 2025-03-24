package com.example.projectgrup6

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.projectgrup6.databinding.ItemAdhanBinding

class AdhanAdapter : RecyclerView.Adapter<AdhanAdapter.ViewHolder>() {

    private var prayerTimes = listOf<PrayerTime>()

    class ViewHolder(private val binding: ItemAdhanBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(prayerTime: PrayerTime) {
            binding.prayerName.text = prayerTime.name

            val hours = prayerTime.time.toInt()
            val minutes = ((prayerTime.time - hours) * 60).toInt()
            binding.prayerTime.text = String.format("%02d:%02d", hours, minutes)

            val iconResId = when (prayerTime.name) {
                "Fajr" -> R.drawable.adhan
                "Dhuhr" -> R.drawable.adhan
                "Asr" -> R.drawable.adhan
                "Maghrib" -> R.drawable.adhan
                "Isha" -> R.drawable.adhan
                else -> R.drawable.adhan
            }
            binding.prayerIcon.setImageResource(iconResId)

            binding.root.contentDescription = "${prayerTime.name} at ${String.format("%02d:%02d", hours, minutes)}"
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemAdhanBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(prayerTimes[position])
    }

    override fun getItemCount(): Int = prayerTimes.size

    fun updateTimes(times: List<PrayerTime>) {
        prayerTimes = times
        notifyDataSetChanged()
    }
}