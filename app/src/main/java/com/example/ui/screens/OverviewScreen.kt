package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import com.example.logic.SubscriptionCalculations
import com.example.theme.Brand
import com.example.theme.CardBg
import com.example.theme.CardBgElevated
import com.example.theme.TextMuted
import com.example.theme.TextWhite
import com.example.theme.WindowBg
import com.example.ui.components.DayCell
import com.example.ui.components.MonthSubCard
import com.example.ui.dialogs.MonthPickerModal
import java.time.LocalDate
import java.time.Month
import java.time.YearMonth

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
    var selectedDay by remember { mutableStateOf<LocalDate?>(null) }
    var showMonthPicker by remember { mutableStateOf(false) }

    if (showMonthPicker) {
        MonthPickerModal(
            currentYear = selectedYear,
            currentMonth = selectedMonth,
            onDismiss = { showMonthPicker = false },
            onSelectMonthYear = { y, m ->
                selectedYear = y
                selectedMonth = m
                selectedDay = null
            }
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(WindowBg)
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
                    .clip(RoundedCornerShape(12.dp))
                    .background(CardBgElevated)
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
    val ym = YearMonth.of(selectedYear, selectedMonth)
    val daysInMonth = ym.lengthOfMonth()
    val monthName = strings.getMonthName(selectedMonth, short = false)

    // Calculate occurrences for each sub in this month
    data class MonthOccurrence(val sub: SubscriptionEntity, val date: LocalDate)
    val allOccurrences = remember(subscriptions, selectedYear, selectedMonth) {
        val list = mutableListOf<MonthOccurrence>()
        subscriptions.forEach { sub ->
            val dates = SubscriptionCalculations.occurrencesInMonth(sub, selectedYear, selectedMonth)
            dates.forEach { d -> list.add(MonthOccurrence(sub, d)) }
        }
        list.sortedBy { it.date }
    }

    val occurrencesByDate = remember(allOccurrences) {
        allOccurrences.groupBy { it.date }
    }

    val displayedOccurrences = if (selectedDay != null) {
        allOccurrences.filter { it.date == selectedDay }
    } else {
        allOccurrences
    }

    val monthTotal = allOccurrences.sumOf { it.sub.priceUsd }
    val todayOccurrences = allOccurrences.filter { it.date == today }

    LazyColumn(
        state = listState,
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 90.dp),
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
                        .clip(RoundedCornerShape(12.dp))
                        .background(CardBgElevated)
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
            // Horizontal Day Strip
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                for (day in 1..daysInMonth) {
                    val date = LocalDate.of(selectedYear, selectedMonth, day)
                    val isSelected = selectedDay == date
                    val isToday = (date == today)
                    val hasPayments = (occurrencesByDate[date]?.isNotEmpty() == true)

                    DayCell(
                        date = date,
                        isSelected = isSelected,
                        isToday = isToday,
                        hasPayments = hasPayments,
                        onClick = { onSelectDay(date) }
                    )
                }
            }
        }

        item {
            // Subtitle for active filter
            val headerText = if (selectedDay != null) {
                val dateStr = strings.formatDateShort(selectedDay)
                strings.dueOnHeader(dateStr, displayedOccurrences.size)
            } else {
                strings.allInMonthHeader(monthName, allOccurrences.size)
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
                            onClick = { onSubscriptionClick(pair[0].sub) }
                        )
                    }
                    if (pair.size > 1) {
                        Box(modifier = Modifier.weight(1f)) {
                            MonthSubCard(
                                subscription = pair[1].sub,
                                dueDate = pair[1].date,
                                primaryCurrency = primaryCurrency,
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
                    .clip(RoundedCornerShape(16.dp))
                    .background(CardBg)
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

@Composable
private fun YearlyContentView(
    subscriptions: List<SubscriptionEntity>,
    primaryCurrency: String,
    selectedYear: Int,
    today: LocalDate,
    onYearChange: (Int) -> Unit,
    listState: LazyListState = rememberLazyListState()
) {
    val strings = LocalStrings.current
    val months = Month.values()
    var yearTotal = 0.0

    val monthSummaries = months.map { month ->
        var count = 0
        var total = 0.0
        subscriptions.forEach { sub ->
            val dates = SubscriptionCalculations.occurrencesInMonth(sub, selectedYear, month.value)
            count += dates.size
            total += sub.priceUsd * dates.size
        }
        yearTotal += total
        Triple(month, count, total)
    }

    LazyColumn(
        state = listState,
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 90.dp),
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
                IconButton(onClick = { onYearChange(selectedYear - 1) }) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Prev", tint = TextWhite)
                }
                Text(
                    text = selectedYear.toString(),
                    color = TextWhite,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                IconButton(onClick = { onYearChange(selectedYear + 1) }) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Next", tint = TextWhite)
                }
            }
        }

        item {
            // Year Total Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(CardBgElevated)
                    .padding(18.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(text = strings.yearTotal(selectedYear), color = TextMuted, fontSize = 13.sp)
                        Text(
                            text = "${"%.2f".format(yearTotal)} $primaryCurrency",
                            color = TextWhite,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Text(
                        text = strings.twelveMonths,
                        color = Brand,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
            Spacer(modifier = Modifier.height(6.dp))
        }

        items(monthSummaries.size) { i ->
            val (month, count, total) = monthSummaries[i]
            val isCurrent = (selectedYear == today.year && month.value == today.monthValue)
            val monthName = strings.getMonthName(month.value, short = false)

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(if (isCurrent) CardBgElevated else CardBg)
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = monthName,
                            color = if (isCurrent) Brand else TextWhite,
                            fontSize = 15.sp,
                            fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Medium
                        )
                        if (isCurrent) {
                            Text(
                                text = " (${strings.currentMonthBadge})",
                                color = Brand,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = strings.paymentsCount(count),
                            color = TextMuted,
                            fontSize = 12.sp
                        )
                        Text(
                            text = "${"%.2f".format(total)} $primaryCurrency",
                            color = TextWhite,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
