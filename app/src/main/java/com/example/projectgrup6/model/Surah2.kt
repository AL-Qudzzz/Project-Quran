package com.example.projectgrup6.model

data class Surah2(
    val nama: String,
    val namaLatin: String,
    val jumlahAyat: Int,
    val tempatTurun: String,
    val arti: String,
    val deskripsi: String,
    val audio: String,
    val ayat: MutableList<Ayat> // Ensure this is MutableList
)
