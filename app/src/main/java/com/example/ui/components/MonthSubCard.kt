package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.SubscriptionEntity
import com.example.localization.LocalStrings
import com.example.theme.CardBg
import com.example.theme.TextMuted
import com.example.theme.TextWhite
import com.example.theme.getCategoryColor
import com.example.ui.glass.flatSurface
import java.time.LocalDate

/**
 * Marks whether this specific occurrence is the first payment under a new price (set by
 * OverviewScreen when it detects a price-history cutover landing on this date), so the card
 * can flag it — null means the price was unchanged from the prior era.
 */
enum class PriceChangeDirection { INCREASED, DECREASED }

@Composable
fun MonthSubCard(
    subscription: SubscriptionEntity,
    dueDate: LocalDate,
    primaryCurrency: String,
    onClick: () -> Unit,
    priceChange: PriceChangeDirection? = null,
    modifier: Modifier = Modifier
) {
    val strings = LocalStrings.current
    val dateStr = strings.formatDateShort(dueDate)
    val catColor = getCategoryColor(subscription.category)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .flatSurface(RoundedCornerShape(16.dp), CardBg)
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .padding(12.dp)
            .testTag("month_sub_card_${subscription.id}_${dueDate.dayOfMonth}")
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                ServiceIcon(
                    name = subscription.name,
                    category = subscription.category,
                    size = 36.dp
                )

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(catColor.copy(alpha = 0.2f))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = dateStr,
                        color = catColor,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = subscription.name,
                color = TextWhite,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(4.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "${"%.2f".format(subscription.priceUsd)} $primaryCurrency",
                    color = TextWhite.copy(alpha = 0.9f),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
                if (priceChange != null) {
                    Spacer(modifier = Modifier.width(4.dp))
                    val (icon, tint, desc) = when (priceChange) {
                        PriceChangeDirection.INCREASED ->
                            Triple(Icons.AutoMirrored.Filled.TrendingUp, Color(0xFFEF4444), strings.priceIncreasedLabel)
                        PriceChangeDirection.DECREASED ->
                            Triple(Icons.AutoMirrored.Filled.TrendingDown, Color(0xFF22C55E), strings.priceDecreasedLabel)
                    }
                    Icon(
                        imageVector = icon,
                        contentDescription = desc,
                        tint = tint,
                        modifier = Modifier
                            .size(14.dp)
                            .testTag("price_change_icon_${subscription.id}_${dueDate.dayOfMonth}")
                    )
                }
            }
        }
    }
}
