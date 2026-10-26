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
