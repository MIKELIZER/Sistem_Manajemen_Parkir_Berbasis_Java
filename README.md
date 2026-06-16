# 🚗 Aplikasi Sistem Parkir Mall (Java Swing)

---

### 🧑‍💻 Pengembang
- **Nama** : Noval Berauh
- **NIM** : 2421052
- **Prodi** : [MASUKKAN PRODI KAMU, MISAL: SISTEM INFORMASI]

---

## 📝 Tentang Aplikasi
Aplikasi ini adalah sistem manajemen parkir berbasis desktop (Java) yang dirancang untuk menangani operasional parkir mall secara profesional. Sistem ini mencakup pencatatan kendaraan masuk, perhitungan tarif otomatis, manajemen denda (tiket hilang), hingga pelaporan keuangan secara *real-time*.

## 🚀 Fitur-Fitur Utama

### 1. Sistem Keamanan & Login (Multi-Role)
* **Akses Fleksibel**: Membedakan hak akses dan antarmuka antara `Admin` (Back-office/Bos) dan `Petugas` (Front-office/Kasir).
* **Log Shift**: Pencatatan riwayat aktivitas untuk melacak waktu login dan logout setiap pengguna secara akurat.

### 2. Dashboard Petugas (Front-Office)
* **Input Kendaraan**: Mendukung jenis kendaraan Mobil & Motor dengan pembuatan ID Tiket unik secara otomatis.
* **Kalkulasi Otomatis**: Menghitung tarif parkir secara dinamis menggunakan sistem progresif per jam.
* **Fitur Tiket Hilang**: Mengotomatisasi penambahan biaya denda ke dalam total tagihan jika pelanggan kehilangan tiket.
* **Statistik Visual**: Menampilkan grafik performa jumlah kendaraan dan total pendapatan harian secara langsung.

### 3. Pencetakan & Laporan
* **Cetak Struk**: Mendukung pencetakan fisik via *Printer Thermal* atau ekspor digital ke format *Print to PDF*.
* **Informasi Detail**: Struk memuat data lengkap seperti Jam Masuk, Jam Keluar, Durasi, dan Nomor Polisi.
* **Live Report Table**: Tabel laporan yang terus diperbarui secara otomatis (*real-time*) setiap ada transaksi baru.
* **Ekspor Data**: Fitur ekspor laporan keuangan ke format CSV/Excel untuk mempermudah audit eksternal.

### 4. Admin Panel (Back-Office)
* **Manajemen Pengguna**: Fitur CRUD penuh (Create, Read, Update, Delete) untuk mengelola data akun petugas.
* **Konfigurasi Tarif Dinamis**: Memungkinkan Admin mengubah nominal tarif parkir per jam dan denda langsung dari aplikasi tanpa perlu menyentuh atau menulis ulang kode program.

---

## 📸 Tampilan Aplikasi
*(Silakan hapus teks ini dan masukkan file gambar hasil screenshot kamu di bawah ini)*

| Halaman Login | Dashboard Kasir / Petugas |
|---|---|
| ![Login Screen](path_ke_gambar_login.png) | ![Dashboard](path_ke_gambar_dashboard.png) |

| Admin Panel & Pengaturan | Hasil Cetak Struk / Laporan |
|---|---|
| ![Admin Panel](path_ke_gambar_admin.png) | ![Struk Laporan](path_ke_gambar_laporan.png) |

---

## 🛠️ Teknologi yang Digunakan
* **Bahasa Pemrograman** : Java (JDK 21)
* **GUI Framework** : Java Swing (JFrame)
* **Database** : MySQL (XAMPP)
* **Driver Database** : MySQL Connector/J
* **IDE** : IntelliJ IDEA

---

## ⚙️ Cara Instalasi & Penggunaan

### 1. Persiapan Database
1. Pastikan modul **Apache** dan **MySQL** pada XAMPP Control Panel sudah berjalan.
2. Buka browser Anda lalu akses ke: `http://localhost/phpmyadmin`
3. Buat database baru dengan nama: `dbparkir`
4. Pilih database tersebut, masuk ke menu **Import**, lalu pilih file `dbparkir.sql` yang tersedia di dalam folder proyek ini.
5. Klik **Go** / **Kirim** dan tunggu hingga proses selesai.

### 2. Menjalankan Aplikasi
1. Buka folder proyek ini menggunakan **IntelliJ IDEA**.
2. Pastikan file *MySQL Connector/J* sudah ditambahkan ke dalam *Libraries/Dependencies* proyek.
3. Cari file `Main.java`, klik kanan, lalu pilih **Run 'Main.main()'**.

### 3. Akun Login (Demo)
Gunakan akun di bawah ini untuk menguji hak akses masing-masing *role*:

#### A. Role ADMIN (Akses Penuh)
* **Username** : `antum`
* **Password** : `[MASUKKAN PASSWORD ADMIN DI SINI, MISAL: 123]`

#### B. Role PETUGAS (Akses Kasir)
* **Username** : `budi`
* **Password** : `123`

---

## 📁 Lokasi Output File
Hasil ekspor laporan berupa file `.csv` (Excel) akan tersimpan secara otomatis pada direktori:
* `Documents/OneDrive/belajar/` (atau `Documents/belajar/`)
