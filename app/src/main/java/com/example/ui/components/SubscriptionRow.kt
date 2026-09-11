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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.example.theme.CardBg
import com.example.theme.TextMuted
import com.example.theme.TextWhite
import com.example.theme.getCategoryColor

@Composable
fun SubscriptionRow(
    subscription: SubscriptionEntity,
    primaryCurrency: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(CardBg)
            .clickable { onClick() }
            .padding(14.dp)
            .testTag("subscription_row_${subscription.id}")
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ServiceIcon(
                name = subscription.name,
                category = subscription.category,
                size = 46.dp
            )

            Spacer(modifier = Modifier.width(14.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = subscription.name,
                    color = TextWhite,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(3.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    val catColor = getCategoryColor(subscription.category)
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(catColor.copy(alpha = 0.2f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = subscription.category ?: "Other",
                            color = catColor,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    val freqLabel = when (subscription.frequency.lowercase()) {
                        "monthly" -> "Monthly"
                        "quarterly" -> "Quarterly"
                        "yearly" -> "Yearly"
                        "custom" -> "Every ${subscription.intervalDays ?: 0}d"
                        else -> subscription.frequency
                    }
                    Text(
                        text = freqLabel,
                        color = TextMuted,
                        fontSize = 12.sp
                    )

                    if (!subscription.card.isNullOrBlank()) {
                        Text(
                            text = "• •••• ${subscription.card}",
                            color = TextMuted,
                            fontSize = 12.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = "${"%.2f".format(subscription.priceUsd)} $primaryCurrency",
                    color = TextWhite,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )

                if (subscription.secondaryAmount != null && !subscription.secondaryCurrency.isNullOrBlank()) {
                    Text(
                        text = "${"%.2f".format(subscription.secondaryAmount)} ${subscription.secondaryCurrency}",
                        color = TextMuted,
                        fontSize = 11.sp
                    )
                }
            }
        }
    }
}
