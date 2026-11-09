package com.example.widget

import android.content.Context
import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextAlign
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.example.MainActivity
import com.example.data.model.SubscriptionEntity
import com.example.theme.DarkCardBg
import com.example.theme.DarkTextMuted
import com.example.theme.DarkTextWhite
import com.example.theme.DefaultBrand
import com.example.theme.getCategoryColor
import com.example.ui.components.findBrandDrawableId
import com.example.ui.components.findBrandStyle
import java.time.LocalDate
import java.time.format.TextStyle as JavaTextStyle
import java.util.Locale

/** 4x2 widget: today plus the next 3 days, with an icon/badge on days a payment is due. */
class CalendarWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val snapshot = WidgetDataSource.loadSnapshot(context)
        val today = LocalDate.now()
        val days = (0..3).map { today.plusDays(it.toLong()) }
        val byDay = WidgetDataSource.occurrencesByDay(snapshot.subscriptions, days)

        provideContent {
            Box(
                modifier = GlanceModifier
                    .fillMaxSize()
                    .background(DarkCardBg)
                    .cornerRadius(18.dp)
                    .padding(12.dp)
                    .clickable(actionStartActivity(Intent(context, MainActivity::class.java).apply {
                        putExtra(MainActivity.EXTRA_NAV_TAB, MainActivity.TAB_OVERVIEW)
                    }))
            ) {
                Row(modifier = GlanceModifier.fillMaxSize()) {
                    days.forEach { day ->
                        val isToday = day == today
                        val subsForDay = byDay[day].orEmpty()
                        Column(
                            modifier = GlanceModifier.defaultWeight(),
                            horizontalAlignment = Alignment.Horizontal.CenterHorizontally
                        ) {
                            Text(
                                text = day.dayOfWeek.getDisplayName(JavaTextStyle.SHORT, Locale.getDefault()).take(3),
                                style = TextStyle(color = ColorProvider(DarkTextMuted), fontSize = 10.sp, textAlign = TextAlign.Center)
                            )
                            Spacer(modifier = GlanceModifier.height(2.dp))
                            Text(
                                text = day.dayOfMonth.toString(),
                                style = TextStyle(
                                    color = ColorProvider(if (isToday) DefaultBrand else DarkTextWhite),
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center
                                )
                            )
                            Spacer(modifier = GlanceModifier.height(4.dp))
                            DayPaymentBadge(subsForDay)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DayPaymentBadge(subs: List<SubscriptionEntity>) {
    when {
        subs.isEmpty() -> {
            Spacer(modifier = GlanceModifier.size(22.dp))
        }
        subs.size == 1 -> {
            val sub = subs[0]
            val resId = findBrandDrawableId(sub.name)
            val bg = findBrandStyle(sub.name)?.backgroundColor ?: getCategoryColor(sub.category)
            Box(
                modifier = GlanceModifier.size(22.dp).background(bg).cornerRadius(11.dp),
                contentAlignment = Alignment.Center
            ) {
                if (resId != null) {
                    Image(
                        provider = ImageProvider(resId),
                        contentDescription = sub.name,
                        modifier = GlanceModifier.size(14.dp)
                    )
                } else {
                    Text(
                        text = sub.name.trim().take(1).uppercase(),
                        style = TextStyle(color = ColorProvider(DarkTextWhite), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    )
                }
            }
        }
        else -> {
            Box(
                modifier = GlanceModifier.size(22.dp).background(DefaultBrand).cornerRadius(11.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = subs.size.toString(),
                    style = TextStyle(color = ColorProvider(DarkTextWhite), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                )
            }
        }
    }
}

class CalendarWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = CalendarWidget()
}
