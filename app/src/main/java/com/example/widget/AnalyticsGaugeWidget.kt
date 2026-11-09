package com.example.widget

import android.content.Context
import android.content.Intent
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.action.actionStartActivity
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.padding
import com.example.MainActivity
import com.example.theme.DarkCardBg

/** 2x2 widget: half-donut gauge of this year's spend, broken down by category. */
class AnalyticsGaugeWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val snapshot = WidgetDataSource.loadSnapshot(context)
        val categorySlices = WidgetDataSource.categoryBreakdown(snapshot.subscriptions)
        val totalAnnual = WidgetDataSource.totalAnnualSpend(snapshot.subscriptions)
        val bitmap = GaugeBitmapRenderer.render(categorySlices, totalAnnual, snapshot.currencySymbol)

        provideContent {
            Box(
                modifier = GlanceModifier
                    .fillMaxSize()
                    .background(DarkCardBg)
                    .cornerRadius(18.dp)
                    .padding(10.dp)
                    .clickable(actionStartActivity(Intent(context, MainActivity::class.java).apply {
                        putExtra(MainActivity.EXTRA_NAV_TAB, MainActivity.TAB_ANALYTICS)
                    })),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    provider = ImageProvider(bitmap),
                    contentDescription = "Yearly spend by category",
                    modifier = GlanceModifier.fillMaxSize()
                )
            }
        }
    }
}

class AnalyticsGaugeWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = AnalyticsGaugeWidget()
}
