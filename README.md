# MyQuran

MyQuran adalah aplikasi Al-Qur'an digital berbasis Android yang menyediakan berbagai fitur seperti membaca Al-Qur'an, jadwal adzan, arah kiblat, dan pengaturan tampilan.

## 📌 Fitur Utama
1. **Membaca Al-Qur'an**: Menampilkan daftar surah dan ayat dengan terjemahan.
2. **Jadwal Adzan**: Menampilkan waktu sholat berdasarkan lokasi pengguna. ( Sementara satu lokasi dan calendar API nya belum ditambahkan)
3. **Arah Kiblat**: Menunjukkan arah kiblat menggunakan kompas.
4. **Pengaturan**: Mengubah tema, ukuran font, dan jenis tulisan Arab. ( Coming soon )

## 📂 Struktur Proyek
```
app/
├── manifests/
│   └── AndroidManifest.xml
├── kotlin+java/com.example.projectgrup6/
│   ├── adapter/
│   │   ├── AdhanAdapter.kt
│   │   ├── AyatAdapter.kt
│   │   ├── SurahAdapter.kt
│   ├── model/
│   │   ├── Adhan.kt
│   │   ├── Ayat.kt
│   │   ├── Surah.kt
│   │   ├── Surah2.kt
│   ├── AdhanActivity.kt
│   ├── AyatActivity.kt
│   ├── MainActivity.kt
│   ├── MenuActivity.kt
│   ├── QiblaActivity.kt
│   ├── ReadQuranActivity.kt
│   ├── SettingsActivity.kt
```

## ⚙️ Teknologi yang Digunakan
- **Kotlin** - Bahasa utama untuk pengembangan aplikasi Android.
- **RecyclerView** - Menampilkan daftar surat dan ayat.
- **API (npoint.io)** - Sumber data untuk daftar surah dan ayat.

## 🚀 Cara Menjalankan Proyek
1. Clone repository ini ke Android Studio:
   ```sh
   git clone https://github.com/username/MyQuran.git
   ```
2. Buka proyek di **Android Studio**.
3. Pastikan memiliki koneksi internet untuk mengambil data dari API.
4. Jalankan proyek menggunakan emulator atau perangkat fisik.

## 📜 Lisensi
Aplikasi ini dibuat untuk tujuan edukasi dan non-komersial.

---
Dibuat oleh **Tim Project Grup 6** 🎯

