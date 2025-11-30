# Izin Lapor - Sistem Pengaduan Masyarakat

Aplikasi desktop berbasis JavaFX yang dirancang untuk memudahkan masyarakat dalam menyampaikan pengaduan atau laporan terkait infrastruktur, pelayanan publik, dan keamanan lingkungan kepada pihak berwenang.

## Anggota Kelompok
1. Kevin Ananda Putra - L0124103
2. Muhammad Alfin Hasan - L0124105
3. Rafif Adyatma Setyawan - L0124117

## Fitur Aplikasi

### Autentikasi
*   **Login**
*   **Registrasi**

### Admin
*   **Dashboard**
*   **Kelola Pengaduan**
*   **Update Status**
*   **Diskusi/Chat**
*   **Kelola User**
*   **Ekspor Data**
*   **Reset Database**

### Warga (User)
*   **Buat Pengaduan**
*   **Riwayat Laporan**
*   **Diskusi/Chat**
*   **Edit Profil**
*   **Hapus Laporan**

## Cara Penggunaan

### Prasyarat
*   Java JDK 21+.
*   Maven.
*   XAMPP (untuk database MySQL). Link panduan instalasi:
*   https://www.canva.com/design/DAGxXf1wA4s/egDD3Obw-Zxaf7pXaPtMiw/edit?utm_content=DAGxXf1wA4s&utm_campaign=designshare&utm_medium=link2&utm_source=sharebutton
*   Git.

### Langkah-langkah Instalasi

1.  **Clone Repository**
    ```bash
    git clone https://github.com/KevinAnandaP/izin-lapor.git
    cd izin-lapor/izinlapor
    ```

2.  **Persiapan Database**
    *   Buka **XAMPP Control Panel**.
    *   Klik tombol **Start** pada **Apache** dan **MySQL**.
    *   Aplikasi akan secara otomatis membuat database `izin-lapor` dan tabel yang diperlukan saat pertama kali dijalankan.

3.  **Menjalankan Aplikasi**
    Jalankan perintah berikut di dalam folder proyek:
    ```bash
    mvn clean compile
    mvn javafx:run
    ```

4.  **Login**
    *   **Admin Default:**
        *   Username: `admin`
        *   Password: `admin123`
    *   **Warga:** Registrasi akun baru.