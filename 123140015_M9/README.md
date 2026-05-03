
# Tugas Praktikum M8 - Notes App dengan Koin Dependency Injection

## Informasi
- Nama: Danar Prayogo
- NIM: 123140015
- Branch Pengumpulan: week-9

## Deskripsi Singkat
Proyek ini adalah upgrade aplikasi Notes M7 yang ditingkatkan dengan Koin Dependency Injection framework untuk mengelola dependencies secara terpusat, DeviceInfo (expect/actual) untuk menampilkan informasi device, NetworkMonitor (expect/actual) untuk memonitor status jaringan secara real-time, dan Gemini API untuk merangkum isi catatan secara otomatis.

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

### Fitur Baru Week 8
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

## Teknologi Utama
- Kotlin Multiplatform + Jetpack Compose (Android)
- **Koin 3.5.6** (Dependency Injection)
- SQLDelight 2.0.2
- AndroidX DataStore Preferences
- Android ConnectivityManager (Network monitoring)
- MVVM (ViewModel + Repository)

## Struktur Project
```
composeApp/
├── src/
│   ├── commonMain/
│   │   └── kotlin/org/example/project/
│   │       ├── di/
│   │       │   └── KoinModule.kt          # Koin dependency definitions
│   │       ├── device/
│   │       │   └── DeviceInfo.kt          # DeviceInfo interface (expect)
│   │       └── network/
│   │           └── NetworkMonitor.kt      # NetworkMonitor interface (expect)
│   ├── androidMain/
│   │   ├── AndroidManifest.xml
│   │   └── kotlin/org/example/project/
│   │       ├── device/
│   │       │   └── DeviceInfo.kt          # Android implementation (actual)
│   │       ├── network/
│   │       │   └── NetworkMonitor.kt      # Android implementation (actual)
│   │       ├── notes/
│   │       │   ├── NotesViewModel.kt      # ViewModel (managed by Koin)
│   │       │   ├── NotesRepository.kt
│   │       │   ├── SettingsRepository.kt
│   │       │   └── NoteModels.kt
│   │       ├── App.kt                     # Main UI composables
│   │       └── MainActivity.kt            # Koin initialization
│   └── commonMain/sqldelight/
│       └── org/example/project/db/Notes.sq
└── build.gradle.kts
```

## Penjelasan Arsitektur

### Koin Module (di/KoinModule.kt)
Menyediakan single instances untuk:
- `DeviceInfo`: Implementasi Android device info
- `NetworkMonitor`: Monitor jaringan menggunakan ConnectivityManager
- `NotesDatabase`: SQLDelight database instance
- `NotesRepository`: Akses database
- `SettingsRepository`: Akses DataStore preferences
- `NotesViewModel`: ViewModel untuk UI

### DeviceInfo Interface
```kotlin
interface DeviceInfo {
    val deviceName: String
    val osVersion: String
    val manufacturer: String
    val model: String
    val appVersion: String
}
```

**Android Implementation** (`AndroidDeviceInfo`):
- Menggunakan `Build` class untuk mendapatkan info hardware
- Menggunakan `PackageManager` untuk app version
- Dijalankan pada runtime saat aplikasi dimulai

### NetworkMonitor Interface
```kotlin
interface NetworkMonitor {
    val isNetworkAvailable: StateFlow<Boolean>
}
```

**Android Implementation** (`AndroidNetworkMonitor`):
- Menggunakan `ConnectivityManager` dengan `NetworkCallback`
- StateFlow untuk observable network status
- Callback otomatis update saat network berubah (connect/disconnect)

### UI Integration
- **MainActivity.kt**: Inisialisasi Koin sebelum UI rendering
- **App.kt**: Menggunakan `koinViewModel()` dan `koinInject()` untuk dependency injection
- **NotesContent**: Menerima callbacks dari ViewModel
- **SettingsContent**: Menampilkan DeviceInfo dan NetworkStatus dengan card UI
- **M7NotesApp**: Menampilkan network status indicator saat offline

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

## Checklist Pengujian Fitur

### Week 7 Features
- [x] Create note
- [x] Read/list notes
- [x] Update note
- [x] Delete note
- [x] Search note
- [x] Toggle favorite
- [x] Ubah theme (DataStore)
- [x] Ubah sort order (DataStore)
- [x] Offline-first (data tetap lokal)
- [x] UI state loading/empty/content

### Week 8 Features
- [x] Koin DI initialization di MainActivity
- [x] DeviceInfo ditampilkan di Settings
  - [x] Device Manufacturer
  - [x] Device Model
  - [x] Device Name
  - [x] OS Version
  - [x] App Version
- [x] NetworkMonitor tracking real-time
- [x] Network status indicator di main screen
- [x] Network status di Settings screen
- [x] Expect/actual pattern untuk DeviceInfo
- [x] Expect/actual pattern untuk NetworkMonitor
- [x] Semua dependencies di-inject via Koin

### Week 9 Features
- [x] Integrasi Gemini API untuk ringkasan catatan
- [x] Loading state saat proses AI berjalan
- [x] Error handling saat key belum ada atau request gagal
- [x] System prompt untuk mengarahkan output ringkasan
- [x] Model Gemini yang kompatibel untuk `generateContent`

## Bukti Demo
Silakan isi bagian berikut saat mengumpulkan tugas:

- Screenshot Settings screen yang menampilkan Device Info dan Network Status.
- Screenshot main screen saat offline yang menampilkan network indicator.
- Screenshot fitur AI summarization saat ringkasan Gemini tampil.
- Link video demo 45 detik.

Isi video demo:
- Tampilkan Device Info di Settings.
- Test Network Monitor dengan menyalakan dan mematikan WiFi.
- Lakukan CRUD notes.
- Tampilkan fitur search dan settings.
- Tampilkan ringkasan AI pada salah satu catatan.