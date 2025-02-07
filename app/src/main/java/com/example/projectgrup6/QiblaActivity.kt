package com.example.projectgrup6

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.view.animation.RotateAnimation
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class QiblaActivity : AppCompatActivity(), SensorEventListener {

    private lateinit var compassImage: ImageView
    private lateinit var sensorManager: SensorManager
    private var accelerometer: Sensor? = null
    private var magnetometer: Sensor? = null
    private var gravity: FloatArray? = null
    private var geomagnetic: FloatArray? = null
    private var currentDegree = 0f
    private val qiblaDirection = 295.0f // Arah kiblat (tergantung lokasi)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.compas_main)

        // Tombol kembali
        val backButton: ImageView = findViewById(R.id.btn_back)
        backButton.setOnClickListener {
            // Kembali ke aktivitas sebelumnya
            finish()
        }

        // Inisialisasi tampilan kompas
        compassImage = findViewById(R.id.compassImage)

        // Inisialisasi SensorManager
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)
    }

    override fun onResume() {
        super.onResume()
        // Daftarkan sensor listener
        accelerometer?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
        }
        magnetometer?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
        }
    }

    override fun onPause() {
        super.onPause()
        // Hentikan sensor listener saat aktivitas dijeda
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

                // Hitung rotasi terhadap arah kiblat
                val rotationDegree = qiblaDirection - azimuthInDegrees
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
        rotateAnimation.duration = 500
        rotateAnimation.fillAfter = true
        compassImage.startAnimation(rotateAnimation)
        currentDegree = degree
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Tidak digunakan
    }
}
