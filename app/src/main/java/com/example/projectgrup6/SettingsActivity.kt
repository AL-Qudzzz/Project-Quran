package com.example.projectgrup6

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AlertDialog

class SettingsActivity : AppCompatActivity() {
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("QuranSettings", MODE_PRIVATE)

        // Back button
        val btnBack: ImageView = findViewById(R.id.btn_back)
        btnBack.setOnClickListener {
            finish()
        }

        // Arabic Settings
        setupArabicSettings()

        // Latin Settings
        setupLatinSettings()

        // Translation Settings
        setupTranslationSettings()
    }

    private fun setupArabicSettings() {
        // Types of Arabic Writing
        findViewById<LinearLayout>(R.id.arabic_types).setOnClickListener {
            showArabicTypesDialog()
        }

        // Arabic Font Size
        findViewById<LinearLayout>(R.id.arabic_font_size).setOnClickListener {
            showFontSizeDialog("arabic_font_size", "Arabic Font Size")
        }

        // Colored Tajwid
        findViewById<LinearLayout>(R.id.colored_tajwid).setOnClickListener {
            toggleColoredTajwid()
        }
    }

    private fun setupLatinSettings() {
        // Enable Latin
        findViewById<LinearLayout>(R.id.enable_latin).setOnClickListener {
            toggleLatin()
        }

        // Latin Font Size
        findViewById<LinearLayout>(R.id.latin_font_size).setOnClickListener {
            showFontSizeDialog("latin_font_size", "Latin Font Size")
        }
    }

    private fun setupTranslationSettings() {
        // Enable Translation
        findViewById<LinearLayout>(R.id.enable_translation).setOnClickListener {
            toggleTranslation()
        }

        // Translation Font Size
        findViewById<LinearLayout>(R.id.translation_font_size).setOnClickListener {
            showFontSizeDialog("translation_font_size", "Translation Font Size")
        }

        // Translator
        findViewById<LinearLayout>(R.id.translator).setOnClickListener {
            showTranslatorDialog()
        }
    }

    private fun showArabicTypesDialog() {
        val types = arrayOf("Uthmani", "Indopak", "Simple")
        val currentType = sharedPreferences.getString("arabic_type", "Uthmani") ?: "Uthmani"

        AlertDialog.Builder(this)
            .setTitle("Types of Arabic Writing")
            .setSingleChoiceItems(types, types.indexOf(currentType)) { dialog, which ->
                sharedPreferences.edit().putString("arabic_type", types[which]).apply()
                dialog.dismiss()
                Toast.makeText(this, "Arabic type set to ${types[which]}", Toast.LENGTH_SHORT).show()
            }
            .show()
    }

    private fun showFontSizeDialog(prefKey: String, title: String) {
        val sizes = arrayOf("Small", "Medium", "Large")
        val currentSize = sharedPreferences.getString(prefKey, "Medium") ?: "Medium"

        AlertDialog.Builder(this)
            .setTitle(title)
            .setSingleChoiceItems(sizes, sizes.indexOf(currentSize)) { dialog, which ->
                sharedPreferences.edit().putString(prefKey, sizes[which]).apply()
                dialog.dismiss()
                Toast.makeText(this, "Font size set to ${sizes[which]}", Toast.LENGTH_SHORT).show()
            }
            .show()
    }

    private fun toggleColoredTajwid() {
        val currentState = sharedPreferences.getBoolean("colored_tajwid", false)
        sharedPreferences.edit().putBoolean("colored_tajwid", !currentState).apply()
        Toast.makeText(this, "Colored Tajwid ${if (!currentState) "enabled" else "disabled"}", Toast.LENGTH_SHORT).show()
    }

    private fun toggleLatin() {
        val currentState = sharedPreferences.getBoolean("enable_latin", true)
        sharedPreferences.edit().putBoolean("enable_latin", !currentState).apply()
        Toast.makeText(this, "Latin ${if (!currentState) "enabled" else "disabled"}", Toast.LENGTH_SHORT).show()
    }

    private fun toggleTranslation() {
        val currentState = sharedPreferences.getBoolean("enable_translation", true)
        sharedPreferences.edit().putBoolean("enable_translation", !currentState).apply()
        Toast.makeText(this, "Translation ${if (!currentState) "enabled" else "disabled"}", Toast.LENGTH_SHORT).show()
    }

    private fun showTranslatorDialog() {
        val translators = arrayOf("Indonesian", "English", "Malay")
        val currentTranslator = sharedPreferences.getString("translator", "Indonesian") ?: "Indonesian"

        AlertDialog.Builder(this)
            .setTitle("Select Translator")
            .setSingleChoiceItems(translators, translators.indexOf(currentTranslator)) { dialog, which ->
                sharedPreferences.edit().putString("translator", translators[which]).apply()
                dialog.dismiss()
                Toast.makeText(this, "Translator set to ${translators[which]}", Toast.LENGTH_SHORT).show()
            }
            .show()
    }
}