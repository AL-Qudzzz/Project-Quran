package com.example.projectgrup6

import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.projectgrup6.adapter.AdhanAdapter
import com.example.projectgrup6.model.Adhan
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*
import kotlin.concurrent.thread

class AdhanActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var locationText: TextView
    private val adhanList = mutableListOf<Adhan>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.adhan_activity)

        recyclerView = findViewById(R.id.adhanRecyclerView)
        locationText = findViewById(R.id.location)

        // Tombol kembali
        val backButton: ImageView = findViewById(R.id.btn_back)
        backButton.setOnClickListener {
            finish()
        }

        recyclerView.layoutManager = LinearLayoutManager(this)

        // Set lokasi tetap
        val cityId = "1108"
        val cityName = "KOTA TANGERANG SELATAN"
        locationText.text = cityName

        // Ambil jadwal adhan untuk lokasi tetap
        fetchAdhanSchedule(cityId)
    }

    private fun fetchAdhanSchedule(cityId: String) {
        thread {
            try {
                val date = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault()).format(Date())
                val url = URL("https://api.myquran.com/v2/sholat/jadwal/$cityId/$date")
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"

                val response = connection.inputStream.bufferedReader().use { it.readText() }
                val jsonObj = JSONObject(response).getJSONObject("data").getJSONObject("jadwal")

                val adhanTimes = listOf(
                    Adhan("Subuh", jsonObj.getString("subuh")),
                    Adhan("Dzuhur", jsonObj.getString("dzuhur")),
                    Adhan("Ashar", jsonObj.getString("ashar")),
                    Adhan("Maghrib", jsonObj.getString("maghrib")),
                    Adhan("Isya", jsonObj.getString("isya"))
                )

                runOnUiThread {
                    adhanList.clear()
                    adhanList.addAll(adhanTimes)
                    recyclerView.adapter = AdhanAdapter(adhanList)
                }
            } catch (e: Exception) {
                Log.e("API_ERROR", "Error fetching adhan schedule: ${e.message}")
                runOnUiThread {
                    Toast.makeText(this, "Gagal mengambil jadwal adhan", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
