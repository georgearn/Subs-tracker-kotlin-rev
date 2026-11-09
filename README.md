# Subscription Tracker 📱💳

A modern, offline-first Android application built with **Kotlin** and **Jetpack Compose** (Material Design 3) to effortlessly monitor, organize, and analyze your recurring subscriptions and expenses.

---

## ✨ Features

- **📊 Comprehensive Overview**
  - Instant glance at your monthly and annual commitments.
  - Interactive monthly and yearly calendar view highlighting payment dates.
  - Active subscription count and expense breakdown.

- **📑 Subscription Management**
  - Add, edit, and organize subscriptions with custom names, categories, billing cycles (weekly, monthly, quarterly, yearly, custom interval days), and card numbers.
  - Quick swipe-to-delete gesture and quick delete dialogs.

- **📜 Price History Tracking**
  - Keeps historical records when a subscription's price changes over time without altering past months' accounting.
  - Dedicated Price History tab with the exact date when price changes took effect.

- **💳 Card Visualizer & Customization**
  - Interactive 3D wallet card visuals with EMV chip graphics.
  - Custom card color presets (Gunmetal, Sapphire Navy, Emerald Green, Imperial Purple, Rose Bronze) and card aliases/nicknames.

- **📈 Spending Analytics**
  - Half-donut category spend gauge and category breakdown bars.
  - Group analytics by Bank Card or by Category.

- **💱 Multi-Currency Support**
  - Primary currency selector with international currency support.
  - Secondary currency entry per subscription for cross-border subscriptions.

- **🧩 Home Screen Widgets**
  - Four interactive **Jetpack Glance** widgets (Calendar, Monthly Total, Next Payment, Category Gauge) with live preview support in the Android widget picker.

- **💾 Data Backup & Import/Export**
  - Backup and restore subscription data via JSON import/export.
  - Export data to CSV spreadsheet format.

- **📅 Calendar Integration**
  - One-tap `.ics` calendar event generation with recurring rules (`RRULE`) for integration with system calendar apps.

- **⏰ Smart Bill Reminders**
  - Daily payment reminders and monthly digest notifications powered by Android **WorkManager**.

- **🎨 Material 3 & Dynamic Theme**
  - Full **Material Design 3** compliance with adaptive Dark, Light, and System theme modes.
  - Optional **Material You** dynamic accent colors (Android 12+).

- **🌍 Internationalization (i18n)**
  - Native multi-language support (English & Russian) with instant in-app language switching.

- **🔒 Offline-First & Privacy-Focused**
  - Stored 100% locally on your device via **Room Database**.
  - No external account, tracking, or cloud lock-in required.

---

## 🛠️ Tech Stack & Architecture

- **Language:** [Kotlin](https://kotlinlang.org/)
- **UI Toolkit:** [Jetpack Compose](https://developer.android.com/jetpack/compose) with **Material Design 3**
- **Home Screen Widgets:** [AndroidX Glance](https://developer.android.com/jetpack/compose/glance) 1.1+
- **Architecture:** MVVM (Model-View-ViewModel) + Clean Architecture principles
- **Local Persistence:** [Room](https://developer.android.com/training/data-storage/room) 2.7+ (SQLite ORM with KSP)
- **Background Tasks:** [AndroidX WorkManager](https://developer.android.com/topic/libraries/architecture/workmanager)
- **Serialization:** Kotlinx Serialization (JSON)
- **Asynchrony & State:** Kotlin Coroutines & `StateFlow`
- **Compile SDK:** 37
- **Target SDK:** 37 (Android 16)
- **Minimum SDK:** 31 (Android 12)
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
├── widget/              # Jetpack Glance Home Screen AppWidgets & Bitmap renderers
└── ui/
    ├── components/      # Reusable UI components (HeroCard, BankCardVisual, ArcGauge, etc.)
    ├── dialogs/         # Add/Edit sheet, Import/Export, Month Picker
    ├── screens/         # Overview, Subscriptions, Analytics, Settings
    └── MainActivity.kt  # Root navigation & edge-to-edge container
```

---

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
