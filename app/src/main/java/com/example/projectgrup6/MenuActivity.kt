package com.example.projectgrup6

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MenuActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.menu_activity)  // Pastikan layout yang benar digunakan

        // Find the ReadButton
        val readButton: Button = findViewById(R.id.ReadButton)
        val adhanButton: Button = findViewById(R.id.AdhanButton) // Tambahkan ini

        // Set OnClickListener for the ReadButton
        readButton.setOnClickListener {
            // Navigasi ke ReadQuranActivity
            val intent = Intent(this, ReadQuranActivity::class.java)
            startActivity(intent)
        }

        // Set OnClickListener for the AdhanButton
        adhanButton.setOnClickListener {
            // Navigasi ke AdhanActivity
            val intent = Intent(this, AdhanActivity::class.java)
            startActivity(intent)
        }
    }
}
