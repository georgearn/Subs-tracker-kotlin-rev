package com.example.widget

import android.content.Context
import androidx.glance.appwidget.updateAll

/**
 * Called after any subscription data change so every home screen widget refreshes right
 * away, instead of waiting on the OS's own ~30 minute update window.
 */
object WidgetUpdater {
    suspend fun updateAll(context: Context) {
        MonthTotalWidget().updateAll(context)
        NextPaymentWidget().updateAll(context)
        CalendarWidget().updateAll(context)
        AnalyticsGaugeWidget().updateAll(context)
    }
}
