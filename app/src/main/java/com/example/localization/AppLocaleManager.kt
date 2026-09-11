package com.example.localization

import android.app.LocaleManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.LocaleList
import android.provider.Settings
import java.util.Locale

object AppLocaleManager {

    /**
     * Reads the per-app language configured in Android system settings (Android 13+ / API 33+).
     * Returns "en", "ru", or null if not explicitly set (or on older Android versions).
     */
    fun getSystemAppLanguage(context: Context): String? {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            try {
                val localeManager = context.getSystemService(LocaleManager::class.java)
                val locales = localeManager?.applicationLocales
                if (locales != null && !locales.isEmpty) {
                    val tag = locales[0].language
                    return when (tag.lowercase()) {
                        "ru" -> "ru"
                        "en" -> "en"
                        else -> tag.lowercase()
                    }
                }
            } catch (e: Exception) {
                // Ignore fallback
            }
        }
        return null
    }

    /**
     * Updates Android system settings per-app language (Android 13+ / API 33+).
     * This keeps Phone Settings -> Apps -> Subscription Tracker -> Language in sync with in-app settings.
     */
    fun setSystemAppLanguage(context: Context, langCode: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            try {
                val localeManager = context.getSystemService(LocaleManager::class.java)
                if (localeManager != null) {
                    val locale = Locale.forLanguageTag(langCode)
                    localeManager.applicationLocales = LocaleList(locale)
                }
            } catch (e: Exception) {
                // Ignore fallback
            }
        }
    }

    /**
     * Opens Phone Settings -> Apps -> Subscription Tracker -> Language (App Language) directly.
     */
    fun openSystemAppLanguageSettings(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            try {
                val intent = Intent(Settings.ACTION_APP_LOCALE_SETTINGS).apply {
                    data = Uri.fromParts("package", context.packageName, null)
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(intent)
                return
            } catch (e: Exception) {
                // Fallback to application details
            }
        }

        try {
            val fallbackIntent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = Uri.fromParts("package", context.packageName, null)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(fallbackIntent)
        } catch (_: Exception) {
            // Cannot open settings
        }
    }
}
