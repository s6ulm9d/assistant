# Jarvis Assistant

A powerful Android AI assistant built with Kotlin, Hilt, and Gemini.

## Tech Stack
- **Language**: Kotlin 1.9.22
- **Architecture**: MVVM + Clean Architecture
- **DI**: Hilt 2.51
- **Build**: Gradle 8.2 (AGP 8.2.0)
- **Annotation Processing**: KSP (Kotlin Symbol Processing)
- **Network**: Retrofit + Gson
- **AI**: Gemini API

## Build Instructions
1. **Prerequisites**: JDK 17, Android Studio Iguana or later.
2. **Build**:
   ```bash
   ./gradlew clean assembleDebug
   ```

## Icon Generation
Use `python generate_icons.py` to regenerate launcher icons when the base logo changes.
Ensure you have `Pillow` installed: `pip install Pillow`.

## Features
- **Deep App Integration**: Control Spotify and other apps.
- **System Control**: Toggle WiFi, Bluetooth, Flashlight.
- **Communication**: Send SMS, WhatsApp, Make Calls.
- **AI Powered**: Uses Gemini for intelligent responses and action routing.
