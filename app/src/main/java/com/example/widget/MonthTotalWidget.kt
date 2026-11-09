package com.example.widget

import android.content.Context
import android.content.Intent
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.action.actionStartActivity
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.example.MainActivity
import com.example.theme.DarkCardBg
import com.example.theme.DarkTextMuted
import com.example.theme.DarkTextWhite

/** 2x1 widget: total cost of all subscriptions due this calendar month. */
class MonthTotalWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val snapshot = WidgetDataSource.loadSnapshot(context)
        val total = WidgetDataSource.monthTotal(snapshot.subscriptions)

        provideContent {
            Box(
                modifier = GlanceModifier
                    .fillMaxSize()
                    .background(DarkCardBg)
                    .cornerRadius(18.dp)
                    .padding(12.dp)
                    .clickable(actionStartActivity(Intent(context, MainActivity::class.java).apply {
                        putExtra(MainActivity.EXTRA_NAV_TAB, MainActivity.TAB_OVERVIEW)
                    })),
                contentAlignment = Alignment.CenterStart
            ) {
                Column {
                    Text(
                        text = "This month",
                        style = TextStyle(color = ColorProvider(DarkTextMuted), fontSize = 12.sp, fontWeight = FontWeight.Normal)
                    )
                    Spacer(modifier = GlanceModifier.height(2.dp))
                    Text(
                        text = "%.2f %s".format(total, snapshot.currencySymbol),
                        style = TextStyle(color = ColorProvider(DarkTextWhite), fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    )
                }
            }
        }
    }
}

class MonthTotalWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = MonthTotalWidget()
}
