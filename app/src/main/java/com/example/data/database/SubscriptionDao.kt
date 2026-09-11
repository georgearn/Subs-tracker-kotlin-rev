package com.example.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.CardAliasEntity
import com.example.data.model.SettingEntity
import com.example.data.model.SubscriptionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SubscriptionDao {

    @Query("SELECT * FROM subscriptions ORDER BY name COLLATE NOCASE ASC")
    fun getAllSubscriptions(): Flow<List<SubscriptionEntity>>

    @Query("SELECT * FROM subscriptions ORDER BY name COLLATE NOCASE ASC")
    suspend fun getAllSubscriptionsSync(): List<SubscriptionEntity>

    @Query("SELECT * FROM subscriptions WHERE id = :id")
    fun getSubscriptionById(id: Int): Flow<SubscriptionEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubscription(sub: SubscriptionEntity): Long

    @Update
    suspend fun updateSubscription(sub: SubscriptionEntity)

    @Query("DELETE FROM subscriptions WHERE id = :id")
    suspend fun deleteSubscription(id: Int)

    @Query("DELETE FROM subscriptions")
    suspend fun deleteAllSubscriptions()

    // Settings
    @Query("SELECT value FROM settings WHERE key = :key")
    fun getSetting(key: String): Flow<String?>

    @Query("SELECT value FROM settings WHERE key = :key")
    suspend fun getSettingSync(key: String): String?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun setSetting(setting: SettingEntity)

    // Card Aliases
    @Query("SELECT * FROM card_aliases")
    fun getAllCardAliases(): Flow<List<CardAliasEntity>>

    @Query("SELECT alias FROM card_aliases WHERE card = :card")
    fun getCardAlias(card: String): Flow<String?>

    @Query("SELECT alias FROM card_aliases WHERE card = :card")
    suspend fun getCardAliasSync(card: String): String?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun setCardAlias(alias: CardAliasEntity)
}
