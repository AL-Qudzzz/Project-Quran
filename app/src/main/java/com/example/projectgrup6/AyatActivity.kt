package com.example.projectgrup6

import android.os.Bundle
import android.widget.TextView
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
import android.widget.ImageView

class AyatActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var ayatAdapter: AyatAdapter
    private lateinit var surah: Surah2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.ayat_activity)

        recyclerView = findViewById(R.id.recyclerViewAyat)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Tombol kembali
        val backButton: ImageView = findViewById(R.id.btn_back)
        backButton.setOnClickListener {
            // Kembali ke aktivitas sebelumnya
            finish()
        }

        // Mendapatkan nomor surah dari Intent
        val surahNumber = intent.getIntExtra("SURAH_NUMBER", 1)
        val surahName = intent.getStringExtra("SURAH_NAME") ?: "Al-Fatihah"

        // Ambil TextView berdasarkan ID
        val judulTextView: TextView = findViewById(R.id.judul)
        // Setel nama surah ke TextView
        judulTextView.text = surahName

        // Memanggil API untuk mengambil data ayat dan surah
        fetchAyatData(surahNumber, surahName)
    }


    private fun fetchAyatData(surahNumber: Int, surahName: String) {
        thread {
            try {
                val url = URL("https://quran-api.santrikoding.com/api/surah/$surahNumber")
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"

                val response = connection.inputStream.bufferedReader().use { it.readText() }
                val jsonObject = JSONObject(response)

                // Mengambil data surah
                surah = Surah2(
                    nama = jsonObject.getString("nama"),
                    namaLatin = jsonObject.getString("nama_latin"),
                    jumlahAyat = jsonObject.getInt("jumlah_ayat"),
                    tempatTurun = jsonObject.getString("tempat_turun"),
                    arti = jsonObject.getString("arti"),
                    deskripsi = jsonObject.getString("deskripsi"),
                    audio = jsonObject.getString("audio"),
                    ayat = mutableListOf()
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
                    ayatAdapter = AyatAdapter(surah.ayat) { audioUrl, ayatNumber ->
                        // Logic untuk memutar audio ketika tombol play di klik
                        Toast.makeText(this, "Memutar audio: $audioUrl", Toast.LENGTH_SHORT).show()

                        // Update Last Read setiap ayat diputar
                        updateLastRead(surahNumber, surah.namaLatin, ayatNumber)
                    }
                    recyclerView.adapter = ayatAdapter
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // Simpan surah dan ayat terakhir yang dibaca
    private fun updateLastRead(surahNumber: Int, surahName: String, ayahNumber: Int) {
        val sharedPreferences = getSharedPreferences("MyQuranPreferences", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putInt("LAST_SURAH_NUMBER", surahNumber)
        editor.putString("LAST_SURAH_NAME", surahName)
        editor.putInt("LAST_SURAH_AYAH", ayahNumber)
        editor.apply()

        Toast.makeText(this, "Last Read updated: $surahName Ayat $ayahNumber", Toast.LENGTH_SHORT).show()
    }
}
