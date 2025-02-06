package com.example.projectgrup6

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.projectgrup6.adapter.SurahAdapter
import com.example.projectgrup6.model.Surah
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import kotlin.concurrent.thread
import android.util.Log

class ReadQuranActivity : AppCompatActivity() {

    private lateinit var surahRecyclerView: RecyclerView
    private val surahList = mutableListOf<Surah>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.read_quran_activity)

        surahRecyclerView = findViewById(R.id.surahRecyclerView)
        surahRecyclerView.layoutManager = LinearLayoutManager(this)

        // Update informasi Last Read di UI
        updateLastReadUI()

        // Tangani klik pada card Last Read
        handleLastReadClick()

        // Ambil data Surah
        fetchSurahData()
    }

    private fun updateLastReadUI() {
        val sharedPreferences = getSharedPreferences("MyQuranPreferences", MODE_PRIVATE)
        val lastSurahName = sharedPreferences.getString("LAST_SURAH_NAME", null)
        val lastSurahAyah = sharedPreferences.getInt("LAST_SURAH_AYAH", -1)

        val lastReadSurahTextView = findViewById<TextView>(R.id.lastReadSurah)

        // Tampilkan data Last Read jika valid
        if (lastSurahName != null && lastSurahAyah != -1) {
            lastReadSurahTextView.text = "$lastSurahName: Ayat $lastSurahAyah"
        } else {
            lastReadSurahTextView.text = "Belum ada data terakhir dibaca"
        }
    }

    private fun handleLastReadClick() {
        val lastReadCard = findViewById<androidx.cardview.widget.CardView>(R.id.lastReadCard)
        lastReadCard.setOnClickListener {
            val sharedPreferences = getSharedPreferences("MyQuranPreferences", MODE_PRIVATE)
            val lastSurahNumber = sharedPreferences.getInt("LAST_SURAH_NUMBER", -1)
            val lastSurahName = sharedPreferences.getString("LAST_SURAH_NAME", null)
            val lastSurahAyah = sharedPreferences.getInt("LAST_SURAH_AYAH", -1)

            if (lastSurahNumber != -1 && lastSurahName != null && lastSurahAyah != -1) {
                val intent = Intent(this, AyatActivity::class.java)
                intent.putExtra("SURAH_NUMBER", lastSurahNumber)
                intent.putExtra("SURAH_NAME", lastSurahName)
                startActivity(intent)
            } else {
                Toast.makeText(this, "Belum ada data terakhir dibaca", Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun fetchSurahData() {
        thread {
            try {
                val url = URL("https://api.alquran.cloud/v1/surah")
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                connection.connectTimeout = 5000
                connection.readTimeout = 5000

                val responseCode = connection.responseCode
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    val response = connection.inputStream.bufferedReader().use { it.readText() }
                    val jsonObject = JSONObject(response)
                    val jsonArray = jsonObject.getJSONArray("data")

                    for (i in 0 until jsonArray.length()) {
                        val surahObj = jsonArray.getJSONObject(i)
                        val surah = Surah(
                            nomor = surahObj.getInt("number"),
                            nama = surahObj.getString("englishName"),
                            arti = surahObj.getString("englishNameTranslation"),
                            ayat = surahObj.getInt("numberOfAyahs"),
                            type = surahObj.getString("revelationType"),
                            audio = "",
                            keterangan = ""
                        )
                        surahList.add(surah)
                    }

                    runOnUiThread {
                        surahRecyclerView.adapter = SurahAdapter(surahList) { surah ->
                            val intent = Intent(this@ReadQuranActivity, AyatActivity::class.java)
                            intent.putExtra("SURAH_NUMBER", surah.nomor)
                            intent.putExtra("SURAH_NAME", surah.nama)
                            startActivity(intent)
                        }
                    }
                } else {
                    runOnUiThread {
                        Toast.makeText(this, "Gagal mengambil data (Error: $responseCode)", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                runOnUiThread {
                    Toast.makeText(this, "Gagal mengambil data, cek koneksi internet", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
