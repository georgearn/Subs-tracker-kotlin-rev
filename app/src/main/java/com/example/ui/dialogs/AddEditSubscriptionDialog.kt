package com.example.ui.dialogs

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.provider.CalendarContract
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.FileProvider
import com.example.data.model.CATEGORIES
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale
import com.example.data.model.CURRENCIES
import com.example.data.model.FREQUENCIES
import com.example.data.model.SubscriptionEntity
import com.example.localization.LocalStrings
import com.example.logic.SubscriptionCalculations
import com.example.theme.Brand
import com.example.theme.CardBg
import com.example.ui.glass.DialogGlassBorder
import com.example.ui.glass.dialogGlass
import com.example.theme.CardBgElevated
import com.example.theme.RedAlert
import com.example.theme.TextMuted
import com.example.theme.TextWhite
import com.example.ui.components.ServiceIcon
import java.io.File
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditSubscriptionDialog(
    subscription: SubscriptionEntity?,
    primaryCurrency: String,
    onDismiss: () -> Unit,
    onSave: (SubscriptionEntity) -> Unit,
    onDelete: ((Int) -> Unit)? = null,
    // Called instead of onSave when editing changes the price: closedOutOld keeps the
    // original price/history frozen with an end date, newSubscription is a fresh active
    // row starting from the old one's next natural due date. Past months keep showing
    // what was actually charged instead of the new price being applied retroactively.
    onPriceChangeSplit: (closedOutOld: SubscriptionEntity, newSubscription: SubscriptionEntity) -> Unit = { _, _ -> }
) {
    val strings = LocalStrings.current
    val context = LocalContext.current
    val isEdit = subscription != null

    var name by remember { mutableStateOf(subscription?.name ?: "") }
    var priceText by remember {
        mutableStateOf(if (subscription != null) "%.2f".format(subscription.priceUsd) else "")
    }
    var isNameError by remember { mutableStateOf(false) }
    var isPriceError by remember { mutableStateOf(false) }
    var secondaryAmountText by remember {
        mutableStateOf(subscription?.secondaryAmount?.let { "%.2f".format(it) } ?: "")
    }
    var secondaryCurrency by remember { mutableStateOf(subscription?.secondaryCurrency ?: "") }
    var showSecondaryCurrency by remember {
        mutableStateOf(!subscription?.secondaryCurrency.isNullOrBlank())
    }
    var frequency by remember { mutableStateOf(subscription?.frequency ?: "monthly") }
    var intervalDaysText by remember {
        mutableStateOf(subscription?.intervalDays?.toString() ?: "30")
    }

    val initialDate = remember {
        if (subscription != null) {
            try { LocalDate.parse(subscription.startDate) } catch (e: Exception) { LocalDate.now() }
        } else {
            LocalDate.now()
        }
    }
    var dayText by remember { mutableStateOf(initialDate.dayOfMonth.toString()) }
    var monthText by remember { mutableStateOf(initialDate.monthValue.toString()) }
    var yearText by remember { mutableStateOf(initialDate.year.toString()) }
    var showDatePicker by remember { mutableStateOf(false) }

    val formattedStartDate = remember(dayText, monthText, yearText) {
        val d = dayText.toIntOrNull()
        val m = monthText.toIntOrNull()
        val y = yearText.toIntOrNull()
        if (d != null && m != null && y != null) {
            try {
                // Format with the app's own selected language, not the device's system
                // locale — otherwise this can render in Ukrainian/Russian month names
                // even while the in-app language is set to English.
                SubscriptionCalculations.clampedDate(y, m.coerceIn(1, 12), d.coerceIn(1, 31))
                    .format(DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.forLanguageTag(strings.language.code)))
            } catch (e: Exception) {
                "$dayText/$monthText/$yearText"
            }
        } else {
            "$dayText/$monthText/$yearText"
        }
    }

    var card by remember { mutableStateOf(subscription?.card ?: "") }
    var category by remember { mutableStateOf(subscription?.category ?: "Other") }

    var showDeleteConfirm by remember { mutableStateOf(false) }
    var categoryExpanded by remember { mutableStateOf(false) }
    var secondaryCurrencyExpanded by remember { mutableStateOf(false) }

    // Fields sit inside a CardBgElevated group panel — transparent fill plus a faint outline
    // reads as a subtle boundary instead of a second, competing box on top of the panel.
    val textFieldColors = OutlinedTextFieldDefaults.colors(
        focusedTextColor = TextWhite,
        unfocusedTextColor = TextWhite,
        focusedContainerColor = Color.Transparent,
        unfocusedContainerColor = Color.Transparent,
        focusedBorderColor = Brand,
        unfocusedBorderColor = TextMuted.copy(alpha = 0.18f),
        focusedLabelColor = Brand,
        unfocusedLabelColor = TextMuted
    )

    if (showDeleteConfirm && subscription != null) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text(strings.deleteSubscriptionTitle, color = TextWhite, fontWeight = FontWeight.Bold) },
            text = { Text(strings.deleteConfirmMessage(subscription.name), color = TextMuted) },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteConfirm = false
                        onDelete?.invoke(subscription.id)
                        onDismiss()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = RedAlert),
                    modifier = Modifier.testTag("confirm_delete_button")
                ) {
                    Text(strings.deleteAction)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) {
                    Text(strings.cancelAction, color = TextMuted)
                }
            },
            containerColor = CardBg.dialogGlass()
        )
    }

    if (showDatePicker) {
        // The native calendar picker pulls its month/weekday names from the Android
        // Configuration locale, which can still be the device's system language even
        // when the app's own language is set to English. Force this subtree's context
        // to the app's selected language so the picker always matches the app's text.
        val pickerLocale = Locale.forLanguageTag(strings.language.code)
        val localizedPickerContext = remember(pickerLocale) {
            val config = Configuration(context.resources.configuration)
            config.setLocale(pickerLocale)
            context.createConfigurationContext(config)
        }

        CompositionLocalProvider(LocalContext provides localizedPickerContext) {
            val datePickerState = rememberDatePickerState(
                initialSelectedDateMillis = run {
                    val d = (dayText.toIntOrNull() ?: initialDate.dayOfMonth).coerceIn(1, 31)
                    val m = (monthText.toIntOrNull() ?: initialDate.monthValue).coerceIn(1, 12)
                    val y = yearText.toIntOrNull() ?: initialDate.year
                    SubscriptionCalculations.clampedDate(y, m, d)
                        .atStartOfDay(ZoneId.of("UTC"))
                        .toInstant()
                        .toEpochMilli()
                }
            )
            DatePickerDialog(
                onDismissRequest = { showDatePicker = false },
                confirmButton = {
                    TextButton(onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            val picked = Instant.ofEpochMilli(millis).atZone(ZoneId.of("UTC")).toLocalDate()
                            dayText = picked.dayOfMonth.toString()
                            monthText = picked.monthValue.toString()
                            yearText = picked.year.toString()
                        }
                        showDatePicker = false
                    }) {
                        Text("OK", color = Brand, fontWeight = FontWeight.SemiBold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDatePicker = false }) {
                        Text(strings.cancelAction, color = TextMuted)
                    }
                },
                colors = DatePickerDefaults.colors(containerColor = CardBg)
            ) {
                DatePicker(
                    state = datePickerState,
                    colors = DatePickerDefaults.colors(containerColor = CardBg)
                )
            }
        }
    }

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = CardBg.dialogGlass(),
        scrimColor = Color.Black.copy(alpha = 0.5f),
        dragHandle = {
            BottomSheetDefaults.DragHandle(color = TextMuted.copy(alpha = 0.4f))
        },
        modifier = Modifier.testTag("add_edit_dialog")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .imePadding()
                .padding(horizontal = 20.dp, vertical = 10.dp)
                .verticalScroll(rememberScrollState())
        ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isEdit) strings.editSubscriptionTitle else strings.newSubscriptionTitle,
                        color = TextWhite,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Row {
                        if (isEdit && onDelete != null) {
                            IconButton(
                                onClick = { showDeleteConfirm = true },
                                modifier = Modifier.testTag("delete_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Delete",
                                    tint = RedAlert
                                )
                            }
                        }
                        IconButton(onClick = onDismiss, modifier = Modifier.testTag("close_button")) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close",
                                tint = TextMuted
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Live Avatar Preview — no boxed background, sits directly on the dialog so it
                // reads as one continuous area with the header above the perforated divider.
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        ServiceIcon(
                            name = name,
                            category = category,
                            size = 48.dp
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = if (name.isNotBlank()) name else strings.serviceNameLabel,
                            color = if (name.isNotBlank()) TextWhite else TextMuted,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Perforated divider between the preview box and the fields box
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    repeat(24) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(2.dp)
                                .clip(RoundedCornerShape(1.dp))
                                .background(TextMuted.copy(alpha = 0.25f))
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Fields — grouped underneath in their own box
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(CardBgElevated)
                        .padding(14.dp)
                ) {
                    // Name Input — label left, field right
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = strings.serviceNameLabel,
                            color = TextMuted,
                            fontSize = 13.sp,
                            modifier = Modifier.width(96.dp)
                        )
                        OutlinedTextField(
                            value = name,
                            onValueChange = {
                                name = it
                                if (isNameError && it.isNotBlank()) isNameError = false
                            },
                            isError = isNameError,
                            supportingText = if (isNameError) {
                                { Text("Name is required", color = RedAlert, fontSize = 11.sp) }
                            } else null,
                            singleLine = true,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("sub_name_input"),
                            colors = textFieldColors,
                            shape = RoundedCornerShape(12.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Price Input — label left, field right
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${strings.priceLabel} ($primaryCurrency)",
                            color = TextMuted,
                            fontSize = 13.sp,
                            modifier = Modifier.width(96.dp)
                        )
                        OutlinedTextField(
                            value = priceText,
                            onValueChange = {
                                priceText = it
                                if (isPriceError && it.toDoubleOrNull() != null) isPriceError = false
                            },
                            isError = isPriceError,
                            supportingText = if (isPriceError) {
                                { Text("Valid price required", color = RedAlert, fontSize = 11.sp) }
                            } else null,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            singleLine = true,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("sub_price_input"),
                            colors = textFieldColors,
                            shape = RoundedCornerShape(12.dp)
                        )
                    }

                    // When editing an existing subscription with a changed price but an
                    // unmoved Start Date, nudge the user that the date below is what decides
                    // when the new price actually kicks in.
                    if (subscription != null) {
                        val livePrice = priceText.toDoubleOrNull()
                        val priceEdited = livePrice != null &&
                            kotlin.math.abs(livePrice - subscription.priceUsd) > 0.001
                        val liveStart = SubscriptionCalculations.clampedDate(
                            yearText.toIntOrNull() ?: LocalDate.now().year,
                            (monthText.toIntOrNull() ?: LocalDate.now().monthValue).coerceIn(1, 12),
                            (dayText.toIntOrNull() ?: LocalDate.now().dayOfMonth).coerceIn(1, 31)
                        )
                        val originalStart = SubscriptionCalculations.parseStart(subscription)
                        if (priceEdited && !liveStart.isAfter(originalStart)) {
                            Text(
                                text = strings.priceChangeDateHint,
                                color = TextMuted,
                                fontSize = 11.sp,
                                modifier = Modifier.padding(top = 6.dp, start = 4.dp, end = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Secondary Currency & Amount — hidden by default, only revealed once the
                    // user deliberately opts in, instead of always showing two empty fields.
                    if (!showSecondaryCurrency) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { showSecondaryCurrency = true }
                                .padding(vertical = 12.dp)
                                .testTag("add_secondary_currency_button"),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = null,
                                    tint = Brand,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = "Add a secondary currency",
                                    color = Brand,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    } else {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            OutlinedTextField(
                                value = secondaryAmountText,
                                onValueChange = { secondaryAmountText = it },
                                label = { Text(strings.secAmountLabel) },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                singleLine = true,
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("sec_amount_input"),
                                colors = textFieldColors,
                                shape = RoundedCornerShape(12.dp)
                            )

                            ExposedDropdownMenuBox(
                                expanded = secondaryCurrencyExpanded,
                                onExpandedChange = { secondaryCurrencyExpanded = !secondaryCurrencyExpanded },
                                modifier = Modifier.weight(1f)
                            ) {
                                OutlinedTextField(
                                    value = secondaryCurrency.ifEmpty { "—" },
                                    onValueChange = {},
                                    readOnly = true,
                                    label = { Text(strings.secCurrencyLabel) },
                                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = secondaryCurrencyExpanded) },
                                    modifier = Modifier
                                        .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                                        .testTag("sec_currency_dropdown"),
                                    colors = textFieldColors,
                                    shape = RoundedCornerShape(12.dp)
                                )
                                ExposedDropdownMenu(
                                    expanded = secondaryCurrencyExpanded,
                                    onDismissRequest = { secondaryCurrencyExpanded = false },
                                    modifier = Modifier.background(CardBg)
                                ) {
                                    CURRENCIES.forEach { cur ->
                                        DropdownMenuItem(
                                            text = { Text(if (cur.isEmpty()) "—" else cur, color = TextWhite) },
                                            onClick = {
                                                secondaryCurrency = cur
                                                secondaryCurrencyExpanded = false
                                            }
                                        )
                                    }
                                }
                            }

                            IconButton(
                                onClick = {
                                    showSecondaryCurrency = false
                                    secondaryAmountText = ""
                                    secondaryCurrency = ""
                                },
                                modifier = Modifier.testTag("remove_secondary_currency_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Remove secondary currency",
                                    tint = TextMuted
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Frequency Selector Chips
                    Text(strings.frequencyLabel, color = TextMuted, fontSize = 12.sp, modifier = Modifier.padding(start = 4.dp))
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        FREQUENCIES.forEach { freq ->
                            val isSelected = frequency.equals(freq, ignoreCase = true)
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (isSelected) Brand else CardBg)
                                    .clickable { frequency = freq }
                                    .padding(vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = strings.getFrequencyName(freq),
                                    color = if (isSelected) Color.White else TextMuted,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                )
                            }
                        }
                    }

                    // Custom Interval Days (if frequency is custom)
                    if (frequency.equals("custom", ignoreCase = true)) {
                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedTextField(
                            value = intervalDaysText,
                            onValueChange = { intervalDaysText = it },
                            label = { Text(strings.repeatEveryDaysLabel) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("custom_interval_input"),
                            colors = textFieldColors,
                            shape = RoundedCornerShape(12.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Date Selector — label left, tappable field right, opens a real calendar
                    // picker instead of three free-typed Day/Month/Year number boxes.
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = strings.startDateLabel,
                            color = TextMuted,
                            fontSize = 13.sp,
                            modifier = Modifier.width(96.dp)
                        )
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .border(1.dp, TextMuted.copy(alpha = 0.18f), RoundedCornerShape(12.dp))
                                .clickable { showDatePicker = true }
                                .padding(horizontal = 16.dp, vertical = 14.dp)
                                .testTag("start_date_field")
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = formattedStartDate,
                                    color = TextWhite,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Medium
                                )
                                Icon(
                                    imageVector = Icons.Default.CalendarMonth,
                                    contentDescription = null,
                                    tint = Brand,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Card Last 4 Digits & Category
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            value = card,
                            onValueChange = { if (it.length <= 4 && it.all { c -> c.isDigit() }) card = it },
                            label = { Text(strings.cardLast4Label) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("sub_card_input"),
                            colors = textFieldColors,
                            shape = RoundedCornerShape(12.dp)
                        )

                        ExposedDropdownMenuBox(
                            expanded = categoryExpanded,
                            onExpandedChange = { categoryExpanded = !categoryExpanded },
                            modifier = Modifier.weight(1.3f)
                        ) {
                            OutlinedTextField(
                                value = strings.getCategoryName(category),
                                onValueChange = {},
                                readOnly = true,
                                label = { Text(strings.categoryLabel) },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded) },
                                modifier = Modifier
                                    .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                                    .testTag("category_dropdown"),
                                colors = textFieldColors,
                                shape = RoundedCornerShape(12.dp)
                            )
                            ExposedDropdownMenu(
                                expanded = categoryExpanded,
                                onDismissRequest = { categoryExpanded = false },
                                modifier = Modifier.background(CardBg)
                            ) {
                                CATEGORIES.forEach { cat ->
                                    DropdownMenuItem(
                                        text = { Text(strings.getCategoryName(cat), color = TextWhite) },
                                        onClick = {
                                            category = cat
                                            categoryExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Add to Calendar Button
                OutlinedButton(
                    onClick = {
                        shareToCalendar(
                            context = context,
                            name = name.ifBlank { "Subscription" },
                            priceUsd = priceText.toDoubleOrNull() ?: 0.0,
                            frequency = frequency,
                            intervalDays = intervalDaysText.toIntOrNull(),
                            year = yearText.toIntOrNull() ?: LocalDate.now().year,
                            month = monthText.toIntOrNull() ?: LocalDate.now().monthValue,
                            day = dayText.toIntOrNull() ?: LocalDate.now().dayOfMonth
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("add_to_calendar_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Brand)
                ) {
                    Icon(imageVector = Icons.Default.CalendarMonth, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(strings.addToCalendarBtn, fontWeight = FontWeight.SemiBold)
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Save Button
                Button(
                    onClick = {
                        val parsedPrice = priceText.toDoubleOrNull()
                        val hasNameError = name.isBlank()
                        val hasPriceError = parsedPrice == null
                        if (hasNameError || hasPriceError) {
                            isNameError = hasNameError
                            isPriceError = hasPriceError
                            return@Button
                        }

                        val parsedSecPrice = secondaryAmountText.toDoubleOrNull()
                        val parsedInterval = intervalDaysText.toIntOrNull() ?: 30
                        val y = yearText.toIntOrNull() ?: LocalDate.now().year
                        val m = (monthText.toIntOrNull() ?: LocalDate.now().monthValue).coerceIn(1, 12)
                        val d = (dayText.toIntOrNull() ?: LocalDate.now().dayOfMonth).coerceIn(1, 31)
                        val date = SubscriptionCalculations.clampedDate(y, m, d)

                        val entity = SubscriptionEntity(
                            id = subscription?.id ?: 0,
                            name = name.trim(),
                            priceUsd = parsedPrice,
                            secondaryAmount = parsedSecPrice,
                            secondaryCurrency = secondaryCurrency.ifEmpty { null },
                            frequency = frequency,
                            startDate = date.toString(),
                            intervalDays = if (frequency.equals("custom", ignoreCase = true)) parsedInterval else null,
                            card = card.ifBlank { null },
                            category = category
                        )

                        val originalSubscription = subscription
                        val originalStart = originalSubscription?.let { SubscriptionCalculations.parseStart(it) }
                        val priceChanged = originalSubscription != null &&
                            kotlin.math.abs(parsedPrice - originalSubscription.priceUsd) > 0.001
                        // The cutover is manual: it's whatever Start Date the user entered above.
                        // Moving it forward while also changing the price is how they tell the
                        // app "the old price held until here, the new one starts here" — so old
                        // months keep billing the old price and this date onward gets the new one.
                        // If they change the price without moving the date, there's no cutover to
                        // split on, so it's just a normal in-place update.
                        val cutoverMoved = originalStart != null && date.isAfter(originalStart)
                        if (priceChanged && cutoverMoved) {
                            val closedOutOld = originalSubscription.copy(endDate = date.toString())
                            val newSubscription = entity.copy(id = 0, endDate = null)
                            onPriceChangeSplit(closedOutOld, newSubscription)
                        } else {
                            onSave(entity)
                        }
                        onDismiss()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("save_subscription_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Brand)
                ) {
                    Text(
                        text = if (isEdit) strings.saveChangesBtn else strings.addSubscriptionBtn,
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }

private fun shareToCalendar(
    context: Context,
    name: String,
    priceUsd: Double,
    frequency: String,
    intervalDays: Int?,
    year: Int,
    month: Int,
    day: Int
) {
    val date = SubscriptionCalculations.clampedDate(year, month.coerceIn(1, 12), day.coerceIn(1, 31))
    val formattedPrice = String.format(java.util.Locale.US, "%.2f", priceUsd)
    val description = "Subscription renewal: $name ($$formattedPrice / $frequency)"

    val startMillis = date.atTime(9, 0)
        .atZone(java.time.ZoneId.systemDefault())
        .toInstant()
        .toEpochMilli()
    val rrule = when (frequency.lowercase()) {
        "quarterly" -> "FREQ=MONTHLY;INTERVAL=3"
        "yearly", "annually" -> "FREQ=YEARLY;INTERVAL=1"
        "custom" -> "FREQ=DAILY;INTERVAL=${intervalDays ?: 1}"
        else -> "FREQ=MONTHLY;INTERVAL=1"
    }

    // Direct Calendar Insert Intent
    val insertIntent = Intent(Intent.ACTION_INSERT).apply {
        data = CalendarContract.Events.CONTENT_URI
        putExtra(CalendarContract.Events.TITLE, "$name Renewal")
        putExtra(CalendarContract.Events.DESCRIPTION, description)
        putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, startMillis)
        putExtra(CalendarContract.EXTRA_EVENT_END_TIME, startMillis + 3600_000)
        putExtra(CalendarContract.Events.ALL_DAY, false)
        putExtra(CalendarContract.Events.RRULE, rrule)
    }

    try {
        val icsText = SubscriptionCalculations.createIcsContent(name, priceUsd, frequency, intervalDays, date)
        val exportDir = File(context.cacheDir, "exports")
        if (!exportDir.exists()) {
            exportDir.mkdirs()
        }
        val safeName = name.replace(Regex("[^a-zA-Z0-9_-]"), "_").ifBlank { "subscription" }
        val file = File(exportDir, "subscription_${safeName}.ics")
        file.writeText(icsText)

        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )

        // View Intent for text/calendar (opens default calendar / ICS viewer)
        val viewIntent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, "text/calendar")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        // Share/Send Intent (shares .ics via share sheet / drive / email / files)
        val sendIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/calendar"
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_SUBJECT, "$name Subscription (.ics)")
            putExtra(Intent.EXTRA_TEXT, description)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        // Unified chooser offering direct calendar insertion, viewing, or sharing
        val chooser = Intent.createChooser(viewIntent, "Add to Calendar / Share .ics").apply {
            putExtra(Intent.EXTRA_INITIAL_INTENTS, arrayOf(insertIntent, sendIntent))
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        context.startActivity(chooser)
    } catch (e: Exception) {
        // Fallback directly to native CalendarContract insert if FileProvider or chooser encounters an issue
        try {
            insertIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(insertIntent)
        } catch (err: Exception) {
            Toast.makeText(context, "Could not open calendar: ${err.localizedMessage ?: "Unknown error"}", Toast.LENGTH_SHORT).show()
        }
    }
}
