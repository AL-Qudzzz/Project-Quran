package com.example.projectgrup6.model

data class Ayat(
    val id: Int,
    val surah: Int,
    val nomor: Int,
    val ar: String, // Ayat dalam bahasa Arab
    val tr: String, // Transliterasi
    val idn: String // Terjemahan dalam Bahasa Indonesia
)
