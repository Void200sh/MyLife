# MyLife

Aplikasi manajer kehidupan pribadi — **native Kotlin + Jetpack Compose**, offline, tanpa akun. Data tersimpan lokal di HP (SharedPreferences + JSON).

## Fitur

- **Beranda** — kebiasaan harian, tugas prioritas, ringkasan olahraga & fokus.
- **Belajar** — timer fokus 25 menit, ambil tugas saat timer berjalan, check-out sesi, statistik fokus, mata pelajaran.
- **Tugas** — prioritas, kategori, tenggat, selesai/buka dengan konfirmasi.
- **Uang** — pemasukan, pengeluaran, budget bulanan.
- **Progres** — mood, target olahraga (hari/minggu & menit/sesi), grafik 7 hari, target jangka panjang, riwayat aktivitas.
- **5 tema** di Pengaturan/Tema: Soft Sage (default), Paper, Porcelain, Chrome, Tactical.

Data mulai kosong dan riwayat dibatasi 200 entri terakhir.

## Build

Build otomatis lewat **GitHub Actions** setiap push ke `main` (lihat `.github/workflows/build.yml`), menghasilkan APK debug yang ditempel ke release `apk-<run_number>`.

Lokal (opsional):

```bash
gradle assembleDebug
```

Butuh JDK 17 + Android SDK 34. Aplikasi tidak memakai WebView.
