package com.example.projectgrup6.adapter

import android.content.SharedPreferences
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.projectgrup6.R
import com.example.projectgrup6.model.Ayat
import android.text.Html
import okhttp3.*
import org.json.JSONArray
import java.io.IOException

class AyatAdapter(
    private val ayatList: List<Ayat>,
    private val onPlayClick: (String, Int) -> Unit,
    private val sharedPreferences: SharedPreferences
) : RecyclerView.Adapter<AyatAdapter.AyatViewHolder>() {

    inner class AyatViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ayatNumber: TextView = view.findViewById(R.id.ayatNumber)
        val ayatText: TextView = view.findViewById(R.id.ayatText)
        val transliterationText: TextView = view.findViewById(R.id.transliterationText)
        val translationText: TextView = view.findViewById(R.id.translationText)
        val btnPlay: ImageView = view.findViewById(R.id.btnPlay)
    }

    private val client = OkHttpClient()
    private val handler = Handler(Looper.getMainLooper())
    private val translationCache = mutableMapOf<String, String>()
    private val malayCache = mutableMapOf<String, String>()

    private fun translate(text: String, targetLang: String, cache: MutableMap<String, String>, callback: (String) -> Unit) {
        if (cache.containsKey(text)) {
            callback(cache[text]!!)
            return
        }
        val url = "https://translate.googleapis.com/translate_a/single?client=gtx&sl=id&tl=$targetLang&dt=t&q=" + java.net.URLEncoder.encode(text, "UTF-8")
        val request = Request.Builder().url(url).build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                handler.post { callback("Translation failed") }
            }
            override fun onResponse(call: Call, response: Response) {
                val body = response.body?.string()
                try {
                    val json = JSONArray(body)
                    val translated = json.getJSONArray(0).getJSONArray(0).getString(0)
                    cache[text] = translated
                    handler.post { callback(translated) }
                } catch (e: Exception) {
                    handler.post { callback("Translation error") }
                }
            }
        })
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AyatViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_ayat, parent, false)
        return AyatViewHolder(view)
    }

    override fun onBindViewHolder(holder: AyatViewHolder, position: Int) {
        val ayat = ayatList[position]
        holder.ayatNumber.text = ayat.nomor.toString()
        
        // Apply Arabic font settings
        val arabicFontSize = when(sharedPreferences.getString("arabic_font_size", "Medium")) {
            "Small" -> 20f
            "Large" -> 28f
            else -> 24f // Medium
        }
        holder.ayatText.textSize = arabicFontSize
        
        // Apply Arabic type
        val arabicType = sharedPreferences.getString("arabic_type", "Uthmani") ?: "Uthmani"
        holder.ayatText.text = ayat.ar
        
        // Apply Latin settings
        val enableLatin = sharedPreferences.getBoolean("enable_latin", true)
        if (enableLatin) {
            val latinFontSize = when(sharedPreferences.getString("latin_font_size", "Medium")) {
                "Small" -> 13f
                "Large" -> 17f
                else -> 15f // Medium
            }
            holder.transliterationText.textSize = latinFontSize
        holder.transliterationText.text = Html.fromHtml(ayat.tr, Html.FROM_HTML_MODE_LEGACY)
            holder.transliterationText.visibility = View.VISIBLE
        } else {
            holder.transliterationText.visibility = View.GONE
        }
        
        // Apply translation settings
        val enableTranslation = sharedPreferences.getBoolean("enable_translation", true)
        if (enableTranslation) {
            val translationFontSize = when(sharedPreferences.getString("translation_font_size", "Medium")) {
                "Small" -> 14f
                "Large" -> 18f
                else -> 16f // Medium
            }
            holder.translationText.textSize = translationFontSize
            
            // Apply translator selection
            val translator = sharedPreferences.getString("translator", "Indonesian") ?: "Indonesian"
            when(translator) {
                "Indonesian" -> {
                    holder.translationText.text = ayat.idn
                }
                "English" -> {
                    holder.translationText.text = "Translating..."
                    translate(ayat.idn, "en", translationCache) { translated ->
                        holder.translationText.text = translated
                    }
                }
                "Malay" -> {
                    holder.translationText.text = "Menerjemahkan..."
                    translate(ayat.idn, "ms", malayCache) { translated ->
                        holder.translationText.text = translated
                    }
                }
                else -> {
        holder.translationText.text = ayat.idn
                }
            }
            holder.translationText.visibility = View.VISIBLE
        } else {
            holder.translationText.visibility = View.GONE
        }

        holder.btnPlay.setOnClickListener {
            onPlayClick(ayat.ar, ayat.nomor)
        }
    }

    override fun getItemCount() = ayatList.size
}

