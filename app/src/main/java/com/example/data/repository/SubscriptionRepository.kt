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

    val allCardColors: Flow<Map<String, String>> = dao.getSetting(CARD_COLORS_KEY).map { jsonStr ->
        decodeColors(jsonStr)
    }

    suspend fun setCardColor(card: String, colorHex: String) {
        val updated = decodeColors(dao.getSettingSync(CARD_COLORS_KEY)) + (card to colorHex)
        dao.setSetting(SettingEntity(key = CARD_COLORS_KEY, value = Json.encodeToString(updated)))
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

    suspend fun getCardAliasSync(card: String): String? {
        return dao.getCardAliasSync(card)
    }

    private fun decodeColors(jsonStr: String?): Map<String, String> {
        if (jsonStr.isNullOrBlank()) return emptyMap()
        return try {
            Json.decodeFromString<Map<String, String>>(jsonStr)
        } catch (_: Exception) {
            emptyMap()
        }
    }

    private companion object {
        const val CARD_COLORS_KEY = "card_colors"
    }
}
