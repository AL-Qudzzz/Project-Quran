package com.example.projectgrup6.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.projectgrup6.R
import com.example.projectgrup6.model.Ayat
import android.text.Html


class AyatAdapter(
    private val ayatList: List<Ayat>, // Daftar ayat yang akan ditampilkan
    private val onPlayClick: (String) -> Unit // Fungsi untuk memainkan audio surah
) : RecyclerView.Adapter<AyatAdapter.AyatViewHolder>() {

    inner class AyatViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ayatNumber: TextView = view.findViewById(R.id.ayatNumber)
        val ayatText: TextView = view.findViewById(R.id.ayatText)
        val transliterationText: TextView = view.findViewById(R.id.transliterationText)
        val translationText: TextView = view.findViewById(R.id.translationText)
        val btnPlay: ImageView = view.findViewById(R.id.btnPlay)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AyatViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_ayat, parent, false)
        return AyatViewHolder(view)
    }

    override fun onBindViewHolder(holder: AyatViewHolder, position: Int) {
        val ayat = ayatList[position]
        // Nomor ayat
        holder.ayatNumber.text = ayat.nomor.toString()

        // Teks Arab
        holder.ayatText.text = ayat.ar

        // Teks Latin dengan parsing HTML
        holder.transliterationText.text = Html.fromHtml(ayat.tr, Html.FROM_HTML_MODE_LEGACY)

        // Terjemahan
        holder.translationText.text = ayat.idn

    }

    override fun getItemCount() = ayatList.size
}
