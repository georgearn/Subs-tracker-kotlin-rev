package com.example

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.FormatListBulleted
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.model.SubscriptionEntity
import com.example.localization.LocalStrings
import com.example.localization.getAppStrings
import com.example.service.SubscriptionReminderWorker
import com.example.theme.Brand
import com.example.theme.CardBg
import com.example.theme.SubscriptionTrackerTheme
import com.example.theme.TextMuted
import com.example.theme.TextWhite
import com.example.theme.WindowBg
import com.example.ui.MainViewModel
import com.example.ui.dialogs.AddEditSubscriptionDialog
import com.example.ui.dialogs.ImportExportDialog
import com.example.ui.screens.AnalyticsScreen
import com.example.ui.screens.OverviewScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.SubscriptionsScreen

enum class AppTab {
    SUBSCRIPTIONS,
    OVERVIEW,
    ANALYTICS,
    SETTINGS
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        SubscriptionReminderWorker.scheduleDailyReminder(this)
        setContent {
            val viewModel: MainViewModel = viewModel(
                factory = MainViewModel.provideFactory(application)
            )
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
}

@Composable
fun MainApp(
    viewModel: MainViewModel,
    themeMode: String,
    useMaterialAccent: Boolean
) {
    val context = LocalContext.current

    val subscriptions by viewModel.subscriptions.collectAsStateWithLifecycle()
    val primaryCurrency by viewModel.primaryCurrency.collectAsStateWithLifecycle()
    val cardAliases by viewModel.cardAliases.collectAsStateWithLifecycle()
    val cardColors by viewModel.cardColors.collectAsStateWithLifecycle()
    val currentMonthTotal by viewModel.currentMonthTotal.collectAsStateWithLifecycle()
    val appLanguage by viewModel.appLanguage.collectAsStateWithLifecycle()

    val strings = remember(appLanguage) { getAppStrings(appLanguage) }

    var currentTab by remember { mutableStateOf(AppTab.SUBSCRIPTIONS) }
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

    CompositionLocalProvider(LocalStrings provides strings) {
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
                    viewModel.deleteSubscription(id)
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

        Scaffold(
            modifier = Modifier
                .fillMaxSize()
                .testTag("main_scaffold"),
            contentWindowInsets = WindowInsets(0.dp, 0.dp, 0.dp, 0.dp),
            containerColor = WindowBg,
            bottomBar = {
                NavigationBar(
                    containerColor = CardBg,
                    contentColor = TextWhite,
                    modifier = Modifier
                        .navigationBarsPadding()
                        .testTag("bottom_nav_bar")
                ) {
                    NavigationBarItem(
                        selected = currentTab == AppTab.SUBSCRIPTIONS,
                        onClick = { currentTab = AppTab.SUBSCRIPTIONS },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.FormatListBulleted,
                                contentDescription = strings.navSubs,
                                modifier = Modifier.size(24.dp)
                            )
                        },
                        label = {
                            Text(
                                text = strings.navSubs,
                                fontSize = 12.sp,
                                fontWeight = if (currentTab == AppTab.SUBSCRIPTIONS) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.White,
                            selectedTextColor = Brand,
                            indicatorColor = Brand,
                            unselectedIconColor = TextMuted,
                            unselectedTextColor = TextMuted
                        ),
                        modifier = Modifier.testTag("nav_item_subscriptions")
                    )

                    NavigationBarItem(
                        selected = currentTab == AppTab.OVERVIEW,
                        onClick = { currentTab = AppTab.OVERVIEW },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.CalendarMonth,
                                contentDescription = strings.navOverview,
                                modifier = Modifier.size(24.dp)
                            )
                        },
                        label = {
                            Text(
                                text = strings.navOverview,
                                fontSize = 12.sp,
                                fontWeight = if (currentTab == AppTab.OVERVIEW) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.White,
                            selectedTextColor = Brand,
                            indicatorColor = Brand,
                            unselectedIconColor = TextMuted,
                            unselectedTextColor = TextMuted
                        ),
                        modifier = Modifier.testTag("nav_item_overview")
                    )

                    NavigationBarItem(
                        selected = currentTab == AppTab.ANALYTICS,
                        onClick = { currentTab = AppTab.ANALYTICS },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.PieChart,
                                contentDescription = strings.navAnalytics,
                                modifier = Modifier.size(24.dp)
                            )
                        },
                        label = {
                            Text(
                                text = strings.navAnalytics,
                                fontSize = 12.sp,
                                fontWeight = if (currentTab == AppTab.ANALYTICS) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.White,
                            selectedTextColor = Brand,
                            indicatorColor = Brand,
                            unselectedIconColor = TextMuted,
                            unselectedTextColor = TextMuted
                        ),
                        modifier = Modifier.testTag("nav_item_analytics")
                    )

                    NavigationBarItem(
                        selected = currentTab == AppTab.SETTINGS,
                        onClick = { currentTab = AppTab.SETTINGS },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = strings.navSettings,
                                modifier = Modifier.size(24.dp)
                            )
                        },
                        label = {
                            Text(
                                text = strings.navSettings,
                                fontSize = 12.sp,
                                fontWeight = if (currentTab == AppTab.SETTINGS) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.White,
                            selectedTextColor = Brand,
                            indicatorColor = Brand,
                            unselectedIconColor = TextMuted,
                            unselectedTextColor = TextMuted
                        ),
                        modifier = Modifier.testTag("nav_item_settings")
                    )
                }
            },
            floatingActionButton = {
                // Scroll-aware FAB: disappears during scroll, reappears when scrolling stops
                AnimatedVisibility(
                    visible = !isScrolling && currentTab == AppTab.SUBSCRIPTIONS,
                    enter = scaleIn() + fadeIn(),
                    exit = scaleOut() + fadeOut()
                ) {
                    FloatingActionButton(
                        onClick = {
                            activeDialogSubscription = null
                            showAddDialog = true
                        },
                        containerColor = Brand,
                        contentColor = Color.White,
                        shape = CircleShape,
                        elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 6.dp),
                        modifier = Modifier.testTag("add_subscription_fab")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = strings.addSubscription,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .padding(bottom = innerPadding.calculateBottomPadding())
            ) {
                when (currentTab) {
                    AppTab.SUBSCRIPTIONS -> {
                        SubscriptionsScreen(
                            subscriptions = subscriptions,
                            primaryCurrency = primaryCurrency,
                            currentMonthTotal = currentMonthTotal,
                            onOpenImportExport = { showImportExportDialog = true },
                            onSubscriptionClick = { sub ->
                                activeDialogSubscription = sub
                            },
                            listState = subsListState
                        )
                    }
                    AppTab.OVERVIEW -> {
                        OverviewScreen(
                            subscriptions = subscriptions,
                            primaryCurrency = primaryCurrency,
                            onSubscriptionClick = { sub ->
                                activeDialogSubscription = sub
                            },
                            listState = overviewListState
                        )
                    }
                    AppTab.ANALYTICS -> {
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
                    AppTab.SETTINGS -> {
                        SettingsScreen(
                            currentThemeMode = themeMode,
                            onThemeModeChange = { viewModel.setThemeMode(it) },
                            useMaterialAccent = useMaterialAccent,
                            onUseMaterialAccentChange = { viewModel.setUseMaterialAccent(it) },
                            primaryCurrency = primaryCurrency,
                            onCurrencyChange = { viewModel.setPrimaryCurrency(it) },
                            onOpenImportExport = { showImportExportDialog = true },
                            onSendTestNotification = { viewModel.sendTestNotification() },
                            subscriptionCount = subscriptions.size,
                            currentLanguage = appLanguage,
                            onLanguageChange = { viewModel.setAppLanguage(it) }
                        )
                    }
                }
            }
        }
    }
}
