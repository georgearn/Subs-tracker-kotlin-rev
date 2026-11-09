package com.example

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.automirrored.filled.FormatListBulleted
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.Settings
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.layout
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.model.SubscriptionEntity
import com.example.localization.LocalStrings
import com.example.localization.getAppStrings
import com.example.service.SubscriptionReminderWorker
import com.example.theme.AppTheme
import com.example.theme.SubscriptionTrackerTheme
import com.example.theme.WindowBg
import com.example.ui.MainViewModel
import com.example.ui.components.GlassFab
import com.example.ui.components.GlassNavBar
import com.example.ui.components.GlassNavItem
import com.example.ui.dialogs.AddEditSubscriptionDialog
import com.example.ui.dialogs.ImportExportDialog
import com.example.ui.glass.FloatingBarHeight
import com.example.ui.glass.FloatingBarMargin
import com.example.ui.glass.LocalBottomBarInset
import com.example.ui.glass.LocalGlassBackdrop
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

enum class AppTab {
    SUBSCRIPTIONS,
    OVERVIEW,
    ANALYTICS,
    SETTINGS
}

class MainActivity : ComponentActivity() {

    companion object {
        const val EXTRA_NAV_TAB = "extra_nav_tab"
        const val TAB_OVERVIEW = "overview"
        const val TAB_ANALYTICS = "analytics"
    }

    private var viewModelInstance: MainViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        SubscriptionReminderWorker.scheduleDailyReminder(this)
        setContent {
            val viewModel: MainViewModel = viewModel(
                factory = MainViewModel.provideFactory(application)
            )
            viewModelInstance = viewModel

            androidx.compose.runtime.LaunchedEffect(Unit) {
                handleIntent(intent)
            }

            val themeMode by viewModel.themeMode.collectAsStateWithLifecycle()
            val useMaterialAccent by viewModel.useMaterialAccent.collectAsStateWithLifecycle()

            SubscriptionTrackerTheme(
                themeMode = themeMode,
                useMaterialAccent = useMaterialAccent
            ) {
                MainApp(viewModel = viewModel, themeMode = themeMode, useMaterialAccent = useMaterialAccent)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModelInstance?.syncLocaleWithSystem()
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent?) {
        val target = intent?.getStringExtra(EXTRA_NAV_TAB)
        when (target) {
            TAB_OVERVIEW -> viewModelInstance?.navigateToTab(AppTab.OVERVIEW)
            TAB_ANALYTICS -> viewModelInstance?.navigateToTab(AppTab.ANALYTICS)
        }
    }
}

@Composable
fun MainApp(
    viewModel: MainViewModel,
    themeMode: String,
    useMaterialAccent: Boolean
) {
    val context = LocalContext.current

    val subscriptions by viewModel.subscriptions.collectAsStateWithLifecycle()
    val activeSubscriptions by viewModel.activeSubscriptions.collectAsStateWithLifecycle()
    val historySubscriptions by viewModel.historySubscriptions.collectAsStateWithLifecycle()
    val primaryCurrency by viewModel.primaryCurrency.collectAsStateWithLifecycle()
    val cardAliases by viewModel.cardAliases.collectAsStateWithLifecycle()
    val cardColors by viewModel.cardColors.collectAsStateWithLifecycle()
    val currentMonthTotal by viewModel.currentMonthTotal.collectAsStateWithLifecycle()
    val appLanguage by viewModel.appLanguage.collectAsStateWithLifecycle()
    val monthlyNotificationEnabled by viewModel.monthlyNotificationEnabled.collectAsStateWithLifecycle()
    val dailyNotificationEnabled by viewModel.dailyNotificationEnabled.collectAsStateWithLifecycle()

    val strings = remember(appLanguage) { getAppStrings(appLanguage) }

    var currentTab by rememberSaveable { mutableStateOf(AppTab.SUBSCRIPTIONS) }
    val targetTab by viewModel.targetTab.collectAsStateWithLifecycle()

    LaunchedEffect(targetTab) {
        targetTab?.let {
            currentTab = it
            viewModel.onTabNavigated()
        }
    }
    var activeDialogSubscription by remember { mutableStateOf<SubscriptionEntity?>(null) }
    var showAddDialog by remember { mutableStateOf(false) }
    var showImportExportDialog by remember { mutableStateOf(false) }

    // Scroll states for scroll-aware FAB
    val subsListState = rememberLazyListState()
    val overviewListState = rememberLazyListState()

    val isScrolling by remember {
        derivedStateOf {
            if (currentTab == AppTab.SUBSCRIPTIONS) subsListState.isScrollInProgress else false
        }
    }

    // Request POST_NOTIFICATIONS permission on Android 13+
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { _ -> }

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val isGranted = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
            if (!isGranted) {
                permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
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
        // Fallback to Subscriptions screen on back gesture from other screens instead of closing app
        BackHandler(
            enabled = currentTab != AppTab.SUBSCRIPTIONS && !showAddDialog && activeDialogSubscription == null && !showImportExportDialog
        ) {
            currentTab = AppTab.SUBSCRIPTIONS
        }

        // Add / Edit Dialog
        if (showAddDialog || activeDialogSubscription != null) {
            AddEditSubscriptionDialog(
                subscription = activeDialogSubscription,
                primaryCurrency = primaryCurrency,
                onDismiss = {
                    showAddDialog = false
                    activeDialogSubscription = null
                },
                onSave = { sub ->
                    viewModel.saveSubscription(sub)
                    showAddDialog = false
                    activeDialogSubscription = null
                },
                onDelete = { id ->
                    // Deleting from the edit dialog cancels it, same as deleting it from the
                    // list — it survives in the History tab as a cancelled entry.
                    val target = activeDialogSubscription?.takeIf { it.id == id }
                    if (target != null) {
                        viewModel.cancelSubscription(target)
                    } else {
                        viewModel.deleteSubscription(id)
                    }
                    showAddDialog = false
                    activeDialogSubscription = null
                },
                onPriceChangeSplit = { closedOutOld, newSubscription ->
                    viewModel.applyPriceChange(closedOutOld, newSubscription)
                    showAddDialog = false
                    activeDialogSubscription = null
                }
            )
        }

        // Import / Export Dialog
        if (showImportExportDialog) {
            ImportExportDialog(
                subscriptions = subscriptions,
                primaryCurrency = primaryCurrency,
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
                    .background(WindowBg)
                    .ambientGlow(AppTheme.colors.brand)
                    .statusBarsPadding()
            ) {
                    KeepAliveTab(currentTab == AppTab.SUBSCRIPTIONS) {
                        SubscriptionsScreen(
                            // Active only — a price edit leaves the old, closed-out row in
                            // the database (for past months' math) but it shouldn't show up
                            // here as a second, stale copy of the same subscription.
                            subscriptions = activeSubscriptions,
                            historySubscriptions = historySubscriptions,
                            primaryCurrency = primaryCurrency,
                            currentMonthTotal = currentMonthTotal,
                            onOpenImportExport = { showImportExportDialog = true },
                            onSubscriptionClick = { sub ->
                                activeDialogSubscription = sub
                            },
                            onDeleteSubscription = { sub ->
                                viewModel.cancelSubscription(sub)
                            },
                            onDeleteHistoryEntry = { id ->
                                viewModel.deleteSubscription(id)
                            },
                            listState = subsListState
                        )
                    }
                    KeepAliveTab(currentTab == AppTab.OVERVIEW) {
                        OverviewScreen(
                            subscriptions = subscriptions,
                            primaryCurrency = primaryCurrency,
                            onSubscriptionClick = { sub ->
                                activeDialogSubscription = sub
                            },
                            listState = overviewListState
                        )
                    }
                    KeepAliveTab(currentTab == AppTab.ANALYTICS) {
                        AnalyticsScreen(
                            subscriptions = subscriptions,
                            cardAliases = cardAliases,
                            primaryCurrency = primaryCurrency,
                            onSaveCardAlias = { card, alias ->
                                viewModel.setCardAlias(card, alias)
                            },
                            cardColors = cardColors,
                            onSaveCardColor = { card, colorHex ->
                                viewModel.setCardColor(card, colorHex)
                            }
                        )
                    }
                    KeepAliveTab(currentTab == AppTab.SETTINGS) {
                        SettingsScreen(
                            currentThemeMode = themeMode,
                            onThemeModeChange = { viewModel.setThemeMode(it) },
                            useMaterialAccent = useMaterialAccent,
                            onUseMaterialAccentChange = { viewModel.setUseMaterialAccent(it) },
                            primaryCurrency = primaryCurrency,
                            onCurrencyChange = { viewModel.setPrimaryCurrency(it) },
                            onOpenImportExport = { showImportExportDialog = true },
                            subscriptionCount = subscriptions.size,
                            currentLanguage = appLanguage,
                            onLanguageChange = { viewModel.setAppLanguage(it) },
                            dailyNotificationEnabled = dailyNotificationEnabled,
                            onDailyNotificationChange = { viewModel.setDailyNotificationEnabled(it) },
                            monthlyNotificationEnabled = monthlyNotificationEnabled,
                            onMonthlyNotificationChange = { viewModel.setMonthlyNotificationEnabled(it) }
                        )
                    }
            }

            val navItems = listOf(
                GlassNavItem(AppTab.SUBSCRIPTIONS.ordinal, strings.navSubs, Icons.AutoMirrored.Filled.FormatListBulleted, "nav_item_subscriptions"),
                GlassNavItem(AppTab.OVERVIEW.ordinal, strings.navOverview, Icons.Default.CalendarMonth, "nav_item_overview"),
                GlassNavItem(AppTab.ANALYTICS.ordinal, strings.navAnalytics, Icons.Default.PieChart, "nav_item_analytics"),
                GlassNavItem(AppTab.SETTINGS.ordinal, strings.navSettings, Icons.Default.Settings, "nav_item_settings")
            )

            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(horizontal = 16.dp, vertical = FloatingBarMargin)
                    .glassEffectContainer(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                GlassNavBar(
                    backdrop = backdrop,
                    items = navItems,
                    selectedItemId = currentTab.ordinal,
                    onItemSelected = { currentTab = AppTab.entries[it] },
                    modifier = Modifier.weight(1f)
                )
                // Scroll-aware FAB: its slot width, scale and alpha share one progress value, so
                // the nav bar glides to its new width instead of snapping when the FAB leaves.
                val fabVisible = currentTab == AppTab.SUBSCRIPTIONS
                val fabProgress by animateFloatAsState(
                    targetValue = if (fabVisible) 1f else 0f,
                    animationSpec = tween(durationMillis = 180, easing = FastOutSlowInEasing),
                    label = "fab_progress"
                )
                if (fabProgress > 0f) {
                    Box(
                        modifier = Modifier
                            .width((FloatingBarHeight + FloatingBarMargin) * fabProgress)
                            .height(FloatingBarHeight)
                    ) {
                        // The glass shader ignores layer alpha, so it would linger as a ghost
                        // circle while the slot shrinks; drop it once it is mostly gone.
                        if (fabProgress > 0.5f) {
                            GlassFab(
                                backdrop = backdrop,
                                contentDescription = strings.addSubscription,
                                onClick = {
                                    activeDialogSubscription = null
                                    showAddDialog = true
                                },
                                modifier = Modifier
                                    .align(Alignment.CenterEnd)
                                    .requiredSize(FloatingBarHeight)
                                    .graphicsLayer {
                                        scaleX = 0.6f + 0.4f * fabProgress
                                        scaleY = 0.6f + 0.4f * fabProgress
                                    }
                                    .testTag("add_subscription_fab")
                            )
                        }
                    }
                }
            }
        }
    }
}

/** Composes a tab only while it is the selected one. */
@Composable
private fun KeepAliveTab(visible: Boolean, content: @Composable () -> Unit) {
    if (visible) {
        Box(modifier = Modifier.fillMaxSize()) { content() }
    }
}

/** Soft brand-coloured light behind the page, so glass has something to refract. */
private fun Modifier.ambientGlow(color: Color): Modifier = drawBehind {
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(color.copy(alpha = 0.2f), Color.Transparent),
            center = Offset(size.width * 0.9f, size.height * 0.12f),
            radius = size.width * 0.8f
        ),
        radius = size.width * 0.8f,
        center = Offset(size.width * 0.9f, size.height * 0.12f)
    )
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(color.copy(alpha = 0.14f), Color.Transparent),
            center = Offset(size.width * 0.05f, size.height * 0.62f),
            radius = size.width * 0.9f
        ),
        radius = size.width * 0.9f,
        center = Offset(size.width * 0.05f, size.height * 0.62f)
    )
}
