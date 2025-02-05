package com.example.projectgrup6

import android.content.Intent
import android.os.Bundle
import android.util.Log
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
                val url = URL("https://api.alquran.cloud/v1/surah") // API untuk mendapatkan daftar surah
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
                        // Set adapter dan tambahkan fungsi klik untuk berpindah ke AyatActivity
                        surahRecyclerView.adapter = SurahAdapter(surahList) { surah ->
                            val intent = Intent(this@ReadQuranActivity, AyatActivity::class.java)
                            intent.putExtra("SURAH_NUMBER", surah.nomor)
                            intent.putExtra("SURAH_NAME", surah.nama)
                            startActivity(intent)
                        }
                    }
                } else {
                    Log.e("API_ERROR", "Response Code: $responseCode")
                    runOnUiThread {
                        Toast.makeText(this, "Gagal mengambil data (Error: $responseCode)", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                Log.e("API_ERROR", "Exception: ${e.message}")
                e.printStackTrace()
                runOnUiThread {
                    Toast.makeText(this, "Gagal mengambil data, cek koneksi internet", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
