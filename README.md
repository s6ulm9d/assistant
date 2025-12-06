# Jarvis Assistant 🤖

**Jarvis Assistant** is a powerful, AI-driven Android application designed to automate tasks and interact with your device using natural language. Built with modern Android development practices (Kotlin, Hilt, Clean Architecture) and powered by Google's **Gemini Pro** AI model.

It goes beyond simple chatbots by integrating deeply with the Android system via **Accessibility Services** and **Notification Listeners** to perform real actions like sending WhatsApp messages, playing music, toggling system settings, and more.

## 🚀 Features

-   **Intelligent Conversation**: Chat naturally with Gemini Pro to get answers and assistance.
-   **Device Automation & Control**:
    -   **Apps**: Open any application by name.
    -   **System**: Toggle WiFi, Bluetooth, Flashlight, and manage volume.
    -   **Navigation**: Scroll, Go Back, Go Home (via Accessibility).
    -   **Input**: Type text and tap buttons automatically (via Accessibility).
-   **Communication**:
    -   Send WhatsApp messages efficiently.
    -   Make phone calls.
    -   Send SMS.
-   **Media Control**: Search and play music on Spotify, YouTube, etc.
-   **Voice Feedback**: Integrated Text-to-Speech (TTS) for voice responses.

## 🛠️ Tech Stack

-   **Language**: Kotlin
-   **Architecture**: MVVM + Clean Architecture + Repository Pattern
-   **Dependency Injection**: Dagger Hilt
-   **Asynchronous Processing**: Coroutines & Flow
-   **Network**: Retrofit + Gson + OkHttp
-   **AI Model**: Google Gemini 1.5 Pro / Flash
-   **Build System**: Gradle (Kotlin DSL) with Version Catalogs logic

## ⚙️ Setup & Configuration

This project uses **Gemini API** for its intelligence. To protect sensitive keys, the API key is not included in the repository.

### 1. Prerequisites
-   Android Studio Iguana or newer.
-   JDK 17.
-   An API Key from [Google AI Studio](https://aistudio.google.com/).

### 2. Configure Secrets
Create a file named `local.properties` in the root directory of the project (if it doesn't already exist). This file is git-ignored.

Add your Gemini API Key to it:

```properties
sdk.dir=C\:\\Users\\YourUsername\\AppData\\Local\\Android\\Sdk
GEMINI_API_KEY=your_actual_api_key_here
```

> **Note**: The app reads this key at build time and injects it into the `BuildConfig`. If you skip this, the app may fail to build or run.

### 3. Build the Project
Sync the project with Gradle files and build:

```bash
./gradlew clean assembleDebug
```

## 📱 Permissions

This app requires sensitive permissions to function as an automation assistant:
-   **Accessibility Service**: To interact with other apps (tap, scroll, type).
-   **Notification Listener**: To read incoming notifications.
-   **Phone/SMS**: To initiate calls and messages.

## 🤝 Contribution

1.  Fork the repository.
2.  Create a feature branch (`git checkout -b feature/amazing-feature`).
3.  Commit changes (`git commit -m 'Add amazing feature'`).
4.  Push to branch (`git push origin feature/amazing-feature`).
5.  Open a Pull Request.

## 📄 License

Distributed under the MIT License. See `LICENSE` for more information.
