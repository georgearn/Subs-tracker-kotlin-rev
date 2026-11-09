package com.example.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.model.CardAliasEntity
import com.example.data.model.SettingEntity
import com.example.data.model.SubscriptionEntity

@Database(
    entities = [SubscriptionEntity::class, SettingEntity::class, CardAliasEntity::class],
    version = 2,
    exportSchema = false
)
abstract class SubscriptionDatabase : RoomDatabase() {

    abstract fun subscriptionDao(): SubscriptionDao

    companion object {
        @Volatile
        private var INSTANCE: SubscriptionDatabase? = null

        // Adds end_date so a price edit can freeze the old price for past months
        // instead of retroactively rewriting them — purely additive, no data loss.
        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE subscriptions ADD COLUMN end_date TEXT DEFAULT NULL")
            }
        }

        fun getDatabase(context: Context): SubscriptionDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SubscriptionDatabase::class.java,
                    "subscriptions.db"
                )
                    .addMigrations(MIGRATION_1_2)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
