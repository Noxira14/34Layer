# 34Layer Android Application

## 📱 Aplikasi Android untuk Network Testing Suite

**Created By: Ibra Decode & Komodigi Project's**

### 🎯 Fitur Utama

- **Autentikasi**: Login & Register menggunakan Supabase Auth
- **3 Tipe User**: User biasa, Premium, Admin/Developer  
- **Dashboard**: Selamat datang dengan burger menu dan status server
- **Web MultiPorting**: Form input domain, waktu, method untuk testing web
- **IP Troubleshooting**: Diagnosa masalah koneksi IP
- **Proxy Server**: Testing koneksi melalui proxy
- **Admin Panel**: Manage metode secara real-time
- **Modern UI**: Dark/Light theme dengan animasi Lottie
- **Sound Effects**: Efek suara dan getaran

### 🏗️ Struktur Project

```
34Layer_Android/
├── app/
│   ├── build.gradle
│   └── src/main/
│       ├── AndroidManifest.xml
│       ├── java/com/ibracodeko/layerthirtyfour/
│       │   ├── LayerApplication.java
│       │   ├── activities/
│       │   │   ├── SplashActivity.java
│       │   │   ├── LoginActivity.java
│       │   │   ├── RegisterActivity.java
│       │   │   ├── DashboardActivity.java
│       │   │   └── WebMultiPortingActivity.java
│       │   ├── api/
│       │   │   ├── ApiClient.java
│       │   │   ├── ApiService.java
│       │   │   └── SupabaseManager.java
│       │   ├── models/
│       │   │   ├── User.java
│       │   │   ├── ScanRequest.java
│       │   │   ├── ScanResponse.java
│       │   │   └── MethodResponse.java
│       │   └── utils/
│       │       ├── PreferenceManager.java
│       │       ├── ThemeManager.java
│       │       ├── SoundManager.java
│       │       ├── ValidationUtils.java
│       │       └── NetworkUtils.java
│       └── res/
│           ├── layout/
│           │   ├── activity_splash.xml
│           │   ├── activity_login.xml
│           │   ├── activity_dashboard.xml
│           │   └── nav_header.xml
│           ├── menu/
│           │   └── nav_menu.xml
│           └── values/
│               ├── colors.xml
│               ├── strings.xml
│               └── styles.xml
```

### 🔧 Konfigurasi

#### Dependencies Utama
- Material Design Components
- Retrofit untuk API calls
- Lottie untuk animasi
- Supabase SDK
- Security Crypto untuk enkripsi

#### API Endpoint
```
https://139.59.29.42/noxira?target={target}&time={time}&methods={methods}
```

#### Supabase Configuration
```
PROJECT_URL: https://xdkwewqepaewfvuobkqy.supabase.co
ANON_PUBLIC: eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

### 🎨 Desain & Theme

- **Modern UI**: Material Design 3
- **Dual Theme**: Light & Dark mode support
- **Animasi**: Lottie animations untuk splash, login, register
- **Sound Effects**: Click, success, error sounds
- **Responsive**: Support multiple screen sizes

### 🔒 Keamanan

- **Encrypted SharedPreferences**: Untuk data sensitif
- **Input Validation**: Validasi form dan input
- **Rate Limiting**: Client-side throttling (1 menit delay)
- **Legal Warning**: Peringatan untuk testing domain sendiri

### 📱 User Types

1. **User**: Akses basic ke semua fitur testing
2. **Premium**: Fitur tambahan dan priority support
3. **Admin/Developer**: Akses ke admin panel dan method management

### 🚀 Instalasi & Setup

1. Buka project di Android Studio
2. Sync project dengan Gradle files
3. Tambahkan file animasi Lottie ke `assets/`
4. Tambahkan file suara ke `res/raw/`
5. Build dan run aplikasi

### 📋 TODO / Fitur yang Perlu Dilengkapi

- [ ] Implementasi lengkap IP Troubleshooting Activity
- [ ] Implementasi Proxy Server Activity  
- [ ] Admin Panel Activity untuk method management
- [ ] Real-time method loading dari server
- [ ] Result display untuk scan responses
- [ ] Push notifications
- [ ] Offline mode support
- [ ] Export hasil scan

### 🎵 Assets Diperlukan

#### Lottie Animations (JSON):
- `login_animation.json` - Untuk halaman login
- `signup_animation.json` - Untuk halaman register  
- `email_success.json` - Untuk success registration
- `dashboard_animation.json` - Untuk dashboard
- `loading_animation.json` - Untuk loading states

#### Sound Effects (RAW):
- `click_sound.wav` - Suara click button
- `success_sound.wav` - Suara sukses
- `error_sound.wav` - Suara error
- `notification_sound.wav` - Suara notifikasi
- `startup_sound.wav` - Suara startup

#### Icons (Vector Drawables):
- Menu dan navigation icons
- Feature icons (web, IP, proxy)
- Theme toggle icons
- User avatars

### 📞 Support

Untuk pertanyaan atau support, hubungi:
- **Ibra Decode**
- **Komodigi Project's**

---

**Copyright © 2024 Ibra Decode & Komodigi Project's**