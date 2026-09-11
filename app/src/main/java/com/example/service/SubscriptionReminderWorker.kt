package com.example.service

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.example.data.database.SubscriptionDatabase
import com.example.data.repository.SubscriptionRepository
import java.util.concurrent.TimeUnit

class SubscriptionReminderWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            val db = SubscriptionDatabase.getDatabase(applicationContext)
            val repository = SubscriptionRepository(db.subscriptionDao())
            val subscriptions = repository.getAllSubscriptionsSync()
            val currency = repository.getPrimaryCurrencySync()

            NotificationHelper.checkAndSendReminders(applicationContext, subscriptions, currency)
            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            Result.retry()
        }
    }

    companion object {
        private const val WORK_NAME = "subscription_daily_reminder_work"

        fun scheduleDailyReminder(context: Context) {
            val workRequest = PeriodicWorkRequestBuilder<SubscriptionReminderWorker>(
                24, TimeUnit.HOURS
            ).build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                workRequest
            )
        }
    }
}
