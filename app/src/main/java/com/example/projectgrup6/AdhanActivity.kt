package com.example.projectgrup6

import android.Manifest
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.projectgrup6.databinding.AdhanActivityBinding
import com.google.android.gms.location.*
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Executor
import java.util.concurrent.Executors
import kotlin.math.*

class AdhanActivity : AppCompatActivity() {

    private lateinit var binding: AdhanActivityBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var adhanAdapter: AdhanAdapter
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var locationCallback: LocationCallback

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = AdhanActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        setupRecyclerView()
        setupTimeUpdater()
        setupLocation()

        binding.btnBack.setOnClickListener {
            finish()
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
                handler.postDelayed(this, 60000) // Update every minute
            }
        }
        handler.post(runnable)
    }

    private fun updateDateTime() {
        // Gregorian Date
        val gregorianFormat = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
        binding.dateGregorian.text = gregorianFormat.format(Date())

        // Hijri Date (Simplified conversion)
        val hijriCalendar = Calendar.getInstance()
        val hijriOffset = 1389 // Approximate offset from Gregorian
        hijriCalendar.add(Calendar.YEAR, -hijriOffset)

        val hijriMonths = arrayOf(
            "Muharram", "Safar", "Rabi'ul Awal", "Rabi'ul Akhir",
            "Jumadil Awal", "Jumadil Akhir", "Rajab", "Sya'ban",
            "Ramadhan", "Syawal", "Dzulqa'dah", "Dzuhijjah"
        )

        val day = hijriCalendar.get(Calendar.DAY_OF_MONTH)
        val month = hijriCalendar.get(Calendar.MONTH)
        val year = hijriCalendar.get(Calendar.YEAR)
        binding.dateHijri.text = "$day ${hijriMonths[month]} ${year}H"
    }

    private fun setupLocation() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                1
            )
            return
        }

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult.lastLocation?.let { location ->
                    val geocoder = Geocoder(this@AdhanActivity, Locale.getDefault())
                    try {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            // API 33 and above: Use the new Geocoder API with callback
                            val executor: Executor = Executors.newSingleThreadExecutor()
                            geocoder.getFromLocation(
                                location.latitude,
                                location.longitude,
                                1,
                                object : Geocoder.GeocodeListener {
                                    override fun onGeocode(addresses: MutableList<Address>) {
                                        if (addresses.isNotEmpty()) {
                                            val address = addresses[0]
                                            runOnUiThread {
                                                binding.location.text = "${address.locality}, ${address.subAdminArea} - ${address.countryName}"
                                                updatePrayerTimes(location.latitude, location.longitude)
                                            }
                                        } else {
                                            runOnUiThread {
                                                binding.location.text = "Location unavailable"
                                            }
                                        }
                                    }

                                    override fun onError(errorMessage: String?) {
                                        runOnUiThread {
                                            binding.location.text = "Location unavailable: ${errorMessage ?: "Unknown error"}"
                                        }
                                    }
                                }
                            )
                        } else {
                            // API 32 and below: Use the deprecated Geocoder API
                            @Suppress("DEPRECATION")
                            val addresses = geocoder.getFromLocation(
                                location.latitude,
                                location.longitude,
                                1
                            )
                            if (!addresses.isNullOrEmpty()) {
                                val address = addresses[0]
                                binding.location.text = "${address.locality}, ${address.subAdminArea} - ${address.countryName}"
                                updatePrayerTimes(location.latitude, location.longitude)
                            } else {
                                binding.location.text = "Location unavailable"
                            }
                        }
                    } catch (e: Exception) {
                        binding.location.text = "Location unavailable: ${e.message}"
                    }
                }
            }
        }

        val locationRequest = LocationRequest.create().apply {
            interval = 60000
            fastestInterval = 5000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                setupLocation()
            } else {
                binding.location.text = "Location permission denied"
            }
        }
    }

    private fun updatePrayerTimes(latitude: Double, longitude: Double) {
        val prayerTimes = calculatePrayerTimes(latitude, longitude)
        adhanAdapter.updateTimes(prayerTimes)
        binding.adhanRecyclerView.contentDescription = "Adhan schedule list with ${prayerTimes.size} prayer times"
    }

    private fun calculatePrayerTimes(latitude: Double, longitude: Double): List<PrayerTime> {
        val calendar = Calendar.getInstance()
        val timezone = TimeZone.getDefault().rawOffset / 3600000.0

        // Simplified prayer time calculation
        val dayOfYear = calendar.get(Calendar.DAY_OF_YEAR)
        val eqt = 12.0 + timezone - longitude / 15.0

        return listOf(
            PrayerTime("Fajr", calculateFajr(latitude, dayOfYear)),
            PrayerTime("Dhuhr", eqt),
            PrayerTime("Asr", calculateAsr(latitude, dayOfYear, eqt)),
            PrayerTime("Maghrib", calculateMaghrib(latitude, dayOfYear)),
            PrayerTime("Isha", calculateIsha(latitude, dayOfYear))
        )
    }

    private fun calculateFajr(latitude: Double, dayOfYear: Int): Double {
        val declination = 23.45 * sin(Math.toRadians(360.0 * (284 + dayOfYear) / 365))
        return 12.0 - acos(-sin(Math.toRadians(15.0)) /
                (cos(Math.toRadians(latitude)) * cos(Math.toRadians(declination)))) / 15.0
    }

    private fun calculateAsr(latitude: Double, dayOfYear: Int, dhuhr: Double): Double {
        val declination = 23.45 * sin(Math.toRadians(360.0 * (284 + dayOfYear) / 365))
        val shadowFactor = 1.0 // Standard Shafi'i method
        return dhuhr + acos(sin(atan(1.0 / shadowFactor)) /
                (cos(Math.toRadians(latitude)) * cos(Math.toRadians(declination)))) / 15.0
    }

    private fun calculateMaghrib(latitude: Double, dayOfYear: Int): Double {
        val declination = 23.45 * sin(Math.toRadians(360.0 * (284 + dayOfYear) / 365))
        return 12.0 + acos(-sin(Math.toRadians(0.833)) /
                (cos(Math.toRadians(latitude)) * cos(Math.toRadians(declination)))) / 15.0
    }

    private fun calculateIsha(latitude: Double, dayOfYear: Int): Double {
        val declination = 23.45 * sin(Math.toRadians(360.0 * (284 + dayOfYear) / 365))
        return 12.0 + acos(-sin(Math.toRadians(17.0)) /
                (cos(Math.toRadians(latitude)) * cos(Math.toRadians(declination)))) / 15.0
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }
}

data class PrayerTime(val name: String, val time: Double)

class AdhanAdapter : RecyclerView.Adapter<AdhanAdapter.ViewHolder>() {
    private var prayerTimes = listOf<PrayerTime>()

    class ViewHolder(itemView: android.view.View) : RecyclerView.ViewHolder(itemView) {
        val name: android.widget.TextView = itemView.findViewById(android.R.id.text1)
        val time: android.widget.TextView = itemView.findViewById(android.R.id.text2)
    }

    override fun onCreateViewHolder(parent: android.view.ViewGroup, viewType: Int): ViewHolder {
        val view = android.view.LayoutInflater.from(parent.context)
            .inflate(android.R.layout.simple_list_item_2, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val prayer = prayerTimes[position]
        holder.name.text = prayer.name

        val hours = prayer.time.toInt()
        val minutes = ((prayer.time - hours) * 60).toInt()
        holder.time.text = String.format("%02d:%02d", hours, minutes)

        // Improve accessibility
        holder.itemView.contentDescription = "${prayer.name} at ${String.format("%02d:%02d", hours, minutes)}"
    }

    override fun getItemCount(): Int = prayerTimes.size

    fun updateTimes(times: List<PrayerTime>) {
        prayerTimes = times
        notifyDataSetChanged()
    }
}