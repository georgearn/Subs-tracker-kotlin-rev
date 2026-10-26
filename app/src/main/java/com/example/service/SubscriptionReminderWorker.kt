package com.example.service

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.example.appContainer
import java.util.concurrent.TimeUnit

class SubscriptionReminderWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            val container = applicationContext.appContainer
            val subscriptions = container.subscriptionRepository.getAllSubscriptionsSync()
            val settings = container.settingsRepository.current()

            NotificationHelper.checkAndSendReminders(
                applicationContext,
                subscriptions,
                settings.primaryCurrency,
                settings.monthlyNotificationEnabled,
                settings.dailyNotificationEnabled
            )
            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            Result.retry()
        }
    }

    companion object {
        private const val WORK_NAME = "subscription_daily_reminder_work"

        fun scheduleDailyReminder(context: Context) {
            val now = java.time.LocalDateTime.now()
            var targetTime = now.withHour(9).withMinute(0).withSecond(0).withNano(0)
            if (now.isAfter(targetTime)) {
                targetTime = targetTime.plusDays(1)
            }
            val initialDelayMinutes = java.time.Duration.between(now, targetTime).toMinutes()

            val workRequest = PeriodicWorkRequestBuilder<SubscriptionReminderWorker>(
                24, TimeUnit.HOURS
            )
                .setInitialDelay(initialDelayMinutes, TimeUnit.MINUTES)
                .build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                WORK_NAME,
                ExistingPeriodicWorkPolicy.UPDATE,
                workRequest
            )
        }
    }
}
