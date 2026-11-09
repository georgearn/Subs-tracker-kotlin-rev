package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ImportExport
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.SubscriptionEntity
import com.example.localization.LocalStrings
import com.example.theme.Brand
import com.example.theme.CardBg
import com.example.ui.glass.DialogGlassBorder
import com.example.ui.glass.dialogGlass
import com.example.theme.CardBgElevated
import com.example.theme.TextMuted
import com.example.theme.TextWhite
import com.example.theme.WindowBg
import com.example.theme.getCategoryColor
import com.example.ui.components.HeroCard
import com.example.ui.components.ServiceIcon
import com.example.ui.components.SubscriptionRow
import com.example.ui.glass.LocalBottomBarInset
import com.example.ui.glass.glassControl
import com.example.ui.glass.glassSurface
import com.example.ui.glass.flatSurface

@Composable
fun SubscriptionsScreen(
    subscriptions: List<SubscriptionEntity>,
    primaryCurrency: String,
    currentMonthTotal: Double,
    onOpenImportExport: () -> Unit,
    onSubscriptionClick: (SubscriptionEntity) -> Unit,
    // Deleting an active subscription takes the full entity — the caller uses it to also
    // clear out that subscription's price-history rows, not just this one row.
    onDeleteSubscription: (SubscriptionEntity) -> Unit = {},
    // History rows are pruned individually: deleting one doesn't touch its siblings.
    onDeleteHistoryEntry: (Int) -> Unit = {},
    historySubscriptions: List<SubscriptionEntity> = emptyList(),
    modifier: Modifier = Modifier,
    listState: LazyListState = rememberLazyListState()
) {
    val strings = LocalStrings.current
    val bottomInset = LocalBottomBarInset.current

    // 0 = Active subscriptions, 1 = Price history (closed-out rows from past price changes)
    var selectedTab by remember { mutableStateOf(0) }

    // State for swiped row ID (only one row swiped open at a time)
    var swipedSubscriptionId by remember { mutableStateOf<Int?>(null) }

    // State for pending deletion
    var pendingDeleteSub by remember { mutableStateOf<SubscriptionEntity?>(null) }

    // Close swiped row on back press if a row is currently open
    BackHandler(enabled = swipedSubscriptionId != null && pendingDeleteSub == null) {
        swipedSubscriptionId = null
    }

    // Quick Delete Confirmation Dialog
    if (pendingDeleteSub != null) {
        val subToDelete = pendingDeleteSub!!
        AlertDialog(
            onDismissRequest = { pendingDeleteSub = null },
            containerColor = CardBgElevated.dialogGlass(),
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = null,
                        tint = Color(0xFFEF4444),
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = strings.deleteSubscriptionTitle,
                        color = TextWhite,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                }
            },
            text = {
                Text(
                    text = strings.deleteConfirmMessage(subToDelete.name),
                    color = TextMuted,
                    fontSize = 14.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        onDeleteSubscription(subToDelete)
                        pendingDeleteSub = null
                        swipedSubscriptionId = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626)),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.testTag("confirm_delete_btn")
                ) {
                    Text(
                        text = strings.deleteAction,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { pendingDeleteSub = null },
                    modifier = Modifier.testTag("cancel_delete_btn")
                ) {
                    Text(text = strings.cancelAction, color = TextMuted)
                }
            }
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .testTag("subscriptions_screen")
    ) {
        // Top Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, bottom = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = strings.subscriptionsTitle,
                color = TextWhite,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )

            // Import / Export button
            IconButton(
                onClick = onOpenImportExport,
                modifier = Modifier
                    .size(36.dp)
                    .glassControl(RoundedCornerShape(12.dp), CardBgElevated)
                    .clip(RoundedCornerShape(12.dp))
                    .testTag("import_export_btn")
            ) {
                Icon(
                    imageVector = Icons.Default.ImportExport,
                    contentDescription = strings.importExport,
                    tint = TextWhite,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        // Active / History Segmented Control Pills
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .glassControl(RoundedCornerShape(14.dp), CardBgElevated)
                .clip(RoundedCornerShape(14.dp))
                .padding(4.dp)
                .testTag("subs_history_tabs")
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (selectedTab == 0) Brand else Color.Transparent)
                    .clickable { selectedTab = 0 }
                    .padding(vertical = 10.dp)
                    .testTag("subs_tab_active"),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = strings.activeTabLabel,
                    color = if (selectedTab == 0) Color.White else TextMuted,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (selectedTab == 1) Brand else Color.Transparent)
                    .clickable { selectedTab = 1 }
                    .padding(vertical = 10.dp)
                    .testTag("subs_tab_history"),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = strings.historyTabLabel,
                    color = if (selectedTab == 1) Color.White else TextMuted,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        if (selectedTab == 0) {
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(top = 4.dp, bottom = bottomInset),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    HeroCard(
                        monthTotal = currentMonthTotal,
                        activeCount = subscriptions.size,
                        currency = primaryCurrency
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = strings.activeSubscriptionsHeader(subscriptions.size),
                            color = TextMuted,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(start = 4.dp, bottom = 4.dp)
                        )
                        if (subscriptions.isNotEmpty()) {
                            Text(
                                text = strings.swipeHint,
                                color = TextMuted.copy(alpha = 0.7f),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Normal,
                                modifier = Modifier.padding(end = 4.dp, bottom = 4.dp)
                            )
                        }
                    }
                }

                if (subscriptions.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 40.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = strings.emptySubsTitle,
                                    color = TextWhite,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = strings.emptySubsSubtitle,
                                    color = TextMuted,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }
                } else {
                    items(subscriptions, key = { it.id }) { sub ->
                        SubscriptionRow(
                            modifier = Modifier.animateItem(),
                            subscription = sub,
                            primaryCurrency = primaryCurrency,
                            isRevealed = swipedSubscriptionId == sub.id,
                            onRevealChange = { revealed ->
                                swipedSubscriptionId = if (revealed) sub.id else null
                            },
                            onClick = {
                                if (swipedSubscriptionId != null) {
                                    swipedSubscriptionId = null
                                } else {
                                    onSubscriptionClick(sub)
                                }
                            },
                            onLongClick = {
                                swipedSubscriptionId = sub.id
                                pendingDeleteSub = sub
                            },
                            onQuickDelete = {
                                pendingDeleteSub = sub
                            }
                        )
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(top = 4.dp, bottom = bottomInset),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (historySubscriptions.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 40.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = strings.emptyHistoryTitle,
                                    color = TextWhite,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = strings.emptyHistorySubtitle,
                                    color = TextMuted,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }
                } else {
                    items(historySubscriptions, key = { it.id }) { sub ->
                        val wasCancelled = (subscriptions + historySubscriptions).none {
                            it.id != sub.id && it.name == sub.name && it.startDate == sub.endDate
                        }
                        HistoryRow(
                            subscription = sub,
                            primaryCurrency = primaryCurrency,
                            wasCancelled = wasCancelled,
                            onDelete = { onDeleteHistoryEntry(sub.id) }
                        )
                    }
                }
            }
        }
    }
}

/**
 * A single frozen price-era row: a subscription's price as it was between [startDate] and
 * [endDate] before an edit moved it to a new price. Read-only — no swipe-to-edit, since editing
 * a closed-out row would fork history again — but deletable to let users prune old entries.
 */
@Composable
private fun HistoryRow(
    subscription: SubscriptionEntity,
    primaryCurrency: String,
    onDelete: () -> Unit,
    wasCancelled: Boolean = false,
    modifier: Modifier = Modifier
) {
    val strings = LocalStrings.current
    val locale = remember(strings.language.code) { java.util.Locale.forLanguageTag(strings.language.code) }
    val formatter = remember(locale) {
        java.time.format.DateTimeFormatter.ofPattern("dd MMM yyyy", locale)
    }
    val startStr = remember(subscription.startDate, formatter) {
        try {
            java.time.LocalDate.parse(subscription.startDate).format(formatter)
        } catch (e: Exception) {
            subscription.startDate
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .flatSurface(RoundedCornerShape(16.dp), CardBgElevated.copy(alpha = 0.7f))
            .clip(RoundedCornerShape(16.dp))
            .padding(14.dp)
            .testTag("history_row_${subscription.id}")
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ServiceIcon(
                name = subscription.name,
                category = subscription.category,
                size = 44.dp
            )

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = subscription.name,
                    color = TextWhite,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = startStr,
                    color = TextMuted,
                    fontSize = 12.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    val catColor = getCategoryColor(subscription.category)
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(catColor.copy(alpha = 0.16f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = subscription.category ?: "Other",
                            color = catColor,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    if (wasCancelled) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0xFFDC2626).copy(alpha = 0.16f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                .testTag("cancelled_badge_${subscription.id}")
                        ) {
                            Text(
                                text = strings.cancelledLabel,
                                color = Color(0xFFEF4444),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.width(10.dp))

            Text(
                text = "${"%.2f".format(subscription.priceUsd)} $primaryCurrency",
                color = TextMuted,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )

            IconButton(
                onClick = onDelete,
                modifier = Modifier
                    .size(40.dp)
                    .testTag("delete_history_btn_${subscription.id}")
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = strings.deleteAction,
                    tint = TextMuted,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}
