
# Tugas Praktikum M9 

## Informasi
- Nama: Danar Prayogo
- NIM: 123140015
- Branch Pengumpulan: week-9

## Fitur Week 9
1. **AI Content Summarization** menggunakan Gemini API.
2. Model yang digunakan adalah `gemini-2.5-flash` melalui endpoint `generateContent`.
3. Tombol ringkas AI di setiap catatan untuk menghasilkan summary otomatis.
4. Loading state dan error handling jika API key belum disiapkan, model tidak tersedia, atau request gagal.
5. Prompt ringkas terstruktur agar hasil ringkasan singkat dan konsisten.

## Fitur yang Diimplementasikan
### Fitur Week 7 (dipertahankan)
1. SQLDelight database untuk menyimpan catatan secara lokal (offline-first).
2. CRUD lengkap: tambah, tampilkan, edit, hapus catatan.
3. Search catatan berdasarkan judul atau isi konten.
4. Settings menggunakan DataStore:
   - Theme (dark mode on/off)
   - Sort order (Terbaru, Terlama, Judul A-Z, Judul Z-A)
5. Toggle favorite pada tiap catatan.
6. UI states yang proper (loading, empty, content).

### Fitur Week 8
1. **Koin Dependency Injection**: Setup lengkap untuk mengelola semua dependencies
2. **DeviceInfo (Multiplatform)**: Interface expect/actual untuk menampilkan:
   - Manufacturer (misal: Samsung, Xiaomi)
   - Device Model (misal: Galaxy S21)
   - Device Name (misal: oriole)
   - OS Version (misal: Android 13 API 33)
   - App Version
3. **NetworkMonitor (Multiplatform)**: Interface expect/actual untuk memonitor:
   - Real-time network availability status
   - ConnectivityManager listener pada Android
4. **UI Enhancements**:
   - Network status indicator di top bar (tampil saat offline)
   - Device Info section di Settings screen
   - Status jaringan di card dengan warna indikator


### Week 9 AI Integration
- **GeminiService**: Mengirim judul dan isi catatan ke Gemini API untuk menghasilkan ringkasan.
- **NotesViewModel**: Menyimpan state AI summary, loading, dan error message.
- **Notes screen**: Menyediakan tombol ringkas pada tiap catatan dan menampilkan hasil ringkasan di kartu khusus.
- **Setup runtime**: API key dibaca dari `local.properties` melalui `BuildConfig.GEMINI_API_KEY`.

## Cara Menjalankan
Masuk ke root project [123140015_M9](.) lalu jalankan:

- Build debug APK
```bash
./gradlew :composeApp:assembleDebug
```

- Install ke device Android yang terhubung
```bash
./gradlew :composeApp:installDebug
```

Catatan untuk terminal Windows:
- Jika memakai Bash (Git Bash/WSL), gunakan `./gradlew ...`
- Jika memakai PowerShell/CMD, gunakan `.\gradlew.bat ...`

## Setup Gemini API
Tambahkan API key Gemini gratis ke file `local.properties` di root project:

```properties
GEMINI_API_KEY=isi_api_key_anda_di_sini
```

Jika key belum diisi, fitur ringkasan AI tetap tampil tetapi akan menampilkan pesan error yang jelas saat dipakai.

Catatan: aplikasi saat ini menggunakan model `gemini-2.5-flash` karena model tersebut mendukung `generateContent` pada API Gemini yang aktif.


## Bukti Demo
<img width="723" height="1600" alt="WhatsApp Image 2026-05-03 at 22 35 11 (1)" src="https://github.com/user-attachments/assets/51b3713f-91a9-4a59-b7fb-fa355bfebb75" />
<img width="723" height="1600" alt="WhatsApp Image 2026-05-03 at 22 35 12" src="https://github.com/user-attachments/assets/a346e27d-7836-4bb5-853e-6ff2a3983690" />
<img width="723" height="1600" alt="WhatsApp Image 2026-05-03 at 22 35 11" src="https://github.com/user-attachments/assets/92f4009f-ad62-424f-80c8-5d85ce47a4e6" />


