package com.example.projectgrup6

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)

        // Initialize the Get Started button
        val getStartedButton: Button = findViewById(R.id.getStartedButton)

        // Set click listener for the Get Started button
        getStartedButton.setOnClickListener {
            // When the button is clicked, navigate to MenuActivity
            val intent = Intent(this, MenuActivity::class.java)
            startActivity(intent)
            finish()  // Optionally finish the MainActivity to remove it from the back stack
        }
    }
}
