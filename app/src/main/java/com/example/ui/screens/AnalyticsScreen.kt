package com.example.ui.screens

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CATEGORIES
import com.example.data.model.SubscriptionEntity
import com.example.localization.LocalStrings
import com.example.logic.SubscriptionCalculations
import com.example.theme.Brand
import com.example.theme.CardBgElevated
import com.example.theme.TextMuted
import com.example.theme.TextWhite
import com.example.theme.WindowBg
import com.example.ui.components.ArcGauge
import com.example.ui.components.BankCardVisual
import com.example.ui.components.CategoryBarItem
import com.example.ui.components.CategorySpend
import com.example.ui.dialogs.RenameCardDialog
import kotlinx.coroutines.launch

@Composable
fun AnalyticsScreen(
    subscriptions: List<SubscriptionEntity>,
    cardAliases: Map<String, String>,
    primaryCurrency: String,
    onSaveCardAlias: (card: String, alias: String) -> Unit,
    modifier: Modifier = Modifier,
    cardColors: Map<String, String> = emptyMap(),
    onSaveCardColor: (card: String, hex: String) -> Unit = { _, _ -> }
) {
    val strings = LocalStrings.current
    var isCategoryView by remember { mutableStateOf(false) }

    // Cards list (distinct cards from subscriptions + unassigned)
    val cardGroups = remember(subscriptions) {
        val groups = subscriptions.groupBy { it.card ?: "" }
        if (groups.isEmpty()) listOf("" to emptyList()) else groups.toList()
    }
    var renameCardTarget by remember { mutableStateOf<String?>(null) }
    val coroutineScope = rememberCoroutineScope()
    val pagerState = rememberPagerState(pageCount = { cardGroups.size })

    if (renameCardTarget != null) {
        RenameCardDialog(
            cardNumber = renameCardTarget!!,
            currentAlias = cardAliases[renameCardTarget!!],
            onDismiss = { renameCardTarget = null },
            onSave = { newAlias ->
                onSaveCardAlias(renameCardTarget!!, newAlias)
            }
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(WindowBg)
            .padding(horizontal = 16.dp)
            .testTag("analytics_screen")
    ) {
        // Header with Segmented Toggle
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, bottom = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = strings.analyticsTitle,
                color = TextWhite,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )

            // Segmented pill: By Card | By Category
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(CardBgElevated)
                    .padding(3.dp)
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (!isCategoryView) Brand else Color.Transparent)
                        .clickable { isCategoryView = false }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                        .testTag("toggle_by_card"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = strings.byCard,
                        color = if (!isCategoryView) Color.White else TextMuted,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isCategoryView) Brand else Color.Transparent)
                        .clickable { isCategoryView = true }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                        .testTag("toggle_by_category"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = strings.byCategory,
                        color = if (isCategoryView) Color.White else TextMuted,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        if (!isCategoryView) {
            // BY CARD VIEW
            if (cardGroups.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 80.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(strings.noCardsOrSubs, color = TextMuted)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 100.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Card Carousel Controls & Stepper
                    if (cardGroups.size > 1) {
                        item {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                IconButton(
                                    onClick = {
                                        coroutineScope.launch {
                                            val targetPage = if (pagerState.currentPage > 0) {
                                                pagerState.currentPage - 1
                                            } else {
                                                cardGroups.size - 1
                                            }
                                            pagerState.animateScrollToPage(targetPage)
                                        }
                                    },
                                    modifier = Modifier.testTag("prev_card_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                        contentDescription = "Prev Card",
                                        tint = TextWhite
                                    )
                                }

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text(
                                        text = strings.cardStepper(pagerState.currentPage + 1, cardGroups.size),
                                        color = TextMuted,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                    // Subtle indicator dots
                                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                        repeat(cardGroups.size) { idx ->
                                            val isCurrent = idx == pagerState.currentPage
                                            Box(
                                                modifier = Modifier
                                                    .size(if (isCurrent) 6.dp else 4.dp)
                                                    .clip(CircleShape)
                                                    .background(if (isCurrent) Brand else TextMuted.copy(alpha = 0.4f))
                                            )
                                        }
                                    }
                                }

                                IconButton(
                                    onClick = {
                                        coroutineScope.launch {
                                            val targetPage = if (pagerState.currentPage < cardGroups.size - 1) {
                                                pagerState.currentPage + 1
                                            } else {
                                                0
                                            }
                                            pagerState.animateScrollToPage(targetPage)
                                        }
                                    },
                                    modifier = Modifier.testTag("next_card_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                        contentDescription = "Next Card",
                                        tint = TextWhite
                                    )
                                }
                            }
                        }
                    } else {
                        item { Spacer(modifier = Modifier.height(10.dp)) }
                    }

                    // Swipable Horizontal Pager for Cards
                    item {
                        HorizontalPager(
                            state = pagerState,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("card_horizontal_pager")
                        ) { page ->
                            val safePage = page.coerceIn(0, cardGroups.size - 1)
                            val (cardNumber, cardSubs) = cardGroups[safePage]
                            val yearlySpend = cardSubs.sumOf { SubscriptionCalculations.annualCost(it) }
                            val alias = cardAliases[cardNumber]
                            val savedColorHex = cardColors[cardNumber]
                            val customColor = savedColorHex?.let { hex ->
                                try {
                                    Color(android.graphics.Color.parseColor(hex))
                                } catch (_: Exception) {
                                    null
                                }
                            }

                            BankCardVisual(
                                cardNumber = cardNumber,
                                alias = alias,
                                yearlySpend = yearlySpend,
                                subscriptions = cardSubs,
                                primaryCurrency = primaryCurrency,
                                onRenameClick = { renameCardTarget = cardNumber },
                                customColor = customColor,
                                onSelectColor = { pickedColor ->
                                    val hex = String.format("#%06X", 0xFFFFFF and pickedColor.toArgb())
                                    onSaveCardColor(cardNumber, hex)
                                }
                            )
                        }
                    }
                }
            }
        } else {
            // BY CATEGORY VIEW
            val totalAnnualSpend = remember(subscriptions) {
                subscriptions.sumOf { SubscriptionCalculations.annualCost(it) }
            }

            val categorySpends = remember(subscriptions, totalAnnualSpend) {
                val map = mutableMapOf<String, Double>()
                CATEGORIES.forEach { map[it] = 0.0 }
                subscriptions.forEach { sub ->
                    val cat = sub.category ?: "Other"
                    val cost = SubscriptionCalculations.annualCost(sub)
                    map[cat] = (map[cat] ?: 0.0) + cost
                }
                map.map { (cat, amount) ->
                    val pct = if (totalAnnualSpend > 0) (amount / totalAnnualSpend).toFloat() else 0f
                    CategorySpend(cat, amount, pct)
                }.sortedByDescending { it.annualAmount }
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 100.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    ArcGauge(
                        categorySpends = categorySpends.filter { it.annualAmount > 0 },
                        totalSpend = totalAnnualSpend,
                        primaryCurrency = primaryCurrency
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = strings.totalAnnualSpend,
                        color = TextMuted,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }

                items(categorySpends) { catSpend ->
                    CategoryBarItem(
                        categorySpend = catSpend,
                        primaryCurrency = primaryCurrency
                    )
                }
            }
        }
    }
}
