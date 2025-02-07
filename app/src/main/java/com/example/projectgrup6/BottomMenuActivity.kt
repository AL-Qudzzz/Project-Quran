package com.example.projectgrup6

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.) // Ganti dengan layout utama Anda

        // Inisialisasi BottomNavigationView
        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottomNavigation)

        // Set listener untuk item yang dipilih
        bottomNavigationView.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_surah -> {
                    // Berpindah ke aktivitas Surah
                    val intent = Intent(this, ReadQuranActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.nav_adhan -> {
                    // Berpindah ke aktivitas Adhan
                    val intent = Intent(this, AdhanActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.nav_ayat -> {
                    // Berpindah ke aktivitas Ayat
                    val intent = Intent(this, AyatActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.nav_qibla -> {
                    // Berpindah ke aktivitas Qibla
                    val intent = Intent(this, QiblaActivity::class.java)
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }
    }
}
