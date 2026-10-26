# Glass UI + Architecture Migration Guide

Transfer guide for porting the changes made on branch `claude/festive-faraday-f0h00k` (base commit `c3e892b`) to another version of the Subscription Tracker app. The full diff is also available with `git diff c3e892b..claude/festive-faraday-f0h00k`.

None of this was compiled in the sandbox where it was written, because Google Maven was unreachable. Build it once after porting and fix any version-related errors.

## Overview

Two goals were handled together.

1. Architecture: manual DI container, a typed settings repository, a single immutable UI state, UI-owned navigation, and a slim `MainActivity`.
2. Liquid Glass UI using the Vitro library (`io.github.itshedimisawi:vitro:0.1.0`): a floating glass navigation bar and FAB, glass cards and buttons, a calendar that focuses on today, and a redesigned yearly view.

Suggested porting order: build files, data layer, DI and ViewModel, glass helpers, `MainApp` and `MainActivity`, then screen edits.

## 1. Build changes

Vitro 0.1.0 requires Compose 1.12.1 and a Kotlin 2.4 stdlib, so the toolchain is bumped to match Vitro's own catalog.

`gradle/libs.versions.toml`:

| Item | Old | New |
| --- | --- | --- |
| `agp` | 9.1.1 | 9.4.1 |
| `kotlin` | 2.2.10 | 2.4.21 |
| `activityCompose` | 1.10.1 | 1.13.0 |
| `composeBom` | 2024.09.00 | 2026.09.00 |
| `materialIconsExtended` | 1.7.0 | 1.7.8 |
| `room` | 2.7.0 | 2.8.4 |
| `vitro` | n/a | 0.1.0 |

Also remove the unused `material3 = "1.3.0"` version entry (the BOM manages Material3), and add these libraries:

```toml
androidx-lifecycle-runtime-compose = { group = "androidx.lifecycle", name = "lifecycle-runtime-compose", version.ref = "lifecycleRuntimeKtx" }
vitro = { group = "io.github.itshedimisawi", name = "vitro", version.ref = "vitro" }
```

`app/build.gradle.kts` dependencies:

```kotlin
implementation(libs.androidx.lifecycle.runtime.compose)
implementation(libs.vitro)
```

Known risks on first build: KSP `2.3.5` against Kotlin 2.4.21 may need a bump, and Material3 API drift between 1.3 and the new BOM may produce deprecation warnings or errors in older screens.

## 2. Manifest

Register the Application class:

```xml
<application
    android:name=".SubscriptionApp"
    ...>
```

## 3. Architecture changes

### Data layer

- `SubscriptionDao` gains `getAllSettings(): Flow<List<SettingEntity>>` (`SELECT * FROM settings`). No schema change, so no migration.
- Settings logic moves out of `SubscriptionRepository` into the new `SettingsRepository`. The DB keys are unchanged (`theme_mode`, `use_material_accent`, `primary_currency`, `app_language`, `daily_notification_enabled`, `monthly_notification_enabled`, `card_colors`), so existing user data keeps working.
- Theme mode is now the `ThemeMode` enum instead of the strings `"dark"`, `"light"`, `"system"`.

### DI and platform seam

- `SubscriptionApp` owns an `AppContainer`. `Context.appContainer` is an extension used by the ViewModel factory and the reminder worker.
- `SystemServices` wraps the Android calls the ViewModel needs (scheduling the worker, sending reminders, per-app language), so `MainViewModel` no longer extends `AndroidViewModel`.
- `SubscriptionReminderWorker.doWork()` now reads from the container:

```kotlin
val container = applicationContext.appContainer
val subscriptions = container.subscriptionRepository.getAllSubscriptionsSync()
val settings = container.settingsRepository.current()

NotificationHelper.checkAndSendReminders(
    applicationContext,
    subscriptions,
    settings.primaryCurrency,
    settings.monthlyNotificationEnabled,
    settings.dailyNotificationEnabled
)
```

### ViewModel and UI state

- `MainViewModel` exposes one `uiState: StateFlow<MainUiState>` and is created with `viewModelFactory { initializer { ... } }`.
- Navigation state (`targetTab`, `navigateToTab`, `onTabNavigated`) is removed from the ViewModel. The tab lives in the UI as `rememberSaveable`, and a notification intent hands the tab to the UI through `MainActivity.requestedTab`.
- `sendTestNotification` and `sendTestMonthlyNotification` were removed from the ViewModel because nothing called them.
- `MainActivity.onResume` locale sync is replaced by `LifecycleEventEffect(Lifecycle.Event.ON_RESUME)` inside `MainApp`.

### Theme

- `SubscriptionTrackerTheme(themeMode: ThemeMode, ...)` replaces the string parameter, and the `when` over the strings becomes a `when` over the enum.
- The deprecated `window.statusBarColor` and `window.navigationBarColor` assignments are removed (edge-to-edge handles them), along with the unused `toArgb` import. The `isAppearanceLight*` calls stay.

## 4. Glass changes

### Concept

- `MainApp` creates one `rememberGlassBackdrop()` and marks the content area with `glassBackdrop(backdrop)`. The floating bar and FAB sit above it and refract whatever scrolls underneath.
- `LocalGlassBackdrop` shares the backdrop. `Modifier.glassSurface(shape, fallback, glass)` draws glass when a backdrop exists and a flat fill otherwise, so components still work in previews and tests.
- Apply `.clip(shape)` after `glassSurface` so ripples and content stay inside the glass.
- `LocalBottomBarInset` replaces the hardcoded bottom paddings (80, 90 and 100 dp) in every screen's `LazyColumn`.
- `LocalGlassDarkTheme provides AppTheme.colors.isDark` makes glass follow the app's theme instead of the system's.
- The four screen roots lose their opaque `.background(WindowBg)`. Without that, the page backdrop and the `ambientGlow` (soft brand radial gradients drawn behind the page) would be hidden, and glass over a flat colour shows almost nothing.

### Mechanical edits to existing files

Replace every card-like surface of the form

```kotlin
.clip(RoundedCornerShape(16.dp))
.background(CardBg)
```

with

```kotlin
.glassSurface(RoundedCornerShape(16.dp), CardBg)
.clip(RoundedCornerShape(16.dp))
```

This was applied to `SubscriptionRow`, `MonthSubCard`, `CategoryBarItem`, the Overview total/year/month cards, the segmented Monthly/Yearly and By Card/By Category toggles, the month pill, and the import/export button. Only radii of 12 dp or more were changed. Thin progress-bar tracks (3 dp) stay flat.

`HeroCard` keeps its brand gradient but at 0.66 to 0.78 alpha on top of glass:

```kotlin
.glassSurface(RoundedCornerShape(20.dp), BrandDark)
.clip(RoundedCornerShape(20.dp))
.background(brush = Brush.linearGradient(colors = listOf(
    BrandDark.copy(alpha = 0.78f), Brand.copy(alpha = 0.78f), BrandDark.copy(alpha = 0.66f))))
```

`SettingsScreen` cards become transparent Material cards with a glass modifier:

```kotlin
Card(
    modifier = Modifier.fillMaxWidth().glassSurface(RoundedCornerShape(16.dp), CardBg),
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = Color.Transparent)
)
```

`SettingsScreen` also changes its signature to `currentThemeMode: ThemeMode` and `onThemeModeChange: (ThemeMode) -> Unit`, and the three option cards compare against `ThemeMode.DARK`, `LIGHT` and `SYSTEM`.

Every edited screen and component needs `import com.example.ui.glass.glassSurface`, and the screens need `import com.example.ui.glass.LocalBottomBarInset`.

## 5. Calendar changes (`OverviewScreen`, `DayCell`)

- `DayCell` uses `glassSurface`; the selected day uses `CardGlass.tint(Brand.copy(alpha = 0.75f))`, and today keeps its brand border.
- The day strip is now a `LazyRow` (`DayStrip`). On month change it waits for the viewport width, then calls `animateScrollToItem(index, scrollOffset = -(viewportWidth - cellWidth) / 2)` to centre today (or day 1 for other months).
- `selectedDay` starts as `today`. The helper `defaultDayFor(year, month, today)` returns today when that month is shown, otherwise `null`. It is used by the month picker and by tapping a month tile.
- If the selected day has no payments, the list falls back to payments later in the same month (`date > selectedDay`). The header uses the new string `nothingDueRemaining(dateStr, count)`. If nothing remains either, the old "no payments" message shows.

New strings (add to `AppStrings` and to both `EnglishStrings` and `RussianStrings`):

```kotlin
val avgPerMonth: String
val peakMonth: String
fun nothingDueRemaining(dateStr: String, count: Int): String

// English
override val avgPerMonth = "Avg / month"
override val peakMonth = "Peak"
override fun nothingDueRemaining(dateStr: String, count: Int) = "Nothing due on $dateStr · $count left this month"

// Russian
override val avgPerMonth = "В среднем / мес."
override val peakMonth = "Пик"
override fun nothingDueRemaining(dateStr: String, count: Int) = "На $dateStr списаний нет · осталось в месяце: $count"
```

## 6. Yearly view redesign

The month list is replaced by:

- glass year-step buttons (`YearStepButton`);
- `YearSummaryCard`: year total, a 12-bar chart with the current month highlighted, average per month, and the peak month;
- a 3-column grid of `MonthTile`s (glass, with total, payment count and a share-of-peak bar). Tapping a tile switches to the monthly view for that month through `onMonthClick`.

`YearlyContentView` now takes `onMonthClick: (Int) -> Unit`, and computes `List<MonthTotal>` with `remember(subscriptions, selectedYear)`. See the full `OverviewScreen` in the repo for the exact code, since it is the one large edited file.

## 7. New files (full contents)

Copy these as they are. Adjust the package name if the other app does not use `com.example`.

### `app/src/main/java/com/example/data/settings/AppSettings.kt`

```kotlin
package com.example.data.settings

enum class ThemeMode(val key: String) {
    DARK("dark"),
    LIGHT("light"),
    SYSTEM("system");

    companion object {
        fun fromKey(key: String?): ThemeMode = entries.firstOrNull { it.key == key } ?: DARK
    }
}

data class AppSettings(
    val themeMode: ThemeMode = ThemeMode.DARK,
    val useMaterialAccent: Boolean = false,
    val primaryCurrency: String = "USD",
    val language: String = "en",
    val dailyNotificationEnabled: Boolean = true,
    val monthlyNotificationEnabled: Boolean = true
)
```

### `app/src/main/java/com/example/data/settings/SettingsRepository.kt`

```kotlin
package com.example.data.settings

import com.example.data.database.SubscriptionDao
import com.example.data.model.SettingEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class SettingsRepository(private val dao: SubscriptionDao) {

    val settings: Flow<AppSettings> = dao.getAllSettings()
        .map { rows -> rows.associate { it.key to it.value }.toAppSettings() }
        .distinctUntilChanged()

    suspend fun current(): AppSettings = settings.first()

    /** Language persisted in the DB, or null when the user never picked one. */
    suspend fun savedLanguage(): String? = dao.getSettingSync(KEY_LANGUAGE)

    suspend fun setThemeMode(mode: ThemeMode) = put(KEY_THEME_MODE, mode.key)

    suspend fun setUseMaterialAccent(use: Boolean) = put(KEY_USE_MATERIAL_ACCENT, use.toString())

    suspend fun setPrimaryCurrency(currency: String) = put(KEY_PRIMARY_CURRENCY, currency)

    suspend fun setLanguage(language: String) = put(KEY_LANGUAGE, language)

    suspend fun setDailyNotificationEnabled(enabled: Boolean) =
        put(KEY_DAILY_NOTIFICATION, enabled.toString())

    suspend fun setMonthlyNotificationEnabled(enabled: Boolean) =
        put(KEY_MONTHLY_NOTIFICATION, enabled.toString())

    private suspend fun put(key: String, value: String) {
        dao.setSetting(SettingEntity(key = key, value = value))
    }

    private fun Map<String, String>.toAppSettings(): AppSettings {
        val defaults = AppSettings()
        return AppSettings(
            themeMode = ThemeMode.fromKey(this[KEY_THEME_MODE]),
            useMaterialAccent = this[KEY_USE_MATERIAL_ACCENT]?.toBooleanStrictOrNull()
                ?: defaults.useMaterialAccent,
            primaryCurrency = this[KEY_PRIMARY_CURRENCY] ?: defaults.primaryCurrency,
            language = this[KEY_LANGUAGE] ?: defaults.language,
            dailyNotificationEnabled = this[KEY_DAILY_NOTIFICATION]?.toBooleanStrictOrNull()
                ?: defaults.dailyNotificationEnabled,
            monthlyNotificationEnabled = this[KEY_MONTHLY_NOTIFICATION]?.toBooleanStrictOrNull()
                ?: defaults.monthlyNotificationEnabled
        )
    }

    private companion object {
        const val KEY_THEME_MODE = "theme_mode"
        const val KEY_USE_MATERIAL_ACCENT = "use_material_accent"
        const val KEY_PRIMARY_CURRENCY = "primary_currency"
        const val KEY_LANGUAGE = "app_language"
        const val KEY_DAILY_NOTIFICATION = "daily_notification_enabled"
        const val KEY_MONTHLY_NOTIFICATION = "monthly_notification_enabled"
    }
}
```

### `app/src/main/java/com/example/data/repository/SubscriptionRepository.kt`

```kotlin
package com.example.data.repository

import com.example.data.database.SubscriptionDao
import com.example.data.model.CardAliasEntity
import com.example.data.model.SettingEntity
import com.example.data.model.SubscriptionEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class SubscriptionRepository(private val dao: SubscriptionDao) {

    val allSubscriptions: Flow<List<SubscriptionEntity>> = dao.getAllSubscriptions()
    val allCardAliases: Flow<List<CardAliasEntity>> = dao.getAllCardAliases()

    val allCardColors: Flow<Map<String, String>> = dao.getSetting(CARD_COLORS_KEY).map { jsonStr ->
        decodeColors(jsonStr)
    }

    suspend fun setCardColor(card: String, colorHex: String) {
        val updated = decodeColors(dao.getSettingSync(CARD_COLORS_KEY)) + (card to colorHex)
        dao.setSetting(SettingEntity(key = CARD_COLORS_KEY, value = Json.encodeToString(updated)))
    }

    suspend fun insertSubscription(subscription: SubscriptionEntity): Long {
        return dao.insertSubscription(subscription)
    }

    suspend fun updateSubscription(subscription: SubscriptionEntity) {
        dao.updateSubscription(subscription)
    }

    suspend fun deleteSubscription(id: Int) {
        dao.deleteSubscription(id)
    }

    suspend fun deleteAllSubscriptions() {
        dao.deleteAllSubscriptions()
    }

    suspend fun setCardAlias(card: String, alias: String) {
        dao.setCardAlias(CardAliasEntity(card = card, alias = alias))
    }

    suspend fun getAllSubscriptionsSync(): List<SubscriptionEntity> {
        return dao.getAllSubscriptionsSync()
    }

    suspend fun getCardAliasSync(card: String): String? {
        return dao.getCardAliasSync(card)
    }

    private fun decodeColors(jsonStr: String?): Map<String, String> {
        if (jsonStr.isNullOrBlank()) return emptyMap()
        return try {
            Json.decodeFromString<Map<String, String>>(jsonStr)
        } catch (_: Exception) {
            emptyMap()
        }
    }

    private companion object {
        const val CARD_COLORS_KEY = "card_colors"
    }
}
```

### `app/src/main/java/com/example/di/AppContainer.kt`

```kotlin
package com.example.di

import android.content.Context
import com.example.data.database.SubscriptionDatabase
import com.example.data.repository.SubscriptionRepository
import com.example.data.settings.SettingsRepository
import com.example.platform.SystemServices

/** Manual dependency graph, created once by [com.example.SubscriptionApp]. */
class AppContainer(context: Context) {
    private val appContext = context.applicationContext
    private val database by lazy { SubscriptionDatabase.getDatabase(appContext) }

    val subscriptionRepository by lazy { SubscriptionRepository(database.subscriptionDao()) }
    val settingsRepository by lazy { SettingsRepository(database.subscriptionDao()) }
    val systemServices by lazy { SystemServices(appContext) }
}
```

### `app/src/main/java/com/example/SubscriptionApp.kt`

```kotlin
package com.example

import android.app.Application
import android.content.Context
import com.example.di.AppContainer

class SubscriptionApp : Application() {
    val container: AppContainer by lazy { AppContainer(this) }
}

val Context.appContainer: AppContainer
    get() = (applicationContext as SubscriptionApp).container
```

### `app/src/main/java/com/example/platform/SystemServices.kt`

```kotlin
package com.example.platform

import android.content.Context
import com.example.data.model.SubscriptionEntity
import com.example.localization.AppLocaleManager
import com.example.service.NotificationHelper
import com.example.service.SubscriptionReminderWorker

/** Android framework calls the ViewModel needs, kept behind one seam so it holds no Context. */
class SystemServices(private val context: Context) {

    fun scheduleDailyReminder() {
        SubscriptionReminderWorker.scheduleDailyReminder(context)
    }

    fun sendReminders(
        subscriptions: List<SubscriptionEntity>,
        currency: String,
        monthlyEnabled: Boolean,
        dailyEnabled: Boolean
    ) {
        NotificationHelper.checkAndSendReminders(
            context, subscriptions, currency, monthlyEnabled, dailyEnabled
        )
    }

    fun systemLanguage(): String? = AppLocaleManager.getSystemAppLanguage(context)

    fun applyLanguage(code: String) {
        AppLocaleManager.setSystemAppLanguage(context, code)
    }
}
```

### `app/src/main/java/com/example/ui/MainUiState.kt`

```kotlin
package com.example.ui

import androidx.compose.runtime.Immutable
import com.example.data.model.SubscriptionEntity
import com.example.data.settings.AppSettings

@Immutable
data class MainUiState(
    val subscriptions: List<SubscriptionEntity> = emptyList(),
    val settings: AppSettings = AppSettings(),
    val cardAliases: Map<String, String> = emptyMap(),
    val cardColors: Map<String, String> = emptyMap(),
    val currentMonthTotal: Double = 0.0
)
```

### `app/src/main/java/com/example/ui/MainViewModel.kt`

```kotlin
package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.appContainer
import com.example.data.model.SubscriptionEntity
import com.example.data.repository.SubscriptionRepository
import com.example.data.settings.SettingsRepository
import com.example.data.settings.ThemeMode
import com.example.logic.SubscriptionCalculations
import com.example.platform.SystemServices
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate

class MainViewModel(
    private val subscriptionRepository: SubscriptionRepository,
    private val settingsRepository: SettingsRepository,
    private val systemServices: SystemServices
) : ViewModel() {

    val uiState: StateFlow<MainUiState> = combine(
        subscriptionRepository.allSubscriptions,
        settingsRepository.settings,
        subscriptionRepository.allCardAliases.map { list -> list.associate { it.card to it.alias } },
        subscriptionRepository.allCardColors
    ) { subscriptions, settings, aliases, colors ->
        MainUiState(
            subscriptions = subscriptions,
            settings = settings,
            cardAliases = aliases,
            cardColors = colors,
            currentMonthTotal = monthTotal(subscriptions)
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), MainUiState())

    init {
        systemServices.scheduleDailyReminder()
        viewModelScope.launch {
            val settings = settingsRepository.current()
            systemServices.sendReminders(
                subscriptionRepository.getAllSubscriptionsSync(),
                settings.primaryCurrency,
                settings.monthlyNotificationEnabled,
                settings.dailyNotificationEnabled
            )
            syncLanguage(applySavedToSystem = true)
        }
    }

    /** Pulls a language change made in system settings back into the app. */
    fun syncLocaleWithSystem() {
        viewModelScope.launch { syncLanguage(applySavedToSystem = false) }
    }

    fun setThemeMode(mode: ThemeMode) {
        viewModelScope.launch { settingsRepository.setThemeMode(mode) }
    }

    fun setUseMaterialAccent(use: Boolean) {
        viewModelScope.launch { settingsRepository.setUseMaterialAccent(use) }
    }

    fun setMonthlyNotificationEnabled(enabled: Boolean) {
        viewModelScope.launch { settingsRepository.setMonthlyNotificationEnabled(enabled) }
    }

    fun setDailyNotificationEnabled(enabled: Boolean) {
        viewModelScope.launch { settingsRepository.setDailyNotificationEnabled(enabled) }
    }

    fun setPrimaryCurrency(currency: String) {
        viewModelScope.launch { settingsRepository.setPrimaryCurrency(currency) }
    }

    fun setAppLanguage(lang: String) {
        viewModelScope.launch {
            settingsRepository.setLanguage(lang)
            systemServices.applyLanguage(lang)
        }
    }

    fun saveSubscription(subscription: SubscriptionEntity) {
        viewModelScope.launch {
            if (subscription.id == 0) {
                subscriptionRepository.insertSubscription(subscription)
            } else {
                subscriptionRepository.updateSubscription(subscription)
            }
        }
    }

    fun deleteSubscription(id: Int) {
        viewModelScope.launch { subscriptionRepository.deleteSubscription(id) }
    }

    fun setCardAlias(card: String, alias: String) {
        viewModelScope.launch { subscriptionRepository.setCardAlias(card, alias) }
    }

    fun setCardColor(card: String, hex: String) {
        viewModelScope.launch { subscriptionRepository.setCardColor(card, hex) }
    }

    fun importSubscriptions(currency: String?, newSubs: List<SubscriptionEntity>, replace: Boolean) {
        viewModelScope.launch {
            if (replace) {
                subscriptionRepository.deleteAllSubscriptions()
            }
            if (!currency.isNullOrBlank()) {
                settingsRepository.setPrimaryCurrency(currency)
            }
            newSubs.forEach { sub ->
                subscriptionRepository.insertSubscription(sub.copy(id = 0))
            }
        }
    }

    private suspend fun syncLanguage(applySavedToSystem: Boolean) {
        val systemLang = systemServices.systemLanguage()
        val savedLang = settingsRepository.savedLanguage()
        if (systemLang != null && systemLang != savedLang) {
            settingsRepository.setLanguage(systemLang)
        } else if (applySavedToSystem && savedLang != null) {
            systemServices.applyLanguage(savedLang)
        }
    }

    private fun monthTotal(subscriptions: List<SubscriptionEntity>): Double {
        val now = LocalDate.now()
        return subscriptions.sumOf { sub ->
            sub.priceUsd * SubscriptionCalculations.occurrencesInMonth(sub, now.year, now.monthValue).size
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val container = checkNotNull(this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY]).appContainer
                MainViewModel(
                    container.subscriptionRepository,
                    container.settingsRepository,
                    container.systemServices
                )
            }
        }
    }
}
```

### `app/src/main/java/com/example/ui/navigation/AppTab.kt`

```kotlin
package com.example.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.FormatListBulleted
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.localization.AppStrings

enum class AppTab(
    val icon: ImageVector,
    val testTag: String,
    val label: (AppStrings) -> String
) {
    SUBSCRIPTIONS(Icons.Default.FormatListBulleted, "nav_item_subscriptions", { it.navSubs }),
    OVERVIEW(Icons.Default.CalendarMonth, "nav_item_overview", { it.navOverview }),
    ANALYTICS(Icons.Default.PieChart, "nav_item_analytics", { it.navAnalytics }),
    SETTINGS(Icons.Default.Settings, "nav_item_settings", { it.navSettings })
}
```

### `app/src/main/java/com/example/ui/glass/GlassLocals.kt`

```kotlin
package com.example.ui.glass

import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/** Space scrolling content must leave at the bottom so it can pass under the floating bar. */
val LocalBottomBarInset = compositionLocalOf { 0.dp }

val FloatingBarHeight: Dp = 64.dp
val FloatingBarMargin: Dp = 12.dp
```

### `app/src/main/java/com/example/ui/glass/GlassSurface.kt`

```kotlin
package com.example.ui.glass

import androidx.compose.foundation.background
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import io.github.itshedimisawi.vitro.Glass
import io.github.itshedimisawi.vitro.GlassBackdrop
import io.github.itshedimisawi.vitro.glassEffect

/** Backdrop that glass surfaces refract; null where no glass host exists (previews, tests). */
val LocalGlassBackdrop = androidx.compose.runtime.compositionLocalOf<GlassBackdrop?> { null }

/** Subtler than [Glass.Regular] so many cards in a list stay calm and legible. */
val CardGlass: Glass = Glass.Regular.copy(
    refractionHeight = 10.dp,
    refractionAmount = 14.dp,
    shadowRadius = 14.dp,
    shadowOffset = 4.dp,
    shadowOpacity = 0.10f
)

/**
 * Draws [shape] as glass over the shared backdrop, or as a flat [fallback] fill when none exists.
 * Apply `.clip(shape)` after it so ripples and content stay inside the glass.
 */
@Composable
fun Modifier.glassSurface(
    shape: CornerBasedShape,
    fallback: Color,
    glass: Glass = CardGlass
): Modifier {
    val backdrop = LocalGlassBackdrop.current ?: return this.clip(shape).background(fallback)
    return this.glassEffect(backdrop, glass, shape)
}
```

### `app/src/main/java/com/example/ui/components/GlassNavBar.kt`

```kotlin
package com.example.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.background
import com.example.localization.LocalStrings
import com.example.theme.Brand
import com.example.ui.glass.FloatingBarHeight
import com.example.ui.navigation.AppTab
import io.github.itshedimisawi.vitro.Glass
import io.github.itshedimisawi.vitro.GlassBackdrop
import io.github.itshedimisawi.vitro.glassEffect
import io.github.itshedimisawi.vitro.rememberGlassEffectState

@Composable
fun GlassNavBar(
    backdrop: GlassBackdrop,
    currentTab: AppTab,
    onTabSelected: (AppTab) -> Unit,
    modifier: Modifier = Modifier
) {
    val strings = LocalStrings.current
    val glassState = rememberGlassEffectState()

    Row(
        modifier = modifier
            .height(FloatingBarHeight)
            .glassEffect(backdrop, Glass.Regular, state = glassState)
            .padding(6.dp)
            .testTag("bottom_nav_bar"),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AppTab.entries.forEach { tab ->
            val selected = tab == currentTab
            val label = tab.label(strings)
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .clip(CircleShape)
                    .background(if (selected) Brand else Color.Transparent)
                    .selectable(selected = selected, role = Role.Tab, onClick = { onTabSelected(tab) })
                    .testTag(tab.testTag),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                val tint = if (selected) Color.White else glassState.contentColor.copy(alpha = 0.6f)
                Icon(
                    imageVector = tab.icon,
                    contentDescription = label,
                    tint = tint,
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    text = label,
                    color = tint,
                    fontSize = 11.sp,
                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
fun GlassFab(
    backdrop: GlassBackdrop,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val glassState = rememberGlassEffectState()
    val interactionSource = remember { MutableInteractionSource() }

    Box(
        modifier = modifier
            .size(FloatingBarHeight)
            .glassEffect(
                backdrop = backdrop,
                glass = Glass.Regular.tint(Brand.copy(alpha = 0.75f)).interactive(),
                state = glassState,
                interactionSource = interactionSource
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                role = Role.Button,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = contentDescription,
            tint = glassState.contentColor,
            modifier = Modifier.size(28.dp)
        )
    }
}
```

### `app/src/main/java/com/example/ui/components/YearlyComponents.kt`

```kotlin
package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.localization.LocalStrings
import com.example.theme.Brand
import com.example.theme.CardBg
import com.example.theme.CardBgElevated
import com.example.theme.TextMuted
import com.example.theme.TextWhite
import com.example.ui.glass.CardGlass
import com.example.ui.glass.glassSurface

/** Money spent in one calendar month, for the yearly view. */
data class MonthTotal(val month: Int, val total: Double, val paymentCount: Int)

@Composable
fun YearSummaryCard(
    year: Int,
    totals: List<MonthTotal>,
    currentMonth: Int?,
    currency: String,
    modifier: Modifier = Modifier
) {
    val strings = LocalStrings.current
    val yearTotal = totals.sumOf { it.total }
    val peak = totals.maxByOrNull { it.total }?.takeIf { it.total > 0.0 }
    val maxTotal = peak?.total ?: 1.0

    Column(
        modifier = modifier
            .fillMaxWidth()
            .glassSurface(RoundedCornerShape(20.dp), CardBgElevated)
            .clip(RoundedCornerShape(20.dp))
            .padding(18.dp)
    ) {
        Text(text = strings.yearTotal(year), color = TextMuted, fontSize = 13.sp)
        Text(
            text = "${"%.2f".format(yearTotal)} $currency",
            color = TextWhite,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.Bottom
        ) {
            totals.forEach { entry ->
                val fraction = (entry.total / maxTotal).toFloat().coerceIn(0.06f, 1f)
                val isCurrent = entry.month == currentMonth
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(fraction)
                        .clip(RoundedCornerShape(5.dp))
                        .background(if (isCurrent) Brand else Brand.copy(alpha = 0.3f))
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(24.dp)) {
            Column {
                Text(text = strings.avgPerMonth, color = TextMuted, fontSize = 12.sp)
                Text(
                    text = "${"%.2f".format(yearTotal / 12.0)} $currency",
                    color = TextWhite,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
            if (peak != null) {
                Column {
                    Text(text = strings.peakMonth, color = TextMuted, fontSize = 12.sp)
                    Text(
                        text = "${strings.getMonthName(peak.month, short = true)} · ${"%.2f".format(peak.total)}",
                        color = Brand,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

@Composable
fun MonthTile(
    entry: MonthTotal,
    maxTotal: Double,
    isCurrent: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val strings = LocalStrings.current
    val shape = RoundedCornerShape(18.dp)
    val glass = if (isCurrent) CardGlass.tint(Brand.copy(alpha = 0.2f)) else CardGlass
    val fraction = if (maxTotal > 0.0) (entry.total / maxTotal).toFloat().coerceIn(0f, 1f) else 0f

    Column(
        modifier = modifier
            .glassSurface(shape, CardBg, glass)
            .clip(shape)
            .clickable(onClick = onClick)
            .padding(12.dp)
            .testTag("month_tile_${entry.month}")
    ) {
        Text(
            text = strings.getMonthName(entry.month, short = true),
            color = if (isCurrent) Brand else TextWhite,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = "%.2f".format(entry.total),
            color = TextWhite,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = strings.paymentsCount(entry.paymentCount),
            color = TextMuted,
            fontSize = 11.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Spacer(modifier = Modifier.height(10.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(5.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(CardBgElevated)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(fraction)
                    .clip(RoundedCornerShape(3.dp))
                    .background(Brand)
            )
        }
    }
}
```

### `app/src/main/java/com/example/ui/MainApp.kt`

```kotlin
package com.example.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.SubscriptionEntity
import com.example.localization.LocalStrings
import com.example.localization.getAppStrings
import com.example.theme.AppTheme
import com.example.theme.WindowBg
import com.example.ui.components.GlassFab
import com.example.ui.components.GlassNavBar
import com.example.ui.dialogs.AddEditSubscriptionDialog
import com.example.ui.dialogs.ImportExportDialog
import com.example.ui.glass.FloatingBarHeight
import com.example.ui.glass.FloatingBarMargin
import com.example.ui.glass.LocalBottomBarInset
import com.example.ui.glass.LocalGlassBackdrop
import com.example.ui.navigation.AppTab
import com.example.ui.screens.AnalyticsScreen
import com.example.ui.screens.OverviewScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.SubscriptionsScreen
import io.github.itshedimisawi.vitro.LocalGlassDarkTheme
import io.github.itshedimisawi.vitro.ScrollEdgeEffectStyle
import io.github.itshedimisawi.vitro.glassBackdrop
import io.github.itshedimisawi.vitro.glassEffectContainer
import io.github.itshedimisawi.vitro.rememberGlassBackdrop
import io.github.itshedimisawi.vitro.scrollEdgeEffect

@Composable
fun MainApp(
    viewModel: MainViewModel,
    requestedTab: AppTab?,
    onRequestedTabConsumed: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val settings = uiState.settings
    val strings = remember(settings.language) { getAppStrings(settings.language) }

    var currentTab by rememberSaveable { mutableStateOf(AppTab.SUBSCRIPTIONS) }
    var editingSubscription by remember { mutableStateOf<SubscriptionEntity?>(null) }
    var showAddDialog by remember { mutableStateOf(false) }
    var showImportExportDialog by remember { mutableStateOf(false) }

    LaunchedEffect(requestedTab) {
        requestedTab?.let {
            currentTab = it
            onRequestedTabConsumed()
        }
    }

    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) { viewModel.syncLocaleWithSystem() }
    NotificationPermissionEffect()

    val subsListState = rememberLazyListState()
    val overviewListState = rememberLazyListState()
    val isScrolling by remember {
        derivedStateOf { currentTab == AppTab.SUBSCRIPTIONS && subsListState.isScrollInProgress }
    }

    val backdrop = rememberGlassBackdrop()
    val navigationBarInset = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
    val bottomBarInset = navigationBarInset + FloatingBarHeight + FloatingBarMargin * 2

    CompositionLocalProvider(
        LocalStrings provides strings,
        LocalGlassDarkTheme provides AppTheme.colors.isDark,
        LocalGlassBackdrop provides backdrop,
        LocalBottomBarInset provides bottomBarInset
    ) {
        if (showAddDialog || editingSubscription != null) {
            val closeDialog = {
                showAddDialog = false
                editingSubscription = null
            }
            AddEditSubscriptionDialog(
                subscription = editingSubscription,
                primaryCurrency = settings.primaryCurrency,
                onDismiss = closeDialog,
                onSave = { sub ->
                    viewModel.saveSubscription(sub)
                    closeDialog()
                },
                onDelete = { id ->
                    viewModel.deleteSubscription(id)
                    closeDialog()
                }
            )
        }

        if (showImportExportDialog) {
            ImportExportDialog(
                subscriptions = uiState.subscriptions,
                primaryCurrency = settings.primaryCurrency,
                onDismiss = { showImportExportDialog = false },
                onImportSuccess = { newCurrency, importedSubs, replace ->
                    viewModel.importSubscriptions(newCurrency, importedSubs, replace)
                }
            )
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(WindowBg)
                .testTag("main_scaffold")
        ) {
            // Content is the backdrop; the floating bar below refracts whatever scrolls under it.
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .glassBackdrop(backdrop)
                    .scrollEdgeEffect(style = ScrollEdgeEffectStyle.Soft, color = WindowBg)
                    .background(WindowBg)
                    .ambientGlow(AppTheme.colors.brand)
                    .statusBarsPadding()
            ) {
                when (currentTab) {
                    AppTab.SUBSCRIPTIONS -> SubscriptionsScreen(
                        subscriptions = uiState.subscriptions,
                        primaryCurrency = settings.primaryCurrency,
                        currentMonthTotal = uiState.currentMonthTotal,
                        onOpenImportExport = { showImportExportDialog = true },
                        onSubscriptionClick = { editingSubscription = it },
                        listState = subsListState
                    )
                    AppTab.OVERVIEW -> OverviewScreen(
                        subscriptions = uiState.subscriptions,
                        primaryCurrency = settings.primaryCurrency,
                        onSubscriptionClick = { editingSubscription = it },
                        listState = overviewListState
                    )
                    AppTab.ANALYTICS -> AnalyticsScreen(
                        subscriptions = uiState.subscriptions,
                        cardAliases = uiState.cardAliases,
                        primaryCurrency = settings.primaryCurrency,
                        onSaveCardAlias = viewModel::setCardAlias,
                        cardColors = uiState.cardColors,
                        onSaveCardColor = viewModel::setCardColor
                    )
                    AppTab.SETTINGS -> SettingsScreen(
                        currentThemeMode = settings.themeMode,
                        onThemeModeChange = viewModel::setThemeMode,
                        useMaterialAccent = settings.useMaterialAccent,
                        onUseMaterialAccentChange = viewModel::setUseMaterialAccent,
                        primaryCurrency = settings.primaryCurrency,
                        onCurrencyChange = viewModel::setPrimaryCurrency,
                        onOpenImportExport = { showImportExportDialog = true },
                        subscriptionCount = uiState.subscriptions.size,
                        currentLanguage = settings.language,
                        onLanguageChange = viewModel::setAppLanguage,
                        dailyNotificationEnabled = settings.dailyNotificationEnabled,
                        onDailyNotificationChange = viewModel::setDailyNotificationEnabled,
                        monthlyNotificationEnabled = settings.monthlyNotificationEnabled,
                        onMonthlyNotificationChange = viewModel::setMonthlyNotificationEnabled
                    )
                }
            }

            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(horizontal = 16.dp, vertical = FloatingBarMargin)
                    .glassEffectContainer(),
                horizontalArrangement = Arrangement.spacedBy(FloatingBarMargin),
                verticalAlignment = Alignment.CenterVertically
            ) {
                GlassNavBar(
                    backdrop = backdrop,
                    currentTab = currentTab,
                    onTabSelected = { currentTab = it },
                    modifier = Modifier.weight(1f)
                )
                // Scroll-aware: hides while the list scrolls, returns when it settles.
                AnimatedVisibility(
                    visible = !isScrolling && currentTab == AppTab.SUBSCRIPTIONS,
                    enter = scaleIn() + fadeIn(),
                    exit = scaleOut() + fadeOut()
                ) {
                    GlassFab(
                        backdrop = backdrop,
                        contentDescription = strings.addSubscription,
                        onClick = {
                            editingSubscription = null
                            showAddDialog = true
                        },
                        modifier = Modifier.testTag("add_subscription_fab")
                    )
                }
            }
        }
    }
}

@Composable
private fun NotificationPermissionEffect() {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { }
    LaunchedEffect(Unit) {
        val granted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED
        if (!granted) launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
    }
}

/** Soft brand-coloured light behind the page, so glass has something to refract. */
private fun Modifier.ambientGlow(color: Color): Modifier = drawBehind {
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(color.copy(alpha = 0.35f), Color.Transparent),
            center = Offset(size.width * 0.9f, size.height * 0.12f),
            radius = size.width * 0.8f
        ),
        radius = size.width * 0.8f,
        center = Offset(size.width * 0.9f, size.height * 0.12f)
    )
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(color.copy(alpha = 0.25f), Color.Transparent),
            center = Offset(size.width * 0.05f, size.height * 0.62f),
            radius = size.width * 0.9f
        ),
        radius = size.width * 0.9f,
        center = Offset(size.width * 0.05f, size.height * 0.62f)
    )
}
```

### `app/src/main/java/com/example/MainActivity.kt`

```kotlin
package com.example

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.theme.SubscriptionTrackerTheme
import com.example.ui.MainApp
import com.example.ui.MainViewModel
import com.example.ui.navigation.AppTab

class MainActivity : ComponentActivity() {

    companion object {
        const val EXTRA_NAV_TAB = "extra_nav_tab"
        const val TAB_OVERVIEW = "overview"
    }

    private val viewModel: MainViewModel by viewModels { MainViewModel.Factory }

    /** Tab requested by a launching intent (e.g. a notification), consumed by the UI once applied. */
    private var requestedTab by mutableStateOf<AppTab?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) handleIntent(intent)
        setContent {
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()
            SubscriptionTrackerTheme(
                themeMode = uiState.settings.themeMode,
                useMaterialAccent = uiState.settings.useMaterialAccent
            ) {
                MainApp(
                    viewModel = viewModel,
                    requestedTab = requestedTab,
                    onRequestedTabConsumed = { requestedTab = null }
                )
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent?) {
        if (intent?.getStringExtra(EXTRA_NAV_TAB) == TAB_OVERVIEW) {
            requestedTab = AppTab.OVERVIEW
        }
    }
}
```

## 8. Verification checklist

1. Sync and build; resolve any KSP or Material3 version errors first.
2. Tap a notification and confirm it opens the Overview tab, including when the app was already running.
3. Rotate the device and confirm the selected tab survives.
4. Toggle theme (dark, light, system) and confirm the glass follows the app theme, not the system theme.
5. Open the Overview tab on the current month: today should be selected and centred. Select a day with no payments and confirm the remaining-payments fallback.
6. Open the yearly view and tap a month tile.
7. Scroll long lists and watch frame time: every card is its own glass layer, and weak devices may need `Glass.Identity` or flat fills for list items.
