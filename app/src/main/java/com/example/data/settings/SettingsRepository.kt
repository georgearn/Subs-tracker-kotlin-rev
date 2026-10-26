package com.example.data.settings

import com.example.data.database.SubscriptionDao
import com.example.data.model.SettingEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class SettingsRepository(private val dao: SubscriptionDao) {

    val settings: Flow<AppSettings> = dao.getAllSettings()
        .map { rows -> rows.associate { it.key to it.value }.toAppSettings() }
        .distinctUntilChanged()

    suspend fun current(): AppSettings = settings.first()

    /** Language persisted in the DB, or null when the user never picked one. */
    suspend fun savedLanguage(): String? = dao.getSettingSync(KEY_LANGUAGE)

    suspend fun setThemeMode(mode: ThemeMode) = put(KEY_THEME_MODE, mode.key)

    suspend fun setUseMaterialAccent(use: Boolean) = put(KEY_USE_MATERIAL_ACCENT, use.toString())

    suspend fun setPrimaryCurrency(currency: String) = put(KEY_PRIMARY_CURRENCY, currency)

    suspend fun setLanguage(language: String) = put(KEY_LANGUAGE, language)

    suspend fun setDailyNotificationEnabled(enabled: Boolean) =
        put(KEY_DAILY_NOTIFICATION, enabled.toString())

    suspend fun setMonthlyNotificationEnabled(enabled: Boolean) =
        put(KEY_MONTHLY_NOTIFICATION, enabled.toString())

    private suspend fun put(key: String, value: String) {
        dao.setSetting(SettingEntity(key = key, value = value))
    }

    private fun Map<String, String>.toAppSettings(): AppSettings {
        val defaults = AppSettings()
        return AppSettings(
            themeMode = ThemeMode.fromKey(this[KEY_THEME_MODE]),
            useMaterialAccent = this[KEY_USE_MATERIAL_ACCENT]?.toBooleanStrictOrNull()
                ?: defaults.useMaterialAccent,
            primaryCurrency = this[KEY_PRIMARY_CURRENCY] ?: defaults.primaryCurrency,
            language = this[KEY_LANGUAGE] ?: defaults.language,
            dailyNotificationEnabled = this[KEY_DAILY_NOTIFICATION]?.toBooleanStrictOrNull()
                ?: defaults.dailyNotificationEnabled,
            monthlyNotificationEnabled = this[KEY_MONTHLY_NOTIFICATION]?.toBooleanStrictOrNull()
                ?: defaults.monthlyNotificationEnabled
        )
    }

    private companion object {
        const val KEY_THEME_MODE = "theme_mode"
        const val KEY_USE_MATERIAL_ACCENT = "use_material_accent"
        const val KEY_PRIMARY_CURRENCY = "primary_currency"
        const val KEY_LANGUAGE = "app_language"
        const val KEY_DAILY_NOTIFICATION = "daily_notification_enabled"
        const val KEY_MONTHLY_NOTIFICATION = "monthly_notification_enabled"
    }
}
