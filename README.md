# Subscription Tracker 📱💳

A modern, offline-first Android application built with **Kotlin** and **Jetpack Compose** (Material Design 3) to effortlessly monitor, organize, and analyze your recurring subscriptions and expenses.

---

## ✨ Features

- **📊 Comprehensive Overview**
  - Instant glance at your monthly and annual commitments.
  - Highlights upcoming bills due soon so you never miss a renewal or forget to cancel an unwanted service.
  - Active subscription count and average expense breakdown.

- **📑 Subscription Management**
  - Add, edit, and organize subscriptions with custom names, categories, billing cycles (weekly, monthly, yearly, custom), and notes.
  - Tagging and payment method assignment.
  - Instant search, filter, and sorting (by cost, renewal date, name, category).

- **📈 Spending Analytics**
  - Interactive visual charts for spending distribution across categories (Entertainment, Productivity, Utilities, etc.).
  - Cost projection models to understand yearly run-rates.

- **💱 Multi-Currency Support**
  - Flexible multi-currency entry with automatic conversion into your chosen primary currency.
  - Sleek, compact currency selector with searchable international currencies.

- **⏰ Smart Bill Reminders**
  - Configurable push notifications powered by Android **WorkManager**.
  - Receive alerts ahead of upcoming billing dates to review renewals before you are charged.

- **🌍 Internationalization (i18n)**
  - Native multi-language support with instant in-app language switching.

- **🔒 Offline-First & Privacy-Focused**
  - Stored 100% locally on your device via **Room Database**.
  - No external account, tracking, or cloud lock-in required.

---

## 🛠️ Tech Stack & Architecture

- **Language:** [Kotlin](https://kotlinlang.org/)
- **UI Toolkit:** [Jetpack Compose](https://developer.android.com/jetpack/compose) with **Material Design 3**
- **Architecture:** MVVM (Model-View-ViewModel) + Clean Architecture principles
- **Local Persistence:** [Room](https://developer.android.com/training/data-storage/room) (SQLite ORM)
- **Background Tasks:** [AndroidX WorkManager](https://developer.android.com/topic/libraries/architecture/workmanager)
- **Asynchrony & State:** Kotlin Coroutines & `StateFlow`
- **Target SDK:** 36 (Android 14+)
- **Minimum SDK:** 26 (Android 8.0 Oreo)
- **Target ABI:** `arm64-v8a`

---

## 📁 Project Structure

```text
com.georgearn.subscriptiontracker
├── data
│   ├── database/        # Room Database & DAOs
│   ├── model/           # Subscription entity & data models
│   └── repository/      # Repository implementation & data access
├── logic/               # Calculation engines, exchange rates & sorting logic
├── localization/        # Multi-language string catalogs & locale switchers
├── service/             # Notification helpers & WorkManager scheduled workers
├── theme/               # Material 3 Color Schemes, Typography & Shapes
└── ui/
    ├── screens/         # Overview, Subscriptions, Analytics, Settings
    └── MainActivity.kt  # Root navigation & edge-to-edge container
```

---

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
