package com.example.projectgrup6

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity) // Ganti dengan nama layout Anda jika berbeda

        // Inisialisasi tombol kembali
        val btnBack: ImageView = findViewById(R.id.btn_back)

        // Menambahkan aksi klik pada tombol kembali
        btnBack.setOnClickListener {
            // Mengakhiri aktivitas ini untuk kembali ke aktivitas sebelumnya
            finish()
        }
    }
}
