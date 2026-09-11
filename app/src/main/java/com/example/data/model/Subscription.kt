package com.example.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

val FREQUENCIES = listOf("monthly", "quarterly", "yearly", "custom")

val CATEGORIES = listOf(
    "Entertainment", "Essentials", "Productivity", "Quality of Life",
    "Streaming", "Random", "Other"
)

data class CurrencyInfo(
    val code: String,
    val name: String,
    val symbol: String
)

val ALL_CURRENCIES = listOf(
    CurrencyInfo("USD", "US Dollar", "$"),
    CurrencyInfo("EUR", "Euro", "€"),
    CurrencyInfo("UAH", "Ukrainian Hryvnia", "₴"),
    CurrencyInfo("GBP", "British Pound", "£"),
    CurrencyInfo("CHF", "Swiss Franc", "CHF"),
    CurrencyInfo("CAD", "Canadian Dollar", "C$"),
    CurrencyInfo("AUD", "Australian Dollar", "A$"),
    CurrencyInfo("PLN", "Polish Złoty", "zł"),
    CurrencyInfo("MDL", "Moldovan Leu", "L"),
    CurrencyInfo("RON", "Romanian Leu", "lei"),
    CurrencyInfo("CZK", "Czech Koruna", "Kč"),
    CurrencyInfo("HUF", "Hungarian Forint", "Ft"),
    CurrencyInfo("SEK", "Swedish Krona", "kr"),
    CurrencyInfo("NOK", "Norwegian Krone", "kr"),
    CurrencyInfo("DKK", "Danish Krone", "kr"),
    CurrencyInfo("JPY", "Japanese Yen", "¥"),
    CurrencyInfo("CNY", "Chinese Yuan", "¥"),
    CurrencyInfo("INR", "Indian Rupee", "₹"),
    CurrencyInfo("ILS", "Israeli Shekel", "₪"),
    CurrencyInfo("TRY", "Turkish Lira", "₺"),
    CurrencyInfo("BRL", "Brazilian Real", "R$"),
    CurrencyInfo("KRW", "South Korean Won", "₩"),
    CurrencyInfo("MXN", "Mexican Peso", "Mex$"),
    CurrencyInfo("NZD", "New Zealand Dollar", "NZ$"),
    CurrencyInfo("SGD", "Singapore Dollar", "S$"),
    CurrencyInfo("HKD", "Hong Kong Dollar", "HK$"),
    CurrencyInfo("ZAR", "South African Rand", "R"),
    CurrencyInfo("AED", "UAE Dirham", "د.إ"),
    CurrencyInfo("RUB", "Russian Ruble", "₽")
)

val PRIMARY_CURRENCIES = ALL_CURRENCIES.map { it.code }

val CURRENCIES = listOf("") + PRIMARY_CURRENCIES

@Entity(tableName = "subscriptions")
@Serializable
data class SubscriptionEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    @ColumnInfo(name = "price_usd") val priceUsd: Double,
    @ColumnInfo(name = "secondary_amount") val secondaryAmount: Double? = null,
    @ColumnInfo(name = "secondary_currency") val secondaryCurrency: String? = null,
    val frequency: String,
    @ColumnInfo(name = "start_date") val startDate: String, // ISO YYYY-MM-DD
    @ColumnInfo(name = "interval_days") val intervalDays: Int? = null,
    val card: String? = null,
    val category: String? = "Other"
)

@Entity(tableName = "settings")
data class SettingEntity(
    @PrimaryKey val key: String,
    val value: String
)

@Entity(tableName = "card_aliases")
data class CardAliasEntity(
    @PrimaryKey val card: String,
    val alias: String
)

@Serializable
data class SubscriptionExport(
    val name: String,
    val price_usd: Double,
    val secondary_amount: Double? = null,
    val secondary_currency: String? = null,
    val frequency: String,
    val start_date: String,
    val interval_days: Int? = null,
    val card: String? = null,
    val category: String? = null
)

@Serializable
data class ExportData(
    val version: Int = 1,
    val primary_currency: String = "USD",
    val subscriptions: List<SubscriptionExport> = emptyList()
)
