package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.SubscriptionEntity
import com.example.localization.LocalStrings
import com.example.theme.Brand
import com.example.theme.CardBg
import com.example.theme.CardBgElevated
import com.example.theme.TextMuted
import com.example.theme.TextWhite
import com.example.theme.getCategoryColor
import com.example.ui.glass.flatSurface
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SubscriptionRow(
    subscription: SubscriptionEntity,
    primaryCurrency: String,
    onClick: () -> Unit,
    onLongClick: () -> Unit = {},
    onQuickDelete: () -> Unit = {},
    isRevealed: Boolean = false,
    onRevealChange: (Boolean) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val strings = LocalStrings.current
    val density = LocalDensity.current
    val haptics = LocalHapticFeedback.current
    val scope = rememberCoroutineScope()

    val maxRevealDp = 80.dp
    val maxRevealPx = with(density) { maxRevealDp.toPx() }
    val offsetX = remember { Animatable(0f) }

    // Synchronize animation with external reveal state
    LaunchedEffect(isRevealed) {
        val target = if (isRevealed) -maxRevealPx else 0f
        if (offsetX.value != target) {
            offsetX.animateTo(
                targetValue = target,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioNoBouncy,
                    stiffness = Spring.StiffnessMediumLow
                )
            )
        }
    }

    val draggableState = rememberDraggableState { delta ->
        scope.launch {
            val current = offsetX.value
            // Apply resistance when swiping left (revealing deletion) so accidental horizontal
            // drifts during vertical scrolling do not easily displace the card or reveal delete
            val effectiveDelta = if (delta < 0f) {
                delta * 0.65f
            } else {
                delta
            }
            val newOffset = (current + effectiveDelta).coerceIn(-maxRevealPx * 1.15f, 0f)
            offsetX.snapTo(newOffset)
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .testTag("subscription_row_container_${subscription.id}")
    ) {
        // Background Actions Layer (revealed on swipe left - Delete only)
        Row(
            modifier = Modifier
                .matchParentSize()
                // Glass is translucent, so keep the red action hidden until the card slides.
                .graphicsLayer { alpha = (-offsetX.value / maxRevealPx).coerceIn(0f, 1f) },
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Quick Delete Button
            Box(
                modifier = Modifier
                    .width(80.dp)
                    .fillMaxHeight()
                    .background(Color(0xFFDC2626))
                    .clickable {
                        onQuickDelete()
                    }
                    .testTag("quick_delete_btn_${subscription.id}"),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = strings.quickDelete,
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = strings.quickDelete,
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Foreground Card Layer
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .offset { IntOffset(offsetX.value.roundToInt(), 0) }
                .draggable(
                    state = draggableState,
                    orientation = Orientation.Horizontal,
                    onDragStopped = { velocity ->
                        scope.launch {
                            // Require a deliberate swipe distance (at least 60% of reveal area) or intentional flick
                            val shouldReveal = if (isRevealed) {
                                velocity < 350f && offsetX.value < -maxRevealPx * 0.35f
                            } else {
                                velocity < -750f || offsetX.value < -maxRevealPx * 0.60f
                            }
                            if (shouldReveal) {
                                if (!isRevealed) {
                                    haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                                }
                                onRevealChange(true)
                                offsetX.animateTo(
                                    targetValue = -maxRevealPx,
                                    animationSpec = spring(
                                        dampingRatio = Spring.DampingRatioNoBouncy,
                                        stiffness = Spring.StiffnessMediumLow
                                    )
                                )
                            } else {
                                onRevealChange(false)
                                offsetX.animateTo(
                                    targetValue = 0f,
                                    animationSpec = spring(
                                        dampingRatio = Spring.DampingRatioNoBouncy,
                                        stiffness = Spring.StiffnessMediumLow
                                    )
                                )
                            }
                        }
                    }
                )
                .flatSurface(RoundedCornerShape(16.dp), CardBg, alpha = 0.85f)
                .clip(RoundedCornerShape(16.dp))
                .combinedClickable(
                    onClick = {
                        if (isRevealed || offsetX.value < -10f) {
                            onRevealChange(false)
                        } else {
                            onClick()
                        }
                    },
                    onLongClick = {
                        haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                        onLongClick()
                    }
                )
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
}
