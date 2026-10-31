# MyLife

Aplikasi manajer kehidupan pribadi — offline, tanpa akun, semua data tersimpan di HP.

## Fitur

- **Beranda** — kebiasaan harian, tugas prioritas, ringkasan olahraga & mood.
- **Belajar** — timer fokus besar (25 menit), ambil tugas saat timer berjalan, check-out sesi, statistik fokus.
- **Tugas** — prioritas, kategori, tenggat, selesai/buka dengan konfirmasi.
- **Uang** — pemasukan, pengeluaran, dan budget bulanan.
- **Progres** — target kebiasaan, grafik olahraga mingguan, target jangka panjang, riwayat aktivitas, serta pengaturan target olahraga (hari/minggu dan menit/sesi).
- **5 tema** bisa diganti kapan saja di **Pengaturan → Tampilan**: Soft Sage (default), Paper, Porcelain, Chrome, Tactical.

Data mulai kosong; tidak ada data contoh. Riwayat aktivitas disimpan maksimal 200 entri terakhir.

## Build lokal (Termux, tanpa Android Studio)

Butuh: `pkg install openjdk-17 aapt apksigner d8 zipalign`, lalu unduh `android.jar` platform 34 ke `tools/android.jar`.

```bash
bash scripts/build-local.sh
```

Hasil: `dist/MyLife-1.1.apk` (debug-signed, siap install).

## Struktur

- `app/src/main/assets/index.html` — seluruh UI + logika aplikasi.
- `app/src/main/assets/theme.css` — token warna tiap tema.
- `app/src/main/java/com/zyvo/mylife/MainActivity.java` — WebView + jembatan warna status bar.
