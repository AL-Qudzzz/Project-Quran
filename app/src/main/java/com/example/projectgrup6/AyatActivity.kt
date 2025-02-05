package com.example.projectgrup6

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.projectgrup6.adapter.AyatAdapter
import com.example.projectgrup6.model.Ayat
import com.example.projectgrup6.model.Surah2
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import kotlin.concurrent.thread

class AyatActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var ayatAdapter: AyatAdapter
    private lateinit var surah: Surah2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.ayat_activity)

        recyclerView = findViewById(R.id.recyclerViewAyat)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Mendapatkan nomor surah dari Intent
        val surahNumber = intent.getIntExtra("SURAH_NUMBER", 1)

        // Memanggil API untuk mengambil data ayat dan surah
        fetchAyatData(surahNumber)
    }

    private fun fetchAyatData(surahNumber: Int) {
        thread {
            try {
                val url = URL("https://quran-api.santrikoding.com/api/surah/$surahNumber")
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"

                val response = connection.inputStream.bufferedReader().use { it.readText() }
                val jsonObject = JSONObject(response)

                // Mengambil data surah
                // Deklarasi List<Ayat>
                surah = Surah2(
                    nama = jsonObject.getString("nama"),
                    namaLatin = jsonObject.getString("nama_latin"),
                    jumlahAyat = jsonObject.getInt("jumlah_ayat"),
                    tempatTurun = jsonObject.getString("tempat_turun"),
                    arti = jsonObject.getString("arti"),
                    deskripsi = jsonObject.getString("deskripsi"),
                    audio = jsonObject.getString("audio"),
                    ayat = mutableListOf<Ayat>() // Perbaikan tipe
                )


                val ayatArray = jsonObject.getJSONArray("ayat")

                for (i in 0 until ayatArray.length()) {
                    val jsonAyat = ayatArray.getJSONObject(i)
                    val ayat = Ayat(
                        id = jsonAyat.getInt("id"),
                        surah = jsonAyat.getInt("surah"),
                        nomor = jsonAyat.getInt("nomor"),
                        ar = jsonAyat.getString("ar"),
                        tr = jsonAyat.getString("tr"),
                        idn = jsonAyat.getString("idn")
                    )
                    surah.ayat.add(ayat)
                }

                // Update UI di thread utama
                runOnUiThread {
                    ayatAdapter = AyatAdapter(surah.ayat) { audioUrl ->
                        // Logic untuk memutar audio ketika tombol play di klik
                        Toast.makeText(this, "Memutar audio: $audioUrl", Toast.LENGTH_SHORT).show()
                    }
                    recyclerView.adapter = ayatAdapter
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
