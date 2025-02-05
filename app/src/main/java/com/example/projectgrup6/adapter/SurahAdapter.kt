package com.example.projectgrup6.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.projectgrup6.R
import com.example.projectgrup6.model.Surah

class SurahAdapter(
    private val surahList: List<Surah>,
    private val onClick: (Surah) -> Unit
) : RecyclerView.Adapter<SurahAdapter.SurahViewHolder>() {

    inner class SurahViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val surahNumber: TextView = view.findViewById(R.id.surahNumber)
        val surahName: TextView = view.findViewById(R.id.surahName)
        val surahDetails: TextView = view.findViewById(R.id.surahDetails)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SurahViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_surah, parent, false)
        return SurahViewHolder(view)
    }

    override fun onBindViewHolder(holder: SurahViewHolder, position: Int) {
        val surah = surahList[position]
        holder.surahNumber.text = surah.nomor.toString()
        holder.surahName.text = surah.nama
        holder.surahDetails.text = "${surah.type} - ${surah.ayat} verses"

        holder.itemView.setOnClickListener { onClick(surah) }
    }

    override fun getItemCount() = surahList.size
}