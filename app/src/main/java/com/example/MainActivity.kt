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
