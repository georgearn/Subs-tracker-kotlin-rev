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
