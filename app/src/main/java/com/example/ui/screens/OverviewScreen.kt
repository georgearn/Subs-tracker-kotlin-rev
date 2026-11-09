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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.SubscriptionEntity
import com.example.localization.LocalStrings
import com.example.logic.SubscriptionCalculations
import com.example.theme.Brand
import com.example.theme.CardBg
import com.example.theme.CardBgElevated
import com.example.theme.TextMuted
import com.example.theme.TextWhite
import com.example.ui.components.DayCell
import com.example.ui.components.MonthSubCard
import com.example.ui.components.MonthTile
import com.example.ui.components.MonthTotal
import com.example.ui.components.PriceChangeDirection
import com.example.ui.components.YearStepButton
import com.example.ui.components.YearSummaryCard
import com.example.ui.dialogs.MonthPickerModal
import com.example.ui.glass.LocalBottomBarInset
import com.example.ui.glass.glassControl
import com.example.ui.glass.glassSurface
import kotlinx.coroutines.flow.first
import java.time.LocalDate
import java.time.YearMonth

/** Today when [year]/[month] is the month being shown, otherwise no day is pre-selected. */
private fun defaultDayFor(year: Int, month: Int, today: LocalDate): LocalDate? =
    if (year == today.year && month == today.monthValue) today else null

@Composable
fun OverviewScreen(
    subscriptions: List<SubscriptionEntity>,
    primaryCurrency: String,
    onSubscriptionClick: (SubscriptionEntity) -> Unit,
    modifier: Modifier = Modifier,
    listState: LazyListState = rememberLazyListState()
) {
    val strings = LocalStrings.current
    var isYearlyView by remember { mutableStateOf(false) }
    val today = remember { LocalDate.now() }

    var selectedYear by remember { mutableIntStateOf(today.year) }
    var selectedMonth by remember { mutableIntStateOf(today.monthValue) }
    var selectedDay by remember { mutableStateOf<LocalDate?>(today) }
    var showMonthPicker by remember { mutableStateOf(false) }

    if (showMonthPicker) {
        MonthPickerModal(
            currentYear = selectedYear,
            currentMonth = selectedMonth,
            onDismiss = { showMonthPicker = false },
            onSelectMonthYear = { y, m ->
                selectedYear = y
                selectedMonth = m
                selectedDay = defaultDayFor(y, m, today)
            }
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .testTag("overview_screen")
    ) {
        // Top Header & View Toggle (Monthly / Yearly)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, bottom = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = strings.overviewTitle,
                color = TextWhite,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )

            // Segmented pill: Monthly | Yearly
            Row(
                modifier = Modifier
                    .glassControl(RoundedCornerShape(12.dp), CardBgElevated)
                    .clip(RoundedCornerShape(12.dp))
                    .padding(3.dp)
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (!isYearlyView) Brand else Color.Transparent)
                        .clickable { isYearlyView = false }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                        .testTag("toggle_monthly"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = strings.viewMonthly,
                        color = if (!isYearlyView) Color.White else TextMuted,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isYearlyView) Brand else Color.Transparent)
                        .clickable { isYearlyView = true }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                        .testTag("toggle_yearly"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = strings.viewYearly,
                        color = if (isYearlyView) Color.White else TextMuted,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        if (!isYearlyView) {
            // MONTHLY VIEW
            MonthlyContentView(
                subscriptions = subscriptions,
                primaryCurrency = primaryCurrency,
                selectedYear = selectedYear,
                selectedMonth = selectedMonth,
                selectedDay = selectedDay,
                today = today,
                onOpenMonthPicker = { showMonthPicker = true },
                onSelectDay = { day ->
                    selectedDay = if (selectedDay == day) null else day
                },
                onSubscriptionClick = onSubscriptionClick,
                listState = listState
            )
        } else {
            // YEARLY VIEW
            YearlyContentView(
                subscriptions = subscriptions,
                primaryCurrency = primaryCurrency,
                selectedYear = selectedYear,
                today = today,
                onYearChange = { selectedYear = it },
                onMonthClick = { month ->
                    selectedMonth = month
                    selectedDay = defaultDayFor(selectedYear, month, today)
                    isYearlyView = false
                },
                listState = listState
            )
        }
    }
}

@Composable
private fun MonthlyContentView(
    subscriptions: List<SubscriptionEntity>,
    primaryCurrency: String,
    selectedYear: Int,
    selectedMonth: Int,
    selectedDay: LocalDate?,
    today: LocalDate,
    onOpenMonthPicker: () -> Unit,
    onSelectDay: (LocalDate) -> Unit,
    onSubscriptionClick: (SubscriptionEntity) -> Unit,
    listState: LazyListState = rememberLazyListState()
) {
    val strings = LocalStrings.current
    val bottomInset = LocalBottomBarInset.current
    val ym = YearMonth.of(selectedYear, selectedMonth)
    val daysInMonth = ym.lengthOfMonth()
    val monthName = strings.getMonthName(selectedMonth, short = false)

    // Calculate occurrences for each sub in this month. priceChange is only set on the one
    // occurrence that is the very first payment under a new price (i.e. its date is exactly
    // this row's startDate, and a previous row for the same subscription ended right there
    // with a different price) — so the flag shows up once, on the month the change took effect.
    data class MonthOccurrence(val sub: SubscriptionEntity, val date: LocalDate, val priceChange: PriceChangeDirection?)
    val allOccurrences = remember(subscriptions, selectedYear, selectedMonth) {
        val list = mutableListOf<MonthOccurrence>()
        subscriptions.forEach { sub ->
            val dates = SubscriptionCalculations.occurrencesInMonth(sub, selectedYear, selectedMonth)
            if (dates.isEmpty()) return@forEach
            val start = SubscriptionCalculations.parseStart(sub)
            val previousEra = subscriptions.firstOrNull { it.name == sub.name && it.endDate == sub.startDate }
            dates.forEach { d ->
                val change = if (previousEra != null && d == start &&
                    kotlin.math.abs(previousEra.priceUsd - sub.priceUsd) > 0.001
                ) {
                    if (sub.priceUsd > previousEra.priceUsd) PriceChangeDirection.INCREASED else PriceChangeDirection.DECREASED
                } else null
                list.add(MonthOccurrence(sub, d, change))
            }
        }
        list.sortedBy { it.date }
    }

    val occurrencesByDate = remember(allOccurrences) {
        allOccurrences.groupBy { it.date }
    }

    // A selected day with nothing due falls back to the payments still to come later in the month.
    val dayOccurrences = if (selectedDay != null) {
        allOccurrences.filter { it.date == selectedDay }
    } else {
        allOccurrences
    }
    val remainingOccurrences = if (selectedDay != null && dayOccurrences.isEmpty()) {
        allOccurrences.filter { it.date > selectedDay }
    } else {
        emptyList()
    }
    val displayedOccurrences = dayOccurrences.ifEmpty { remainingOccurrences }

    val monthTotal = allOccurrences.sumOf { it.sub.priceUsd }
    val todayOccurrences = allOccurrences.filter { it.date == today }

    LazyColumn(
        state = listState,
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = bottomInset),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            // Month-Year Pill Selector & Today status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier
                        .glassControl(RoundedCornerShape(12.dp), CardBgElevated)
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { onOpenMonthPicker() }
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                        .testTag("month_pill_btn"),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.CalendarToday,
                        contentDescription = null,
                        tint = Brand,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = strings.formatMonthYear(selectedYear, selectedMonth),
                        color = TextWhite,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Text(
                    text = if (todayOccurrences.isNotEmpty()) {
                        strings.dueTodayCount(todayOccurrences.size)
                    } else strings.noneDueToday,
                    color = TextMuted,
                    fontSize = 13.sp
                )
            }
        }

        item {
            DayStrip(
                year = selectedYear,
                month = selectedMonth,
                daysInMonth = daysInMonth,
                selectedDay = selectedDay,
                today = today,
                paymentDates = occurrencesByDate.keys,
                onSelectDay = onSelectDay
            )
        }

        item {
            // Subtitle for active filter
            val headerText = when {
                selectedDay == null -> strings.allInMonthHeader(monthName, allOccurrences.size)
                dayOccurrences.isNotEmpty() ->
                    strings.dueOnHeader(strings.formatDateShort(selectedDay), dayOccurrences.size)
                remainingOccurrences.isNotEmpty() ->
                    strings.nothingDueRemaining(strings.formatDateShort(selectedDay), remainingOccurrences.size)
                else -> strings.dueOnHeader(strings.formatDateShort(selectedDay), 0)
            }
            Text(
                text = headerText,
                color = TextMuted,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(start = 4.dp)
            )
        }

        if (displayedOccurrences.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 30.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (selectedDay != null) strings.noPaymentsDay else strings.noPaymentsMonth(monthName),
                        color = TextMuted,
                        fontSize = 14.sp
                    )
                }
            }
        } else {
            // Grid of items: Pair up in rows of 2 for clean 2-column layout
            val chunked = displayedOccurrences.chunked(2)
            items(chunked.size) { index ->
                val pair = chunked[index]
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(modifier = Modifier.weight(1f)) {
                        MonthSubCard(
                            subscription = pair[0].sub,
                            dueDate = pair[0].date,
                            primaryCurrency = primaryCurrency,
                            priceChange = pair[0].priceChange,
                            onClick = { onSubscriptionClick(pair[0].sub) }
                        )
                    }
                    if (pair.size > 1) {
                        Box(modifier = Modifier.weight(1f)) {
                            MonthSubCard(
                                subscription = pair[1].sub,
                                dueDate = pair[1].date,
                                primaryCurrency = primaryCurrency,
                                priceChange = pair[1].priceChange,
                                onClick = { onSubscriptionClick(pair[1].sub) }
                            )
                        }
                    } else {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }

        item {
            // Total Card for Month
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .glassSurface(RoundedCornerShape(16.dp), CardBg)
                    .clip(RoundedCornerShape(16.dp))
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = strings.totalForMonth(monthName),
                        color = TextWhite,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "${"%.2f".format(monthTotal)} $primaryCurrency",
                        color = Brand,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

/**
 * Horizontal strip of day cells. On every month change it waits for the viewport width, then
 * centres today (or day 1 for other months).
 */
@Composable
private fun DayStrip(
    year: Int,
    month: Int,
    daysInMonth: Int,
    selectedDay: LocalDate?,
    today: LocalDate,
    paymentDates: Set<LocalDate>,
    onSelectDay: (LocalDate) -> Unit
) {
    val stripState = rememberLazyListState()
    val density = LocalDensity.current
    val cellWidthPx = with(density) { 52.dp.roundToPx() }

    LaunchedEffect(year, month) {
        val viewportWidth = snapshotFlow { stripState.layoutInfo.viewportSize.width }.first { it > 0 }
        val index = if (year == today.year && month == today.monthValue) today.dayOfMonth - 1 else 0
        stripState.scrollToItem(index, scrollOffset = -(viewportWidth - cellWidthPx) / 2)
    }

    LazyRow(
        state = stripState,
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(daysInMonth) { i ->
            val date = LocalDate.of(year, month, i + 1)
            DayCell(
                date = date,
                isSelected = selectedDay == date,
                isToday = date == today,
                hasPayments = date in paymentDates,
                onClick = { onSelectDay(date) }
            )
        }
    }
}

@Composable
private fun YearlyContentView(
    subscriptions: List<SubscriptionEntity>,
    primaryCurrency: String,
    selectedYear: Int,
    today: LocalDate,
    onYearChange: (Int) -> Unit,
    onMonthClick: (Int) -> Unit,
    listState: LazyListState = rememberLazyListState()
) {
    val bottomInset = LocalBottomBarInset.current

    val totals = remember(subscriptions, selectedYear) {
        (1..12).map { month ->
            var count = 0
            var total = 0.0
            subscriptions.forEach { sub ->
                val dates = SubscriptionCalculations.occurrencesInMonth(sub, selectedYear, month)
                count += dates.size
                total += sub.priceUsd * dates.size
            }
            MonthTotal(month, total, count)
        }
    }
    val maxTotal = totals.maxOf { it.total }
    val currentMonth = if (selectedYear == today.year) today.monthValue else null

    LazyColumn(
        state = listState,
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = bottomInset),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            // Year Navigation
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                YearStepButton(
                    icon = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Prev",
                    onClick = { onYearChange(selectedYear - 1) }
                )
                Text(
                    text = selectedYear.toString(),
                    color = TextWhite,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
                YearStepButton(
                    icon = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = "Next",
                    onClick = { onYearChange(selectedYear + 1) }
                )
            }
        }

        item {
            YearSummaryCard(
                year = selectedYear,
                totals = totals,
                currentMonth = currentMonth,
                currency = primaryCurrency
            )
        }

        items(totals.chunked(3)) { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                row.forEach { entry ->
                    MonthTile(
                        entry = entry,
                        maxTotal = maxTotal,
                        isCurrent = entry.month == currentMonth,
                        onClick = { onMonthClick(entry.month) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}
