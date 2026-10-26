package com.example.platform

import android.content.Context
import com.example.data.model.SubscriptionEntity
import com.example.localization.AppLocaleManager
import com.example.service.NotificationHelper
import com.example.service.SubscriptionReminderWorker

/** Android framework calls the ViewModel needs, kept behind one seam so it holds no Context. */
class SystemServices(private val context: Context) {

    fun scheduleDailyReminder() {
        SubscriptionReminderWorker.scheduleDailyReminder(context)
    }

    fun sendReminders(
        subscriptions: List<SubscriptionEntity>,
        currency: String,
        monthlyEnabled: Boolean,
        dailyEnabled: Boolean
    ) {
        NotificationHelper.checkAndSendReminders(
            context, subscriptions, currency, monthlyEnabled, dailyEnabled
        )
    }

    fun systemLanguage(): String? = AppLocaleManager.getSystemAppLanguage(context)

    fun applyLanguage(code: String) {
        AppLocaleManager.setSystemAppLanguage(context, code)
    }
}
