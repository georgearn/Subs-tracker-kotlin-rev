package com.example.data.repository

import com.example.data.database.SubscriptionDao
import com.example.data.model.CardAliasEntity
import com.example.data.model.SettingEntity
import com.example.data.model.SubscriptionEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class SubscriptionRepository(private val dao: SubscriptionDao) {

    val allSubscriptions: Flow<List<SubscriptionEntity>> = dao.getAllSubscriptions()
    val allCardAliases: Flow<List<CardAliasEntity>> = dao.getAllCardAliases()

    val allCardColors: Flow<Map<String, String>> = dao.getSetting("card_colors").map { jsonStr ->
        if (jsonStr.isNullOrBlank()) {
            emptyMap()
        } else {
            try {
                Json.decodeFromString<Map<String, String>>(jsonStr)
            } catch (_: Exception) {
                emptyMap()
            }
        }
    }

    suspend fun setCardColor(card: String, colorHex: String) {
        val currentJson = dao.getSettingSync("card_colors")
        val currentMap = if (!currentJson.isNullOrBlank()) {
            try {
                Json.decodeFromString<Map<String, String>>(currentJson).toMutableMap()
            } catch (_: Exception) {
                mutableMapOf()
            }
        } else {
            mutableMapOf()
        }
        currentMap[card] = colorHex
        dao.setSetting(SettingEntity(key = "card_colors", value = Json.encodeToString(currentMap)))
    }

    fun getAppLanguage(): Flow<String?> = dao.getSetting("app_language")

    suspend fun setAppLanguage(lang: String) {
        dao.setSetting(SettingEntity(key = "app_language", value = lang))
    }

    fun getPrimaryCurrency(): Flow<String?> = dao.getSetting("primary_currency")

    suspend fun setPrimaryCurrency(currency: String) {
        dao.setSetting(SettingEntity(key = "primary_currency", value = currency))
    }

    fun getThemeMode(): Flow<String?> = dao.getSetting("theme_mode")

    suspend fun setThemeMode(mode: String) {
        dao.setSetting(SettingEntity(key = "theme_mode", value = mode))
    }

    fun getUseMaterialAccent(): Flow<String?> = dao.getSetting("use_material_accent")

    suspend fun setUseMaterialAccent(use: Boolean) {
        dao.setSetting(SettingEntity(key = "use_material_accent", value = use.toString()))
    }

    fun getMonthlyNotificationEnabled(): Flow<Boolean> = dao.getSetting("monthly_notification_enabled").map {
        it?.toBooleanStrictOrNull() ?: true
    }

    suspend fun setMonthlyNotificationEnabled(enabled: Boolean) {
        dao.setSetting(SettingEntity(key = "monthly_notification_enabled", value = enabled.toString()))
    }

    suspend fun insertSubscription(subscription: SubscriptionEntity): Long {
        return dao.insertSubscription(subscription)
    }

    suspend fun updateSubscription(subscription: SubscriptionEntity) {
        dao.updateSubscription(subscription)
    }

    suspend fun deleteSubscription(id: Int) {
        dao.deleteSubscription(id)
    }

    suspend fun deleteAllSubscriptions() {
        dao.deleteAllSubscriptions()
    }

    suspend fun setCardAlias(card: String, alias: String) {
        dao.setCardAlias(CardAliasEntity(card = card, alias = alias))
    }

    suspend fun getAllSubscriptionsSync(): List<SubscriptionEntity> {
        return dao.getAllSubscriptionsSync()
    }

    suspend fun getPrimaryCurrencySync(): String {
        return dao.getSettingSync("primary_currency") ?: "USD"
    }

    suspend fun getMonthlyNotificationEnabledSync(): Boolean {
        return dao.getSettingSync("monthly_notification_enabled")?.toBooleanStrictOrNull() ?: true
    }

    suspend fun getCardAliasSync(card: String): String? {
        return dao.getCardAliasSync(card)
    }

    suspend fun getAppLanguageSync(): String? {
        return dao.getSettingSync("app_language")
    }
}
