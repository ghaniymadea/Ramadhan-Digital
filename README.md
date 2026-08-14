# Ramadhan Digital

Ramadhan Digital is an Android application built with Kotlin for providing Ramadan-related learning and daily-use features. The app appears to include sections for Qur'an study, prayer guidance, dzikir, user activities, attendance, and a dedicated teacher/administrator view.

## Features

- Splash screen
- User home screen
- Teacher/admin home screen
- Juz Amma access
- Surah detail view
- Prayer reading guide
- Dzikir guide
- User activity menu
- Attendance feature
- Login screen

## Tech Stack

- **Language:** Kotlin
- **Platform:** Android
- **Build System:** Gradle Kotlin DSL
- **UI Approach:** View Binding
- **Networking:** Retrofit, Gson, OkHttp logging interceptor
- **Minimum SDK:** 24
- **Target SDK:** 36

## Project Structure

- `app/` – Android application module
- `app/src/main/` – Main application source, resources, and manifest
- `app/src/main/java/` – Kotlin source code
- `app/src/main/res/` – Layouts, drawables, strings, and XML resources

## Main Activities

Based on the app manifest, the app includes:

- `SplashActivity`
- `LoginActivity`
- `BerandaActivity`
- `BerandaGuruActivity`
- `JuzAmmaActivity`
- `DetailSurahActivity`
- `BacaanSholatActivity`
- `DzikirActivity`
- `KegiatanUserActivity`
- `AbsensiActivity`

## Requirements

To run this project, you will need:

- Android Studio
- JDK 11
- Android SDK with API level 36 support
- An Android device or emulator with minimum API level 24

## Setup

1. Clone the repository:
   ```bash
   git clone https://github.com/ghaniymadea/Ramadhan-Digital.git
