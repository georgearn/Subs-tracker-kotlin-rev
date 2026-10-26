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
