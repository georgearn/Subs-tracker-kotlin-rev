package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.localization.LocalStrings
import com.example.theme.Brand
import com.example.theme.CardBg
import com.example.theme.CardBgElevated
import com.example.theme.TextMuted
import com.example.theme.TextWhite
import com.example.ui.glass.CardGlass
import com.example.ui.glass.glassSurface

/** Money spent in one calendar month, for the yearly view. */
data class MonthTotal(val month: Int, val total: Double, val paymentCount: Int)

@Composable
fun YearSummaryCard(
    year: Int,
    totals: List<MonthTotal>,
    currentMonth: Int?,
    currency: String,
    modifier: Modifier = Modifier
) {
    val strings = LocalStrings.current
    val yearTotal = totals.sumOf { it.total }
    val peak = totals.maxByOrNull { it.total }?.takeIf { it.total > 0.0 }
    val maxTotal = peak?.total ?: 1.0

    Column(
        modifier = modifier
            .fillMaxWidth()
            .glassSurface(RoundedCornerShape(20.dp), CardBgElevated)
            .clip(RoundedCornerShape(20.dp))
            .padding(18.dp)
    ) {
        Text(text = strings.yearTotal(year), color = TextMuted, fontSize = 13.sp)
        Text(
            text = "${"%.2f".format(yearTotal)} $currency",
            color = TextWhite,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.Bottom
        ) {
            totals.forEach { entry ->
                val fraction = (entry.total / maxTotal).toFloat().coerceIn(0.06f, 1f)
                val isCurrent = entry.month == currentMonth
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(fraction)
                        .clip(RoundedCornerShape(5.dp))
                        .background(if (isCurrent) Brand else Brand.copy(alpha = 0.3f))
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(24.dp)) {
            Column {
                Text(text = strings.avgPerMonth, color = TextMuted, fontSize = 12.sp)
                Text(
                    text = "${"%.2f".format(yearTotal / 12.0)} $currency",
                    color = TextWhite,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
            if (peak != null) {
                Column {
                    Text(text = strings.peakMonth, color = TextMuted, fontSize = 12.sp)
                    Text(
                        text = "${strings.getMonthName(peak.month, short = true)} · ${"%.2f".format(peak.total)}",
                        color = Brand,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

@Composable
fun MonthTile(
    entry: MonthTotal,
    maxTotal: Double,
    isCurrent: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val strings = LocalStrings.current
    val shape = RoundedCornerShape(18.dp)
    val glass = if (isCurrent) CardGlass.tint(Brand.copy(alpha = 0.2f)) else CardGlass
    val fraction = if (maxTotal > 0.0) (entry.total / maxTotal).toFloat().coerceIn(0f, 1f) else 0f

    Column(
        modifier = modifier
            .glassSurface(shape, CardBg, glass)
            .clip(shape)
            .clickable(onClick = onClick)
            .padding(12.dp)
            .testTag("month_tile_${entry.month}")
    ) {
        Text(
            text = strings.getMonthName(entry.month, short = true),
            color = if (isCurrent) Brand else TextWhite,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = "%.2f".format(entry.total),
            color = TextWhite,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = strings.paymentsCount(entry.paymentCount),
            color = TextMuted,
            fontSize = 11.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Spacer(modifier = Modifier.height(10.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(5.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(CardBgElevated)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(fraction)
                    .clip(RoundedCornerShape(3.dp))
                    .background(Brand)
            )
        }
    }
}
