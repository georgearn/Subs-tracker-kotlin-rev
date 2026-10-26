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
