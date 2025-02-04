package com.example.projectgrup6

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import kotlin.concurrent.thread
import com.example.projectgrup6.adapter.SurahAdapter
import com.example.projectgrup6.model.Surah

class ReadQuranActivity : AppCompatActivity() {
    private lateinit var surahRecyclerView: RecyclerView
    private val surahList = mutableListOf<Surah>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.read_quran_activity)

        surahRecyclerView = findViewById(R.id.surahRecyclerView)
        surahRecyclerView.layoutManager = LinearLayoutManager(this)

        fetchSurahData()
    }

    private fun fetchSurahData() {
        thread {
            try {
                val url = URL("http://api.alquran.cloud/v1/surah")
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                val response = connection.inputStream.bufferedReader().use { it.readText() }

                val jsonObject = JSONObject(response)
                val jsonArray = jsonObject.getJSONArray("data")

                for (i in 0 until jsonArray.length()) {
                    val surahObj = jsonArray.getJSONObject(i)
                    val surah = Surah(
                        nomor = surahObj.getInt("number"),
                        nama = surahObj.getString("englishName"),
                        arti = surahObj.getString("englishNameTranslation"),
                        type = surahObj.getString("revelationType"),
                        ayat = surahObj.getInt("numberOfAyahs"),
                        audio = "",  // API ini tidak menyediakan audio secara langsung
                        keterangan = ""
                    )
                    surahList.add(surah)
                }

                runOnUiThread {
                    surahRecyclerView.adapter = SurahAdapter(surahList) { surah ->
                        Toast.makeText(this, "Dipilih: ${surah.nama}", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                runOnUiThread {
                    Toast.makeText(this, "Gagal mengambil data", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
