package com.example.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.model.CardAliasEntity
import com.example.data.model.SettingEntity
import com.example.data.model.SubscriptionEntity

@Database(
    entities = [SubscriptionEntity::class, SettingEntity::class, CardAliasEntity::class],
    version = 1,
    exportSchema = false
)
abstract class SubscriptionDatabase : RoomDatabase() {

    abstract fun subscriptionDao(): SubscriptionDao

    companion object {
        @Volatile
        private var INSTANCE: SubscriptionDatabase? = null

        fun getDatabase(context: Context): SubscriptionDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SubscriptionDatabase::class.java,
                    "subscriptions.db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
