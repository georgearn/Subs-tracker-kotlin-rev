package com.example.widget

import android.content.Context
import com.example.data.database.SubscriptionDatabase
import com.example.data.model.ALL_CURRENCIES
import com.example.data.model.SubscriptionEntity
import com.example.logic.SubscriptionCalculations
import java.time.LocalDate

/**
 * Direct-to-Room data access for the home screen widgets. Widgets run outside the
 * Compose/ViewModel tree, so they read the same repository the app uses but without
 * going through MainViewModel's Flow/StateFlow plumbing.
 */
object WidgetDataSource {

    data class WidgetSnapshot(
        val subscriptions: List<SubscriptionEntity>,
        val currencyCode: String,
        val currencySymbol: String
    )

    data class DuePayment(
        val date: LocalDate,
        val subs: List<SubscriptionEntity>
    ) {
        val total: Double get() = subs.sumOf { it.priceUsd }
    }

    data class CategorySlice(
        val category: String,
        val amount: Double,
        val percentage: Float
    )

    suspend fun loadSnapshot(context: Context): WidgetSnapshot {
        val dao = SubscriptionDatabase.getDatabase(context).subscriptionDao()
        val subs = dao.getAllSubscriptionsSync()
        val currencyCode = dao.getSettingSync("primary_currency") ?: "USD"
        val symbol = ALL_CURRENCIES.find { it.code == currencyCode }?.symbol ?: currencyCode
        return WidgetSnapshot(subs, currencyCode, symbol)
    }

    fun monthTotal(subscriptions: List<SubscriptionEntity>, date: LocalDate = LocalDate.now()): Double {
        return subscriptions.sumOf { sub ->
            val count = SubscriptionCalculations.occurrencesInMonth(sub, date.year, date.monthValue).size
            sub.priceUsd * count
        }
    }

    /** Earliest date on/after [from] any active subscription is due, and everything due that day. */
    fun nextDuePayment(subscriptions: List<SubscriptionEntity>, from: LocalDate = LocalDate.now()): DuePayment? {
        val active = subscriptions.filter { it.endDate == null }
        if (active.isEmpty()) return null
        val nextDate = active
            .map { SubscriptionCalculations.nextOccurrenceOnOrAfter(it, from) }
            .minOrNull() ?: return null
        val due = active.filter { SubscriptionCalculations.nextOccurrenceOnOrAfter(it, from) == nextDate }
        return if (due.isEmpty()) null else DuePayment(nextDate, due)
    }

    /** Active subscriptions due on each of [days], keyed by date. */
    fun occurrencesByDay(
        subscriptions: List<SubscriptionEntity>,
        days: List<LocalDate>
    ): Map<LocalDate, List<SubscriptionEntity>> {
        val active = subscriptions.filter { it.endDate == null }
        return days.associateWith { day ->
            active.filter { sub ->
                SubscriptionCalculations.occurrencesInMonth(sub, day.year, day.monthValue).contains(day)
            }
        }
    }

    /** Same category-vs-annual-spend split the Analytics screen's gauge uses. */
    fun categoryBreakdown(subscriptions: List<SubscriptionEntity>, year: Int = LocalDate.now().year): List<CategorySlice> {
        val totalAnnual = subscriptions.sumOf { SubscriptionCalculations.currentYearCost(it, year) }
        if (totalAnnual <= 0) return emptyList()
        val amounts = mutableMapOf<String, Double>()
        subscriptions.forEach { sub ->
            val cat = sub.category ?: "Other"
            amounts[cat] = (amounts[cat] ?: 0.0) + SubscriptionCalculations.currentYearCost(sub, year)
        }
        return amounts.map { (cat, amount) ->
            CategorySlice(cat, amount, (amount / totalAnnual).toFloat())
        }.filter { it.amount > 0 }.sortedByDescending { it.amount }
    }

    fun totalAnnualSpend(subscriptions: List<SubscriptionEntity>, year: Int = LocalDate.now().year): Double {
        return subscriptions.sumOf { SubscriptionCalculations.currentYearCost(it, year) }
    }
}
