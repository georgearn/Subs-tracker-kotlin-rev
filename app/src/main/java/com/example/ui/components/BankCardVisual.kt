package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.SubscriptionEntity
import com.example.localization.LocalStrings
import com.example.theme.CardBg
import com.example.theme.CardBgElevated
import com.example.theme.TextMuted
import com.example.theme.TextWhite
import com.example.theme.getMetallicColor

val CardColorPresets = listOf(
    Color(0xFF545761), // Gunmetal
    Color(0xFF8E929E), // Silver
    Color(0xFF202129), // Obsidian
    Color(0xFFA89255), // Champagne Gold
    Color(0xFF1B4965), // Sapphire Navy
    Color(0xFF1E6B48), // Emerald Green
    Color(0xFF6B3BA7), // Imperial Purple
    Color(0xFF8C263E), // Crimson Wine
    Color(0xFF8B5A5A), // Rose Bronze
    Color(0xFF246B7B)  // Nordic Teal
)

@Composable
fun BankCardVisual(
    cardNumber: String,
    alias: String?,
    yearlySpend: Double,
    subscriptions: List<SubscriptionEntity>,
    primaryCurrency: String,
    onRenameClick: () -> Unit,
    modifier: Modifier = Modifier,
    customColor: Color? = null,
    onSelectColor: ((Color) -> Unit)? = null
) {
    val strings = LocalStrings.current
    val effectiveColor = customColor ?: getMetallicColor(cardNumber)
    val displayAlias = if (!alias.isNullOrBlank()) alias else if (cardNumber.isNotBlank()) "Card •••• $cardNumber" else strings.unlinkedCard
    val formattedCardNumber = if (cardNumber.isNotBlank()) "•••• •••• •••• $cardNumber" else strings.noCardAssigned

    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("bank_card_${cardNumber.ifEmpty { "unassigned" }}"),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Stack container for physical wallet depth
        Box(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .height(210.dp),
            contentAlignment = Alignment.Center
        ) {
            // Background peek card 2
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.86f)
                    .height(180.dp)
                    .offset(y = (-14).dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(Color(0xFF141419))
            )

            // Background peek card 1
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.92f)
                    .height(185.dp)
                    .offset(y = (-7).dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(Color(0xFF1E1E26))
            )

            // Main Active Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(195.dp)
                    .shadow(12.dp, RoundedCornerShape(18.dp))
                    .clip(RoundedCornerShape(18.dp))
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                effectiveColor.copy(alpha = 0.95f),
                                effectiveColor.copy(alpha = 0.65f),
                                Color(0xFF1B1B22)
                            )
                        )
                    )
                    .padding(20.dp)
            ) {
                Column(
                    modifier = Modifier.matchParentSize(),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    // Header: Alias (editable) & Overlapping Circles Logo
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { onRenameClick() }
                                .padding(horizontal = 4.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = displayAlias,
                                color = Color.White,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = strings.renameCard,
                                tint = Color.White.copy(alpha = 0.7f),
                                modifier = Modifier.size(14.dp)
                            )
                        }

                        // Mastercard style overlapping circles
                        Box(modifier = Modifier.size(34.dp, 20.dp)) {
                            Box(
                                modifier = Modifier
                                    .size(20.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFEB001B).copy(alpha = 0.85f))
                            )
                            Box(
                                modifier = Modifier
                                    .size(20.dp)
                                    .offset(x = 14.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFF79E1B).copy(alpha = 0.85f))
                            )
                        }
                    }

                    // Gold EMV Chip Graphic
                    Box(
                        modifier = Modifier
                            .size(38.dp, 28.dp)
                            .clip(RoundedCornerShape(5.dp))
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(
                                        Color(0xFFE5C158),
                                        Color(0xFFB8860B),
                                        Color(0xFFD4AF37)
                                    )
                                )
                            )
                    ) {
                        // Subtle chip contact lines
                        Box(
                            modifier = Modifier
                                .width(1.dp)
                                .height(28.dp)
                                .offset(x = 18.dp)
                                .background(Color.Black.copy(alpha = 0.25f))
                        )
                        Box(
                            modifier = Modifier
                                .width(38.dp)
                                .height(1.dp)
                                .offset(y = 14.dp)
                                .background(Color.Black.copy(alpha = 0.25f))
                        )
                    }

                    // Card Number and Yearly Total Footer
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Column {
                            Text(
                                text = formattedCardNumber,
                                color = Color.White.copy(alpha = 0.95f),
                                fontSize = 15.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Medium,
                                letterSpacing = 1.sp
                            )
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = strings.annualSpend,
                                color = Color.White.copy(alpha = 0.7f),
                                fontSize = 11.sp
                            )
                            Text(
                                text = "${"%.2f".format(yearlySpend)} $primaryCurrency",
                                color = Color.White,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // Quick Color Picker
        if (onSelectColor != null) {
            Spacer(modifier = Modifier.height(14.dp))
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(CardBg)
                    .padding(14.dp)
            ) {
                Text(
                    text = strings.cardColorTitle,
                    color = TextMuted,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CardColorPresets.forEach { presetColor ->
                        val isSelected = presetColor.value == effectiveColor.value
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(presetColor)
                                .border(
                                    width = if (isSelected) 2.5.dp else 1.dp,
                                    color = if (isSelected) Color.White else Color.White.copy(alpha = 0.2f),
                                    shape = CircleShape
                                )
                                .clickable { onSelectColor(presetColor) }
                                .testTag("color_preset_${presetColor.value}"),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Selected",
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Subscriptions linked to this card
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(CardBg)
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(effectiveColor)
                )
                Text(
                    text = strings.linkedSubsCount(subscriptions.size),
                    color = TextWhite,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (subscriptions.isEmpty()) {
                Text(
                    text = strings.noSubsLinked,
                    color = TextMuted,
                    fontSize = 13.sp
                )
            } else {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    subscriptions.forEach { sub ->
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.width(64.dp)
                        ) {
                            ServiceIcon(
                                name = sub.name,
                                category = sub.category,
                                size = 42.dp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = sub.name,
                                color = TextMuted,
                                fontSize = 11.sp,
                                maxLines = 1,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }
    }
}
