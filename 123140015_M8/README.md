
# Tugas Praktikum M8 - Notes App dengan Koin Dependency Injection

## Informasi
- Nama: Danar Prayogo
- NIM: 123140015
- Branch Pengumpulan: week-8

## Deskripsi Singkat
Proyek ini adalah upgrade aplikasi Notes M7 yang ditingkatkan dengan Koin Dependency Injection framework untuk mengelola dependencies secara terpusat, DeviceInfo (expect/actual) untuk menampilkan informasi device, dan NetworkMonitor (expect/actual) untuk memonitor status jaringan secara real-time.

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

## Cara Menjalankan
Masuk ke root project [123140015_M8](.) lalu jalankan:

- Build debug APK
```bash
./gradlew :composeApp:assembleDebug
```

- Install ke device Android yang terhubung
```bash
./gradlew :composeApp:installDebug
```
## demo
https://drive.google.com/file/d/1jofODPkYrEzUh-DNaI3s_3NDy_bHe88Z/view?usp=drivesdk

Catatan untuk terminal Windows:
- Jika memakai Bash (Git Bash/WSL), gunakan `./gradlew ...`
- Jika memakai PowerShell/CMD, gunakan `.\gradlew.bat ...`

