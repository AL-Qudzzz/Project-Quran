package com.example.projectgrup6

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.projectgrup6.adapter.LocationAdapter
import com.example.projectgrup6
.model.Location
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import kotlin.concurrent.thread

class SelectLocationActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private val locationList = mutableListOf<Location>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_location)

        val backButton: ImageView = findViewById(R.id.btnBack)
        backButton.setOnClickListener { finish() }

        recyclerView = findViewById(R.id.recyclerViewLocations)
        recyclerView.layoutManager = LinearLayoutManager(this)

        fetchLocations()
    }

    private fun fetchLocations() {
        thread {
            try {
                val url = URL("https://api.myquran.com/v2/sholat/kota/semua")
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"

                val response = connection.inputStream.bufferedReader().use { it.readText() }
                Log.d("API_RESPONSE", response) // Tambahkan untuk melihat data yang diterima

                // Parsing JSON
                val jsonObject = JSONObject(response)

                // Periksa apakah status = true
                if (!jsonObject.getBoolean("status")) {
                    throw Exception("Status API tidak true")
                }

                val jsonArray = jsonObject.getJSONArray("data") // Ambil array dari properti 'data'

                locationList.clear()
                for (i in 0 until jsonArray.length()) {
                    val jsonObject = jsonArray.getJSONObject(i)
                    val id = jsonObject.getString("id")
                    val lokasi = jsonObject.getString("lokasi")
                    locationList.add(Location(id, lokasi))
                }

                runOnUiThread {
                    val adapter = LocationAdapter(locationList) { location ->
                        onLocationSelected(location)
                    }
                    recyclerView.adapter = adapter
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Log.e("API_ERROR", "Error: ${e.message}")
                runOnUiThread {
                    Toast.makeText(
                        this,
                        "Gagal mengambil data lokasi: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }






    private fun onLocationSelected(location: Location) {
        val intent = Intent()
        intent.putExtra("LOCATION_ID", location.id)
        intent.putExtra("LOCATION_NAME", location.lokasi)
        setResult(RESULT_OK, intent)
        finish()
    }
}
