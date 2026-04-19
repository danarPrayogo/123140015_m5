
# Tugas Praktikum M7 - Notes App Upgrade

## Informasi
- Nama: Danar Prayogo
- NIM: 123140015
- Branch Pengumpulan: week-7

## Deskripsi Singkat
Proyek ini adalah upgrade aplikasi Notes berbasis Kotlin Multiplatform (target Android) dengan penyimpanan lokal menggunakan SQLDelight, pengaturan aplikasi menggunakan DataStore, fitur pencarian, serta UI state yang jelas (loading, empty, content).

## Fitur yang Diimplementasikan
1. SQLDelight database untuk menyimpan catatan secara lokal (offline-first).
2. CRUD lengkap: tambah, tampilkan, edit, hapus catatan.
3. Search catatan berdasarkan judul atau isi konten.
4. Settings menggunakan DataStore:
   - Theme (dark mode on/off)
   - Sort order (Terbaru, Terlama, Judul A-Z, Judul Z-A)
5. Toggle favorite pada tiap catatan.
6. UI states yang proper:
   - Loading: indikator progress saat memuat data
   - Empty: pesan saat data catatan kosong
   - Content: daftar catatan saat data tersedia

## Teknologi Utama
- Kotlin Multiplatform + Jetpack Compose (Android)
- SQLDelight
- AndroidX DataStore Preferences
- MVVM (ViewModel + Repository)

## Struktur Database (SQLDelight)
Schema berada di:
- [composeApp/src/commonMain/sqldelight/org/example/project/db/Notes.sq](composeApp/src/commonMain/sqldelight/org/example/project/db/Notes.sq)

```sql
CREATE TABLE notes (
    id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
    title TEXT NOT NULL,
    content TEXT NOT NULL,
    is_favorite INTEGER NOT NULL DEFAULT 0,
    created_at INTEGER NOT NULL
);
```

Query utama yang digunakan:
- selectAll: ambil semua catatan (default urut terbaru)
- searchNotes: cari berdasarkan title/content (LIKE)
- insertNote: tambah catatan baru
- updateNote: ubah judul dan konten
- toggleFavorite: ubah status favorite 0/1
- deleteNote: hapus catatan berdasarkan id

## Penjelasan Arsitektur Singkat
- ViewModel: [composeApp/src/androidMain/kotlin/org/example/project/notes/NotesViewModel.kt](composeApp/src/androidMain/kotlin/org/example/project/notes/NotesViewModel.kt)
  - Mengelola state UI, aksi CRUD, search, dan sinkronisasi settings.
- Repository DB: [composeApp/src/androidMain/kotlin/org/example/project/notes/NotesRepository.kt](composeApp/src/androidMain/kotlin/org/example/project/notes/NotesRepository.kt)
  - Jembatan antara ViewModel dan query SQLDelight.
- Settings Repository: [composeApp/src/androidMain/kotlin/org/example/project/notes/SettingsRepository.kt](composeApp/src/androidMain/kotlin/org/example/project/notes/SettingsRepository.kt)
  - Menyimpan preference dark mode dan sort order ke DataStore.
- UI Screen: [composeApp/src/androidMain/kotlin/org/example/project/App.kt](composeApp/src/androidMain/kotlin/org/example/project/App.kt)
  - Menampilkan Notes screen, Settings screen, dialog tambah/edit, serta state loading-empty-content.

## Cara Menjalankan
Masuk ke root project [123140015_M7](.) lalu jalankan:

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

## Checklist Pengujian Fitur
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

## Bukti Demo
Silakan tambahkan:
- Screenshot semua screen utama
- Link video demo 45 detik (CRUD, search, settings, offline mode)