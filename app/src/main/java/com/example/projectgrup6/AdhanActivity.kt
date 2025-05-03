package com.example.projectgrup6

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.widget.ArrayAdapter
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.projectgrup6.databinding.AdhanActivityBinding
import com.example.projectgrup6.databinding.DialogCitySelectionBinding
import okhttp3.*
import org.json.JSONObject
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class AdhanActivity : AppCompatActivity() {

    private lateinit var binding: AdhanActivityBinding
    private lateinit var adhanAdapter: AdhanAdapter
    private val handler = Handler(Looper.getMainLooper())
    private val client = OkHttpClient()
    private var cityId: String? = null
    private var cityList: List<City> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = AdhanActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        setupTimeUpdater()
        loadCityList()

        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.location.setOnClickListener {
            showCitySelectionDialog()
        }
    }

    private fun setupRecyclerView() {
        adhanAdapter = AdhanAdapter()
        binding.adhanRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@AdhanActivity)
            adapter = adhanAdapter
        }
    }

    private fun setupTimeUpdater() {
        val runnable = object : Runnable {
            override fun run() {
                updateDateTime()
                handler.postDelayed(this, 60000)
            }
        }
        handler.post(runnable)
    }

    private fun updateDateTime() {
        val gregorianFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        val today = Date()
        binding.dateGregorian.text = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()).format(today)

        val dateStr = gregorianFormat.format(today)
        val url = "https://api.aladhan.com/v1/gToH?date=$dateStr"
        val request = Request.Builder().url(url).build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    binding.dateHijri.text = "Hijri: Error"
                }
            }
            override fun onResponse(call: Call, response: Response) {
                val body = response.body?.string()
                try {
                    val json = JSONObject(body)
                    val hijri = json.getJSONObject("data").getJSONObject("hijri")
                    val day = hijri.getString("day")
                    val month = hijri.getJSONObject("month").getString("en")
                    val year = hijri.getString("year")
                    val hijriText = "$day $month $year H"
                    runOnUiThread {
                        binding.dateHijri.text = hijriText
                    }
                } catch (e: Exception) {
                    runOnUiThread {
                        binding.dateHijri.text = "Hijri: Error"
                    }
                }
            }
        })
    }

    private fun loadCityList() {
        // Check if the JSON file exists in internal storage
        val file = File(filesDir, "cities.json")
        if (file.exists()) {
            // Read the JSON file from internal storage
            val jsonString = file.readText()
            parseCityList(jsonString)
        } else {
            // Fetch from API and save to file
            fetchCityList()
        }
    }

    private fun fetchCityList() {
        val request = Request.Builder()
            .url("https://api.myquran.com/v2/sholat/kota/semua")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    binding.location.text = "Gagal mengambil daftar kota: ${e.message}"
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val responseData = response.body?.string()
                if (responseData != null) {
                    // Save the JSON response to a file in internal storage
                    val file = File(filesDir, "cities.json")
                    file.writeText(responseData)

                    // Parse the JSON data
                    parseCityList(responseData)
                }
            }
        })
    }

    private fun parseCityList(jsonString: String) {
        val jsonObject = JSONObject(jsonString)
        if (jsonObject.getBoolean("status")) {
            val cityArray = jsonObject.getJSONArray("data")
            val cities = mutableListOf<City>()
            for (i in 0 until cityArray.length()) {
                val cityJson = cityArray.getJSONObject(i)
                cities.add(
                    City(
                        id = cityJson.getString("id"),
                        name = cityJson.getString("lokasi")
                    )
                )
            }
            cityList = cities
            runOnUiThread {
                binding.location.text = "Pilih lokasi"
            }
        } else {
            runOnUiThread {
                binding.location.text = "Gagal mengambil daftar kota"
            }
        }
    }

    private fun showCitySelectionDialog() {
        if (cityList.isEmpty()) {
            binding.location.text = "Daftar kota belum tersedia"
            return
        }

        val dialogBinding = DialogCitySelectionBinding.inflate(LayoutInflater.from(this))
        val dialog = AlertDialog.Builder(this)
            .setTitle("Pilih Kota")
            .setView(dialogBinding.root)
            .setNegativeButton("Batal") { dialog, _ -> dialog.dismiss() }
            .create()

        // Populate the Spinner with city names
        val cityNames = cityList.map { it.name }
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, cityNames)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        dialogBinding.citySpinner.adapter = adapter

        // Set a listener for the "Pilih" button
        dialogBinding.btnSelectCity.setOnClickListener {
            val selectedPosition = dialogBinding.citySpinner.selectedItemPosition
            val selectedCity = cityList[selectedPosition]
            cityId = selectedCity.id
            binding.location.text = selectedCity.name
            fetchPrayerTimes(cityId!!)
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun fetchPrayerTimes(cityId: String) {
        val currentDate = Calendar.getInstance()
        val year = currentDate.get(Calendar.YEAR)
        val month = currentDate.get(Calendar.MONTH) + 1
        val day = currentDate.get(Calendar.DAY_OF_MONTH)

        val url = "https://api.myquran.com/v2/sholat/jadwal/$cityId/$year/$month/$day"
        val request = Request.Builder()
            .url(url)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    binding.location.text = "Gagal mengambil jadwal sholat: ${e.message}"
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val responseData = response.body?.string()
                if (responseData != null) {
                    val jsonObject = JSONObject(responseData)
                    if (jsonObject.getBoolean("status")) {
                        val data = jsonObject.getJSONObject("data")
                        val jadwal = data.getJSONObject("jadwal")
                        val prayerTimes = mutableListOf<PrayerTime>()

                        prayerTimes.add(PrayerTime("Fajr", convertTimeToDecimal(jadwal.getString("subuh"))))
                        prayerTimes.add(PrayerTime("Dhuhr", convertTimeToDecimal(jadwal.getString("dzuhur"))))
                        prayerTimes.add(PrayerTime("Asr", convertTimeToDecimal(jadwal.getString("ashar"))))
                        prayerTimes.add(PrayerTime("Maghrib", convertTimeToDecimal(jadwal.getString("maghrib"))))
                        prayerTimes.add(PrayerTime("Isha", convertTimeToDecimal(jadwal.getString("isya"))))

                        runOnUiThread {
                            adhanAdapter.updateTimes(prayerTimes)
                            binding.adhanRecyclerView.contentDescription = "Adhan schedule list with ${prayerTimes.size} prayer times"
                        }
                    } else {
                        runOnUiThread {
                            binding.location.text = "Gagal mengambil jadwal sholat"
                        }
                    }
                }
            }
        })
    }

    private fun convertTimeToDecimal(time: String): Double {
        val parts = time.split(":")
        val hours = parts[0].toInt()
        val minutes = parts[1].toInt()
        return hours + minutes / 60.0
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
    }
}

data class City(val id: String, val name: String)
data class PrayerTime(val name: String, val time: Double)