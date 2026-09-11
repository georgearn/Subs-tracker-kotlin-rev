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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ImportExport
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.example.theme.CardBgElevated
import com.example.theme.TextMuted
import com.example.theme.TextWhite
import com.example.theme.WindowBg
import com.example.ui.components.HeroCard
import com.example.ui.components.SubscriptionRow

@Composable
fun SubscriptionsScreen(
    subscriptions: List<SubscriptionEntity>,
    primaryCurrency: String,
    currentMonthTotal: Double,
    onOpenImportExport: () -> Unit,
    onSubscriptionClick: (SubscriptionEntity) -> Unit,
    modifier: Modifier = Modifier,
    listState: LazyListState = rememberLazyListState()
) {
    val strings = LocalStrings.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(WindowBg)
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
                    .clip(RoundedCornerShape(12.dp))
                    .background(CardBgElevated)
                    .size(36.dp)
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

        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 100.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item {
                HeroCard(
                    monthTotal = currentMonthTotal,
                    activeCount = subscriptions.size,
                    currency = primaryCurrency
                )
                Spacer(modifier = Modifier.height(14.dp))
                Text(
                    text = strings.activeSubscriptionsHeader(subscriptions.size),
                    color = TextMuted,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(start = 4.dp, bottom = 4.dp)
                )
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
                        subscription = sub,
                        primaryCurrency = primaryCurrency,
                        onClick = { onSubscriptionClick(sub) }
                    )
                }
            }
        }
    }
}
