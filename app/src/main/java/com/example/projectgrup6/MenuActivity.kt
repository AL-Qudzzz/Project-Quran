package com.example.projectgrup6

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MenuActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.menu_activity)  // Replace with the actual layout for MenuActivity

        // Find the ReadButton
        val readButton: Button = findViewById(R.id.ReadButton)

        // Set OnClickListener for the ReadButton
        readButton.setOnClickListener {
            // Create an Intent to navigate to ReadQuranActivity
            val intent = Intent(this, ReadQuranActivity::class.java)
            startActivity(intent)
        }

    }
}
