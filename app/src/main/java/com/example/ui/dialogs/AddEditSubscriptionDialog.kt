package com.example.ui.dialogs

import android.content.Context
import android.content.Intent
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
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
import com.example.data.model.CURRENCIES
import com.example.data.model.FREQUENCIES
import com.example.data.model.SubscriptionEntity
import com.example.localization.LocalStrings
import com.example.logic.SubscriptionCalculations
import com.example.theme.Brand
import com.example.theme.CardBg
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
    onDelete: ((Int) -> Unit)? = null
) {
    val strings = LocalStrings.current
    val context = LocalContext.current
    val isEdit = subscription != null

    var name by remember { mutableStateOf(subscription?.name ?: "") }
    var priceText by remember {
        mutableStateOf(if (subscription != null) "%.2f".format(subscription.priceUsd) else "")
    }
    var secondaryAmountText by remember {
        mutableStateOf(subscription?.secondaryAmount?.let { "%.2f".format(it) } ?: "")
    }
    var secondaryCurrency by remember { mutableStateOf(subscription?.secondaryCurrency ?: "") }
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

    var card by remember { mutableStateOf(subscription?.card ?: "") }
    var category by remember { mutableStateOf(subscription?.category ?: "Other") }

    var showDeleteConfirm by remember { mutableStateOf(false) }
    var categoryExpanded by remember { mutableStateOf(false) }
    var secondaryCurrencyExpanded by remember { mutableStateOf(false) }

    val textFieldColors = OutlinedTextFieldDefaults.colors(
        focusedTextColor = TextWhite,
        unfocusedTextColor = TextWhite,
        focusedContainerColor = CardBgElevated,
        unfocusedContainerColor = CardBgElevated,
        focusedBorderColor = Brand,
        unfocusedBorderColor = Color.Transparent,
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
            containerColor = CardBg
        )
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .clip(RoundedCornerShape(24.dp))
                .testTag("add_edit_dialog"),
            color = CardBg
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
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

                // Live Avatar Preview
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(CardBgElevated)
                        .padding(vertical = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        ServiceIcon(
                            name = name,
                            category = category,
                            size = 64.dp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (name.isNotBlank()) name else strings.serviceNameLabel,
                            color = if (name.isNotBlank()) TextWhite else TextMuted,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Name Input
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text(strings.serviceNameLabel) },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("sub_name_input"),
                    colors = textFieldColors,
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Price Input in Primary Currency
                OutlinedTextField(
                    value = priceText,
                    onValueChange = { priceText = it },
                    label = { Text("${strings.priceLabel} ($primaryCurrency)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("sub_price_input"),
                    colors = textFieldColors,
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Secondary Currency & Amount (Optional)
                Row(
                    modifier = Modifier.fillMaxWidth(),
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
                                .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                                .testTag("sec_currency_dropdown"),
                            colors = textFieldColors,
                            shape = RoundedCornerShape(12.dp)
                        )
                        ExposedDropdownMenu(
                            expanded = secondaryCurrencyExpanded,
                            onDismissRequest = { secondaryCurrencyExpanded = false },
                            modifier = Modifier.background(CardBgElevated)
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
                                .background(if (isSelected) Brand else CardBgElevated)
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

                // Date Selector (Day, Month, Year)
                Text(strings.startDateLabel, color = TextMuted, fontSize = 12.sp, modifier = Modifier.padding(start = 4.dp))
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = dayText,
                        onValueChange = { dayText = it },
                        label = { Text(strings.dayLabel) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.weight(1f),
                        colors = textFieldColors,
                        shape = RoundedCornerShape(12.dp)
                    )
                    OutlinedTextField(
                        value = monthText,
                        onValueChange = { monthText = it },
                        label = { Text(strings.monthLabel) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.weight(1f),
                        colors = textFieldColors,
                        shape = RoundedCornerShape(12.dp)
                    )
                    OutlinedTextField(
                        value = yearText,
                        onValueChange = { yearText = it },
                        label = { Text(strings.yearLabel) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.weight(1.3f),
                        colors = textFieldColors,
                        shape = RoundedCornerShape(12.dp)
                    )
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
                                .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                                .testTag("category_dropdown"),
                            colors = textFieldColors,
                            shape = RoundedCornerShape(12.dp)
                        )
                        ExposedDropdownMenu(
                            expanded = categoryExpanded,
                            onDismissRequest = { categoryExpanded = false },
                            modifier = Modifier.background(CardBgElevated)
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
                        val parsedPrice = priceText.toDoubleOrNull() ?: 0.0
                        val parsedSecPrice = secondaryAmountText.toDoubleOrNull()
                        val parsedInterval = intervalDaysText.toIntOrNull() ?: 30
                        val y = yearText.toIntOrNull() ?: LocalDate.now().year
                        val m = (monthText.toIntOrNull() ?: LocalDate.now().monthValue).coerceIn(1, 12)
                        val d = (dayText.toIntOrNull() ?: LocalDate.now().dayOfMonth).coerceIn(1, 31)
                        val date = SubscriptionCalculations.clampedDate(y, m, d)

                        val entity = SubscriptionEntity(
                            id = subscription?.id ?: 0,
                            name = name.ifBlank { "New Subscription" },
                            priceUsd = parsedPrice,
                            secondaryAmount = parsedSecPrice,
                            secondaryCurrency = secondaryCurrency.ifEmpty { null },
                            frequency = frequency,
                            startDate = date.toString(),
                            intervalDays = if (frequency.equals("custom", ignoreCase = true)) parsedInterval else null,
                            card = card.ifBlank { null },
                            category = category
                        )
                        onSave(entity)
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
    try {
        val date = SubscriptionCalculations.clampedDate(year, month.coerceIn(1, 12), day.coerceIn(1, 31))
        val icsText = SubscriptionCalculations.createIcsContent(name, priceUsd, frequency, intervalDays, date)
        val file = File(context.cacheDir, "subscription_${name.replace(" ", "_")}.ics")
        file.writeText(icsText)

        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )

        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, "text/calendar")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        val chooser = Intent.createChooser(intent, "Open with Calendar")
        chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(chooser)
    } catch (e: Exception) {
        // Fallback: silent handling
    }
}
