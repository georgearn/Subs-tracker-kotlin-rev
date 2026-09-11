package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.database.SubscriptionDatabase
import com.example.data.model.CardAliasEntity
import com.example.data.model.SubscriptionEntity
import com.example.data.repository.SubscriptionRepository
import com.example.localization.AppLocaleManager
import com.example.logic.SubscriptionCalculations
import com.example.service.NotificationHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate

class MainViewModel(
    application: Application,
    private val repository: SubscriptionRepository
) : AndroidViewModel(application) {

    val subscriptions: StateFlow<List<SubscriptionEntity>> = repository.allSubscriptions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val primaryCurrency: StateFlow<String> = repository.getPrimaryCurrency()
        .map { it ?: "USD" }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "USD")

    val cardAliases: StateFlow<Map<String, String>> = repository.allCardAliases
        .map { list -> list.associate { it.card to it.alias } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyMap())

    val currentMonthTotal: StateFlow<Double> = combine(subscriptions, primaryCurrency) { subs, _ ->
        val now = LocalDate.now()
        subs.sumOf { sub ->
            val count = SubscriptionCalculations.occurrencesInMonth(sub, now.year, now.monthValue).size
            sub.priceUsd * count
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val themeMode: StateFlow<String> = repository.getThemeMode()
        .map { it ?: "dark" }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "dark")

    val useMaterialAccent: StateFlow<Boolean> = repository.getUseMaterialAccent()
        .map { it?.toBooleanStrictOrNull() ?: false }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val appLanguage: StateFlow<String> = repository.getAppLanguage()
        .map { it ?: "en" }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "en")

    val monthlyNotificationEnabled: StateFlow<Boolean> = repository.getMonthlyNotificationEnabled()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    val dailyNotificationEnabled: StateFlow<Boolean> = repository.getDailyNotificationEnabled()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    val cardColors: StateFlow<Map<String, String>> = repository.allCardColors
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyMap())

    private val _targetTab = MutableStateFlow<com.example.AppTab?>(null)
    val targetTab: StateFlow<com.example.AppTab?> = _targetTab.asStateFlow()

    fun navigateToTab(tab: com.example.AppTab) {
        _targetTab.value = tab
    }

    fun onTabNavigated() {
        _targetTab.value = null
    }

    init {
        // Trigger notification check and sync per-app locale
        viewModelScope.launch {
            val subs = repository.getAllSubscriptionsSync()
            val cur = repository.getPrimaryCurrencySync()
            val monthly = repository.getMonthlyNotificationEnabledSync()
            val daily = repository.getDailyNotificationEnabledSync()
            NotificationHelper.checkAndSendReminders(getApplication(), subs, cur, monthly, daily)

            val systemLang = AppLocaleManager.getSystemAppLanguage(getApplication())
            val savedLang = repository.getAppLanguageSync()
            if (systemLang != null && systemLang != savedLang) {
                repository.setAppLanguage(systemLang)
            } else if (savedLang != null) {
                AppLocaleManager.setSystemAppLanguage(getApplication(), savedLang)
            }
        }
    }

    fun syncLocaleWithSystem() {
        viewModelScope.launch {
            val systemLang = AppLocaleManager.getSystemAppLanguage(getApplication())
            val savedLang = repository.getAppLanguageSync()
            if (systemLang != null && systemLang != savedLang) {
                repository.setAppLanguage(systemLang)
            }
        }
    }

    fun setThemeMode(mode: String) {
        viewModelScope.launch {
            repository.setThemeMode(mode)
        }
    }

    fun setUseMaterialAccent(use: Boolean) {
        viewModelScope.launch {
            repository.setUseMaterialAccent(use)
        }
    }

    fun setMonthlyNotificationEnabled(enabled: Boolean) {
        viewModelScope.launch {
            repository.setMonthlyNotificationEnabled(enabled)
        }
    }

    fun setDailyNotificationEnabled(enabled: Boolean) {
        viewModelScope.launch {
            repository.setDailyNotificationEnabled(enabled)
        }
    }

    fun sendTestMonthlyNotification() {
        viewModelScope.launch {
            val subs = repository.getAllSubscriptionsSync()
            val cur = repository.getPrimaryCurrencySync()
            NotificationHelper.sendTestMonthlyNotification(getApplication(), subs, cur)
        }
    }

    fun sendTestNotification() {
        viewModelScope.launch {
            val subs = repository.getAllSubscriptionsSync()
            val cur = repository.getPrimaryCurrencySync()
            NotificationHelper.sendTestNotification(getApplication(), subs, cur)
        }
    }

    fun setPrimaryCurrency(currency: String) {
        viewModelScope.launch {
            repository.setPrimaryCurrency(currency)
        }
    }

    fun saveSubscription(subscription: SubscriptionEntity) {
        viewModelScope.launch {
            if (subscription.id == 0) {
                repository.insertSubscription(subscription)
            } else {
                repository.updateSubscription(subscription)
            }
        }
    }

    fun deleteSubscription(id: Int) {
        viewModelScope.launch {
            repository.deleteSubscription(id)
        }
    }

    fun setCardAlias(card: String, alias: String) {
        viewModelScope.launch {
            repository.setCardAlias(card, alias)
        }
    }

    fun setAppLanguage(lang: String) {
        viewModelScope.launch {
            repository.setAppLanguage(lang)
            AppLocaleManager.setSystemAppLanguage(getApplication(), lang)
        }
    }

    fun setCardColor(card: String, hex: String) {
        viewModelScope.launch {
            repository.setCardColor(card, hex)
        }
    }

    fun importSubscriptions(currency: String?, newSubs: List<SubscriptionEntity>, replace: Boolean) {
        viewModelScope.launch {
            if (replace) {
                repository.deleteAllSubscriptions()
            }
            if (!currency.isNullOrBlank()) {
                repository.setPrimaryCurrency(currency)
            }
            newSubs.forEach { sub ->
                repository.insertSubscription(sub.copy(id = 0))
            }
        }
    }

    companion object {
        fun provideFactory(application: Application): ViewModelProvider.Factory {
            return object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    val db = SubscriptionDatabase.getDatabase(application)
                    val repo = SubscriptionRepository(db.subscriptionDao())
                    return MainViewModel(application, repo) as T
                }
            }
        }
    }
}
