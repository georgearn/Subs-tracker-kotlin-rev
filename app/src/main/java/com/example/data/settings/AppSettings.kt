package com.example.data.settings

enum class ThemeMode(val key: String) {
    DARK("dark"),
    LIGHT("light"),
    SYSTEM("system");

    companion object {
        fun fromKey(key: String?): ThemeMode = entries.firstOrNull { it.key == key } ?: DARK
    }
}

data class AppSettings(
    val themeMode: ThemeMode = ThemeMode.DARK,
    val useMaterialAccent: Boolean = false,
    val primaryCurrency: String = "USD",
    val language: String = "en",
    val dailyNotificationEnabled: Boolean = true,
    val monthlyNotificationEnabled: Boolean = true
)
