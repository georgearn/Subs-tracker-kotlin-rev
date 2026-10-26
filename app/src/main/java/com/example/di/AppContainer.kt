package com.example.di

import android.content.Context
import com.example.data.database.SubscriptionDatabase
import com.example.data.repository.SubscriptionRepository
import com.example.data.settings.SettingsRepository
import com.example.platform.SystemServices

/** Manual dependency graph, created once by [com.example.SubscriptionApp]. */
class AppContainer(context: Context) {
    private val appContext = context.applicationContext
    private val database by lazy { SubscriptionDatabase.getDatabase(appContext) }

    val subscriptionRepository by lazy { SubscriptionRepository(database.subscriptionDao()) }
    val settingsRepository by lazy { SettingsRepository(database.subscriptionDao()) }
    val systemServices by lazy { SystemServices(appContext) }
}
