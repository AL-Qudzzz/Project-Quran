package com.example.projectgrup6

import android.Manifest
import android.content.pm.PackageManager
import android.hardware.*
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.view.animation.RotateAnimation
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import kotlin.math.*

class QiblaActivity : AppCompatActivity(), SensorEventListener, LocationListener {

    private lateinit var compassImage: ImageView
    private lateinit var locationInfo: TextView
    private lateinit var sensorManager: SensorManager
    private var accelerometer: Sensor? = null
    private var magnetometer: Sensor? = null
    private var gravity: FloatArray? = null
    private var geomagnetic: FloatArray? = null
    private var currentDegree = 0f

    private lateinit var locationManager: LocationManager
    private var userLat = -6.2 // Default Jakarta
    private var userLng = 106.8

    private val kaabaLat = 21.4225
    private val kaabaLng = 39.8262

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.compas_main)

        compassImage = findViewById(R.id.compassImage)
        locationInfo = findViewById(R.id.locationInfo)
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)

        // Request location
        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1f, this)
            locationInfo.text = "Detecting location..."
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
            locationInfo.text = "Location permission required"
        }

        findViewById<ImageView>(R.id.btn_back).setOnClickListener { finish() }
    }

    override fun onResume() {
        super.onResume()
        accelerometer?.let { sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI) }
        magnetometer?.let { sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI) }
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event == null) return
        when (event.sensor.type) {
            Sensor.TYPE_ACCELEROMETER -> gravity = event.values
            Sensor.TYPE_MAGNETIC_FIELD -> geomagnetic = event.values
        }
        if (gravity != null && geomagnetic != null) {
            val R = FloatArray(9)
            val I = FloatArray(9)
            if (SensorManager.getRotationMatrix(R, I, gravity, geomagnetic)) {
                val orientation = FloatArray(3)
                SensorManager.getOrientation(R, orientation)
                val azimuthInRadians = orientation[0]
                var azimuthInDegrees = Math.toDegrees(azimuthInRadians.toDouble()).toFloat()
                azimuthInDegrees = (azimuthInDegrees + 360) % 360

                // Hitung arah kiblat
                val qiblaDirection = calculateQiblaDirection(userLat, userLng, kaabaLat, kaabaLng)
                val rotationDegree = (qiblaDirection - azimuthInDegrees + 360) % 360

                rotateCompass(rotationDegree)
            }
        }
    }

    private fun rotateCompass(degree: Float) {
        val rotateAnimation = RotateAnimation(
            currentDegree,
            degree,
            RotateAnimation.RELATIVE_TO_SELF, 0.5f,
            RotateAnimation.RELATIVE_TO_SELF, 0.5f
        )
        rotateAnimation.duration = 300
        rotateAnimation.fillAfter = true
        compassImage.startAnimation(rotateAnimation)
        currentDegree = degree
    }

    // Rumus menghitung arah kiblat dari lokasi user
    private fun calculateQiblaDirection(lat: Double, lng: Double, kaabaLat: Double, kaabaLng: Double): Float {
        val userLatRad = Math.toRadians(lat)
        val userLngRad = Math.toRadians(lng)
        val kaabaLatRad = Math.toRadians(kaabaLat)
        val kaabaLngRad = Math.toRadians(kaabaLng)
        val dLng = kaabaLngRad - userLngRad
        val y = sin(dLng)
        val x = cos(userLatRad) * tan(kaabaLatRad) - sin(userLatRad) * cos(dLng)
        val bearing = Math.toDegrees(atan2(y, x))
        return ((bearing + 360) % 360).toFloat()
    }

    // Lokasi user berubah
    override fun onLocationChanged(location: Location) {
        userLat = location.latitude
        userLng = location.longitude
        locationInfo.text = "Location: ${String.format("%.4f", userLat)}, ${String.format("%.4f", userLng)}"
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1f, this)
                    locationInfo.text = "Detecting location..."
                }
            } else {
                locationInfo.text = "Location permission denied"
                Toast.makeText(this, "Location permission is required for accurate Qibla direction", Toast.LENGTH_LONG).show()
            }
        }
    }
}