===================================================================
             APLIKASI SISTEM PARKIR MALL (JAVA SWING)
===================================================================

DIBUAT OLEH:
-------------------------------------------------------------------
Nama    : NOVAL BERAUH
NIM     : 2421052
Prodi   : [BASIS DATA/SISTEM INFORMASI]
-------------------------------------------------------------------

TENTANG APLIKASI:
Aplikasi ini adalah sistem manajemen parkir berbasis desktop (Java)
yang dirancang untuk menangani operasional parkir mall secara
profesional, mulai dari pencatatan masuk, perhitungan tarif otomatis,
manajemen denda, hingga pelaporan keuangan real-time.

FITUR-FITUR UTAMA:

1. SISTEM KEAMANAN & LOGIN (MULTI-ROLE)
   - Membedakan akses antara 'Admin' (Bos) dan 'Petugas' (Kasir).
   - Pencatatan Log Shift (Kapan user login dan logout).

2. DASHBOARD PETUGAS (FRONT-OFFICE)
   - Input Kendaraan Masuk (Mobil & Motor) dengan ID Tiket Otomatis.
   - Hitung Tarif Parkir Otomatis (Tarif progresif per jam).
   - Fitur "Tiket Hilang" (Otomatis menambahkan denda ke total bayar).
   - Visualisasi Statistik Harian (Grafik angka kendaraan & pendapatan).

3. PENCETAKAN & LAPORAN
   - Cetak Struk Pembayaran (Support Printer Thermal & Print to PDF).
   - Struk menampilkan detail: Jam Masuk, Keluar, Durasi, & Nopol.
   - Tabel Laporan Live (Real-time update setiap ada transaksi).
   - Export Laporan ke CSV/Excel (Tersimpan otomatis di Documents).

4. ADMIN PANEL (BACK-OFFICE)
   - Manajemen Petugas (CRUD: Tambah & Hapus User Petugas).
   - Pengaturan Tarif Dinamis (Ubah harga parkir & denda langsung dari aplikasi tanpa coding ulang).

TEKNOLOGI YANG DIGUNAKAN:
- Bahasa Pemrograman : Java (JDK 21)
- GUI Framework      : Java Swing (JFrame)
- Database           : MySQL (XAMPP)
- Driver Database    : MySQL Connector/J
- IDE                : IntelliJ IDEA

CARA INSTALASI & PENGGUNAAN:

1. PERSIAPAN DATABASE
   - Pastikan XAMPP (MySQL) sudah berjalan.
   - Buka browser: http://localhost/phpmyadmin
   - Buat database baru dengan nama: dbparkir
   - Klik menu 'Import', pilih file 'dbparkir.sql' yang ada di folder ini.
   - Klik 'Go' / 'Kirim'.

2. MENJALANKAN APLIKASI
   - Buka Project di IntelliJ IDEA.
   - Jalankan file 'Main.java'.

3. AKUN LOGIN (DEMO)
   Gunakan akun berikut untuk mencoba fitur aplikasi:

   A. ROLE ADMIN (Full Akses: Tarif, User, Laporan)
      - Username : antum
      - Password : [ISI PASSWORD ADMIN KAMU DISINI, MISAL: 123]

   B. ROLE PETUGAS (Akses Kasir & Operasional)
      - Username : budi
      - Password : 123  <-- (Sesuai yang kita insert tadi)

4. LOKASI OUTPUT FILE
   - Laporan CSV (Excel) akan tersimpan otomatis di:
     Documents/OneDrive/belajar/ (atau Documents/belajar/)

