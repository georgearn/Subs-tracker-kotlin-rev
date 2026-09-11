package com.example.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.example.R
import com.example.data.model.SubscriptionEntity
import com.example.logic.SubscriptionCalculations
import java.time.LocalDate
import java.time.format.DateTimeFormatter

object NotificationHelper {

    private const val CHANNEL_ID = "sub_tracker_notify"
    private const val CHANNEL_NAME = "Subscription Tracker"
    private const val DAILY_NOTIF_ID = 9002
    private const val MONTHLY_NOTIF_ID = 9001

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance).apply {
                description = "Subscription payment reminders"
            }
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun checkAndSendReminders(
        context: Context,
        subscriptions: List<SubscriptionEntity>,
        currency: String
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val permission = ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.POST_NOTIFICATIONS
            )
            if (permission != PackageManager.PERMISSION_GRANTED) {
                return
            }
        }

        createNotificationChannel(context)
        val today = LocalDate.now()
        val prefs = context.getSharedPreferences("notif_tracking", Context.MODE_PRIVATE)

        // 1. Monthly reminder on 1st of month
        if (today.dayOfMonth == 1) {
            val monthKey = "${today.year}-${today.monthValue}:monthly"
            if (!prefs.getBoolean(monthKey, false)) {
                sendMonthlyNotification(context, subscriptions, currency, today)
                prefs.edit().putBoolean(monthKey, true).apply()
            }
        }

        // 2. Daily payment reminder
        val dailyKey = "${today}:daily"
        if (!prefs.getBoolean(dailyKey, false)) {
            val dueToday = subscriptions.filter { sub ->
                SubscriptionCalculations.occurrencesInMonth(sub, today.year, today.monthValue)
                    .contains(today)
            }
            if (dueToday.isNotEmpty()) {
                sendDailyNotification(context, dueToday, currency)
                prefs.edit().putBoolean(dailyKey, true).apply()
            }
        }
    }

    private fun sendDailyNotification(
        context: Context,
        dueSubs: List<SubscriptionEntity>,
        currency: String
    ) {
        val total = dueSubs.sumOf { it.priceUsd }
        val title = "\uD83D\uDCB3 Payments due today"
        val bodyLines = dueSubs.joinToString("\n") { "• ${it.name}  ${"%.2f".format(it.priceUsd)} $currency" }
        val content = "${"%.2f".format(total)} $currency in payments today\n$bodyLines"

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText("${dueSubs.size} payments due today (${"%.2f".format(total)} $currency)")
            .setStyle(NotificationCompat.BigTextStyle().bigText(content))
            .setAutoCancel(true)
            .build()

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(DAILY_NOTIF_ID, notification)
    }

    private fun sendMonthlyNotification(
        context: Context,
        subscriptions: List<SubscriptionEntity>,
        currency: String,
        today: LocalDate
    ) {
        val occurrences = mutableListOf<String>()
        var total = 0.0
        val monthName = today.month.name.lowercase().replaceFirstChar { it.uppercase() }

        for (sub in subscriptions) {
            val days = SubscriptionCalculations.occurrencesInMonth(sub, today.year, today.monthValue)
            for (d in days) {
                total += sub.priceUsd
                occurrences.add("• ${sub.name}  ${"%.2f".format(sub.priceUsd)} $currency (${d.format(DateTimeFormatter.ofPattern("MMM dd"))})")
            }
        }

        if (occurrences.isEmpty()) return

        val title = "\uD83D\uDCCB $monthName subscriptions"
        val header = "$monthName ${today.year}: ${occurrences.size} payments, ${"%.2f".format(total)} $currency total\n"
        val content = header + occurrences.sorted().joinToString("\n")

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText("${occurrences.size} subscriptions this month (${"%.2f".format(total)} $currency)")
            .setStyle(NotificationCompat.BigTextStyle().bigText(content))
            .setAutoCancel(true)
            .build()

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(MONTHLY_NOTIF_ID, notification)
    }

    fun sendTestNotification(
        context: Context,
        subscriptions: List<SubscriptionEntity>,
        currency: String
    ) {
        createNotificationChannel(context)
        val sampleSub = subscriptions.firstOrNull()?.name ?: "Netflix"
        val samplePrice = subscriptions.firstOrNull()?.priceUsd ?: 15.99
        val title = "\uD83D\uDD14 Test Reminder: Upcoming Payment"
        val content = "Upcoming renewal for $sampleSub: ${"%.2f".format(samplePrice)} $currency due soon!\nBackground reminders are working properly."

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText("Payment reminder test for $sampleSub")
            .setStyle(NotificationCompat.BigTextStyle().bigText(content))
            .setAutoCancel(true)
            .build()

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(9003, notification)
    }
}
