package com.example.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SettingsBrightness
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.data.model.ALL_CURRENCIES
import com.example.localization.LocalStrings
import com.example.theme.Brand
import com.example.theme.BrandLight
import com.example.theme.CardBg
import com.example.theme.CardBgElevated
import com.example.theme.GreenSuccess
import com.example.theme.TextMuted
import com.example.theme.TextWhite
import com.example.theme.WindowBg

@Composable
fun SettingsScreen(
    currentThemeMode: String,
    onThemeModeChange: (String) -> Unit,
    useMaterialAccent: Boolean,
    onUseMaterialAccentChange: (Boolean) -> Unit,
    primaryCurrency: String,
    onCurrencyChange: (String) -> Unit,
    onOpenImportExport: () -> Unit,
    onSendTestNotification: () -> Unit,
    subscriptionCount: Int,
    modifier: Modifier = Modifier,
    currentLanguage: String = "en",
    onLanguageChange: (String) -> Unit = {},
    monthlyNotificationEnabled: Boolean = true,
    onMonthlyNotificationChange: (Boolean) -> Unit = {},
    onSendTestMonthlyNotification: () -> Unit = {}
) {
    val strings = LocalStrings.current
    val context = LocalContext.current
    var showCurrencyDialog by remember { mutableStateOf(false) }
    var currencySearchQuery by remember { mutableStateOf("") }
    var testNotificationSent by remember { mutableStateOf(false) }
    var testMonthlySent by remember { mutableStateOf(false) }

    val hasNotificationPermission = remember {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { _ -> }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(WindowBg)
            .padding(horizontal = 16.dp)
            .testTag("settings_screen")
    ) {
        // Top Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, bottom = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = strings.settingsTitle,
                color = TextWhite,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 90.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // --- SECTION 1: LANGUAGE & LOCALIZATION ---
            item {
                SectionHeader(title = strings.languageSectionTitle, icon = Icons.Default.Language)
            }

            item {
                var languageExpanded by remember { mutableStateOf(false) }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = CardBg)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { languageExpanded = true }
                            .padding(horizontal = 16.dp, vertical = 12.dp)
                            .testTag("settings_language_row"),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .padding(end = 12.dp)
                        ) {
                            Text(
                                text = strings.languageOptionTitle,
                                color = TextWhite,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = if (currentLanguage == "ru") "Русский (RU)" else "English (EN)",
                                color = TextMuted,
                                fontSize = 12.sp
                            )
                        }

                        Box {
                            Row(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(CardBgElevated)
                                    .border(1.dp, Brand.copy(alpha = 0.35f), RoundedCornerShape(10.dp))
                                    .padding(horizontal = 12.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = if (currentLanguage == "ru") "Русский" else "English",
                                    color = Brand,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                                Icon(
                                    imageVector = Icons.Default.ArrowDropDown,
                                    contentDescription = "Select language",
                                    tint = BrandLight,
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            DropdownMenu(
                                expanded = languageExpanded,
                                onDismissRequest = { languageExpanded = false },
                                modifier = Modifier
                                    .background(CardBgElevated)
                                    .border(1.dp, CardBg, RoundedCornerShape(12.dp))
                            ) {
                                DropdownMenuItem(
                                    text = {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = "English (EN)",
                                                color = if (currentLanguage == "en") Brand else TextWhite,
                                                fontWeight = if (currentLanguage == "en") FontWeight.Bold else FontWeight.Medium,
                                                fontSize = 14.sp
                                            )
                                            if (currentLanguage == "en") {
                                                Spacer(modifier = Modifier.width(16.dp))
                                                Icon(
                                                    imageVector = Icons.Default.Check,
                                                    contentDescription = null,
                                                    tint = Brand,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                            }
                                        }
                                    },
                                    onClick = {
                                        onLanguageChange("en")
                                        languageExpanded = false
                                    },
                                    modifier = Modifier.testTag("lang_menu_en")
                                )

                                DropdownMenuItem(
                                    text = {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = "Русский (RU)",
                                                color = if (currentLanguage == "ru") Brand else TextWhite,
                                                fontWeight = if (currentLanguage == "ru") FontWeight.Bold else FontWeight.Medium,
                                                fontSize = 14.sp
                                            )
                                            if (currentLanguage == "ru") {
                                                Spacer(modifier = Modifier.width(16.dp))
                                                Icon(
                                                    imageVector = Icons.Default.Check,
                                                    contentDescription = null,
                                                    tint = Brand,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                            }
                                        }
                                    },
                                    onClick = {
                                        onLanguageChange("ru")
                                        languageExpanded = false
                                    },
                                    modifier = Modifier.testTag("lang_menu_ru")
                                )
                            }
                        }
                    }
                }
            }

            // --- SECTION 2: APPEARANCE & THEME ---
            item {
                SectionHeader(title = strings.themeSectionTitle, icon = Icons.Default.Palette)
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = CardBg)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = strings.themeModeTitle,
                            color = TextWhite,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = strings.themeModeSubtitle,
                            color = TextMuted,
                            fontSize = 13.sp
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // Theme Mode 3-card selector
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            ThemeOptionCard(
                                title = strings.darkTheme,
                                icon = Icons.Default.DarkMode,
                                selected = currentThemeMode == "dark",
                                onClick = { onThemeModeChange("dark") },
                                modifier = Modifier.weight(1f)
                            )
                            ThemeOptionCard(
                                title = strings.lightTheme,
                                icon = Icons.Default.LightMode,
                                selected = currentThemeMode == "light",
                                onClick = { onThemeModeChange("light") },
                                modifier = Modifier.weight(1f)
                            )
                            ThemeOptionCard(
                                title = strings.systemTheme,
                                icon = Icons.Default.SettingsBrightness,
                                selected = currentThemeMode == "system",
                                onClick = { onThemeModeChange("system") },
                                modifier = Modifier.weight(1f)
                            )
                        }

                        // Material You Toggle (Android 12+)
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                            Spacer(modifier = Modifier.height(16.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ColorLens,
                                        contentDescription = null,
                                        tint = Brand,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = strings.materialYouTitle,
                                            color = TextWhite,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Medium
                                        )
                                        Text(
                                            text = strings.materialYouSubtitle,
                                            color = TextMuted,
                                            fontSize = 12.sp
                                        )
                                    }
                                }

                                Switch(
                                    checked = useMaterialAccent,
                                    onCheckedChange = onUseMaterialAccentChange,
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = Color.White,
                                        checkedTrackColor = Brand,
                                        uncheckedThumbColor = TextMuted,
                                        uncheckedTrackColor = CardBgElevated
                                    ),
                                    modifier = Modifier.testTag("material_you_switch")
                                )
                            }
                        }
                    }
                }
            }

            // --- SECTION 3: ACCOUNT & CURRENCY ---
            item {
                SectionHeader(title = strings.currencySectionTitle, icon = Icons.Default.AttachMoney)
            }

            item {
                val currInfo = ALL_CURRENCIES.find { it.code == primaryCurrency }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = CardBg)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showCurrencyDialog = true }
                            .padding(horizontal = 16.dp, vertical = 12.dp)
                            .testTag("settings_currency_row"),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .padding(end = 12.dp)
                        ) {
                            Text(
                                text = strings.primaryCurrencyTitle,
                                color = TextWhite,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = currInfo?.let { "${it.name} (${it.code})" } ?: primaryCurrency,
                                color = TextMuted,
                                fontSize = 12.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(CardBgElevated)
                                .border(1.dp, Brand.copy(alpha = 0.35f), RoundedCornerShape(10.dp))
                                .padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = primaryCurrency,
                                color = Brand,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                            if (currInfo != null && currInfo.symbol.isNotEmpty() && currInfo.symbol != primaryCurrency) {
                                Text(
                                    text = currInfo.symbol,
                                    color = BrandLight,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 12.sp
                                )
                            }
                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = "Select currency",
                                tint = BrandLight,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }

            // --- SECTION 4: NOTIFICATIONS & BACKGROUND REMINDERS ---
            item {
                SectionHeader(title = strings.notificationsSectionTitle, icon = Icons.Default.Notifications)
            }

            // Monthly Digest Notification Toggle Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = CardBg)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(end = 12.dp)
                            ) {
                                Text(
                                    text = strings.monthlyNotificationTitle,
                                    color = TextWhite,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = strings.monthlyNotificationSubtitle,
                                    color = TextMuted,
                                    fontSize = 12.sp
                                )
                            }

                            Switch(
                                checked = monthlyNotificationEnabled,
                                onCheckedChange = onMonthlyNotificationChange,
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = Brand,
                                    uncheckedThumbColor = TextMuted,
                                    uncheckedTrackColor = CardBgElevated
                                ),
                                modifier = Modifier.testTag("monthly_notification_switch")
                            )
                        }

                        if (monthlyNotificationEnabled) {
                            Spacer(modifier = Modifier.height(12.dp))
                            OutlinedButton(
                                onClick = {
                                    onSendTestMonthlyNotification()
                                    testMonthlySent = true
                                },
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("send_test_monthly_notification_btn")
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = if (testMonthlySent) Icons.Default.Check else Icons.Default.Notifications,
                                        contentDescription = null,
                                        tint = Brand,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = if (testMonthlySent) strings.monthlyDigestSent else strings.testMonthlyNotificationBtn,
                                        color = Brand,
                                        fontSize = 13.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(8.dp))
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = CardBg)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .clip(CircleShape)
                                        .background(GreenSuccess)
                                    )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = strings.bgServiceActive,
                                    color = GreenSuccess,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(CardBgElevated)
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "WorkManager",
                                    color = TextMuted,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = strings.bgServiceSubtitle,
                            color = TextMuted,
                            fontSize = 13.sp
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && !hasNotificationPermission) {
                                Button(
                                    onClick = {
                                        permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Brand),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(strings.enablePermission, fontSize = 13.sp)
                                }
                            }

                            OutlinedButton(
                                onClick = {
                                    onSendTestNotification()
                                    testNotificationSent = true
                                },
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("send_test_notification_btn")
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = if (testNotificationSent) Icons.Default.Check else Icons.Default.Sync,
                                        contentDescription = null,
                                        tint = Brand,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = if (testNotificationSent) strings.notificationSent else strings.testNotificationBtn,
                                        color = Brand,
                                        fontSize = 13.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // --- SECTION 5: DATA & BACKUP ---
            item {
                SectionHeader(title = strings.dataSectionTitle, icon = Icons.Default.FileDownload)
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = CardBg)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = strings.backupTitle,
                                    color = TextWhite,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = strings.trackedCountSubtitle(subscriptionCount),
                                    color = TextMuted,
                                    fontSize = 13.sp
                                )
                            }

                            Button(
                                onClick = onOpenImportExport,
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Brand),
                                modifier = Modifier.testTag("settings_import_export_btn")
                            ) {
                                Text(strings.importExportBtn, fontSize = 13.sp)
                            }
                        }
                    }
                }
            }
        }
    }

    // Currency Selection Modal Dialog
    if (showCurrencyDialog) {
        val filteredCurrencies = remember(currencySearchQuery) {
            val q = currencySearchQuery.trim().lowercase()
            if (q.isEmpty()) {
                ALL_CURRENCIES
            } else {
                ALL_CURRENCIES.filter {
                    it.code.lowercase().contains(q) ||
                    it.name.lowercase().contains(q) ||
                    it.symbol.lowercase().contains(q)
                }
            }
        }

        AlertDialog(
            onDismissRequest = {
                showCurrencyDialog = false
                currencySearchQuery = ""
            },
            containerColor = CardBg,
            title = {
                Text(
                    text = strings.selectCurrencyTitle,
                    color = TextWhite,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = currencySearchQuery,
                        onValueChange = { currencySearchQuery = it },
                        placeholder = { Text(strings.searchCurrencyPlaceholder, color = TextMuted, fontSize = 13.sp) },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Search",
                                tint = TextMuted,
                                modifier = Modifier.size(18.dp)
                            )
                        },
                        trailingIcon = {
                            if (currencySearchQuery.isNotEmpty()) {
                                IconButton(onClick = { currencySearchQuery = "" }) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Clear",
                                        tint = TextMuted,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite,
                            focusedContainerColor = CardBgElevated,
                            unfocusedContainerColor = CardBgElevated,
                            focusedBorderColor = Brand,
                            unfocusedBorderColor = Color.Transparent
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp)
                            .testTag("currency_search_input")
                    )

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        items(filteredCurrencies.size) { index ->
                            val curr = filteredCurrencies[index]
                            val isSelected = curr.code == primaryCurrency
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (isSelected) Brand.copy(alpha = 0.15f) else Color.Transparent)
                                    .clickable {
                                        onCurrencyChange(curr.code)
                                        showCurrencyDialog = false
                                        currencySearchQuery = ""
                                    }
                                    .padding(horizontal = 12.dp, vertical = 10.dp)
                                    .testTag("currency_option_${curr.code}"),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(34.dp)
                                            .clip(CircleShape)
                                            .background(if (isSelected) Brand.copy(alpha = 0.25f) else CardBgElevated),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = curr.symbol,
                                            color = if (isSelected) Brand else TextWhite,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp,
                                            maxLines = 1,
                                            softWrap = false
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = curr.code,
                                            color = if (isSelected) Brand else TextWhite,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 15.sp,
                                            maxLines = 1,
                                            softWrap = false
                                        )
                                        Text(
                                            text = curr.name,
                                            color = TextMuted,
                                            fontSize = 12.sp,
                                            maxLines = 1
                                        )
                                    }
                                }
                                if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Selected",
                                        tint = Brand,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    showCurrencyDialog = false
                    currencySearchQuery = ""
                }) {
                    Text(strings.close, color = Brand)
                }
            }
        )
    }
}

@Composable
private fun SectionHeader(
    title: String,
    icon: ImageVector
) {
    Row(
        modifier = Modifier.padding(top = 8.dp, bottom = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Brand,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = title,
            color = BrandLight,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun ThemeOptionCard(
    title: String,
    icon: ImageVector,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(if (selected) Brand.copy(alpha = 0.15f) else CardBgElevated)
            .border(
                width = if (selected) 1.5.dp else 1.dp,
                color = if (selected) Brand else Color.Transparent,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable { onClick() }
            .padding(vertical = 12.dp, horizontal = 8.dp)
            .testTag("theme_option_${title.lowercase().replace(" ", "_").replace("/", "_")}"),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = if (selected) Brand else TextMuted,
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = title,
                color = if (selected) TextWhite else TextMuted,
                fontSize = 12.sp,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
            )
        }
    }
}
