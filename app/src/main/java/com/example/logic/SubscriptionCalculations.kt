package com.example.logic

import com.example.data.model.ExportData
import com.example.data.model.SubscriptionEntity
import com.example.data.model.SubscriptionExport
import kotlinx.serialization.json.Json
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.UUID

object SubscriptionCalculations {

    private val jsonConfig = Json {
        ignoreUnknownKeys = true
        prettyPrint = true
        isLenient = true
    }

    fun clampedDate(year: Int, month: Int, day: Int): LocalDate {
        val ym = YearMonth.of(year, month)
        val lastDay = ym.lengthOfMonth()
        return LocalDate.of(year, month, day.coerceAtMost(lastDay))
    }

    fun parseStart(sub: SubscriptionEntity): LocalDate {
        return try {
            LocalDate.parse(sub.startDate)
        } catch (e: Exception) {
            LocalDate.now()
        }
    }

    fun occurrencesInMonth(sub: SubscriptionEntity, year: Int, month: Int): List<LocalDate> {
        val start = parseStart(sub)
        val freq = sub.frequency.lowercase()
        val occurrences = mutableListOf<LocalDate>()

        when (freq) {
            "monthly" -> {
                val subMonths = start.year * 12 + start.monthValue
                val curMonths = year * 12 + month
                if (curMonths >= subMonths) {
                    occurrences.add(clampedDate(year, month, start.dayOfMonth))
                }
            }
            "quarterly" -> {
                val diff = (year * 12 + month) - (start.year * 12 + start.monthValue)
                if (diff >= 0 && diff % 3 == 0) {
                    occurrences.add(clampedDate(year, month, start.dayOfMonth))
                }
            }
            "yearly" -> {
                if (month == start.monthValue && year >= start.year) {
                    occurrences.add(clampedDate(year, month, start.dayOfMonth))
                }
            }
            "custom" -> {
                val step = sub.intervalDays ?: 0
                if (step > 0) {
                    val monthStart = LocalDate.of(year, month, 1)
                    val ym = YearMonth.of(year, month)
                    val monthEnd = LocalDate.of(year, month, ym.lengthOfMonth())

                    var d = start
                    if (d.isBefore(monthStart)) {
                        val daysBetween = ChronoUnit.DAYS.between(d, monthStart)
                        val jumps = daysBetween / step
                        d = d.plusDays(jumps * step)
                    }
                    while (!d.isAfter(monthEnd)) {
                        if (!d.isBefore(start) && d.year == year && d.monthValue == month) {
                            occurrences.add(d)
                        }
                        d = d.plusDays(step.toLong())
                    }
                }
            }
        }
        return occurrences.sorted()
    }

    fun paymentsPerYear(sub: SubscriptionEntity): Double {
        return when (sub.frequency.lowercase()) {
            "monthly" -> 12.0
            "quarterly" -> 4.0
            "yearly" -> 1.0
            "custom" -> {
                val interval = sub.intervalDays ?: 1
                if (interval > 0) 365.25 / interval else 0.0
            }
            else -> 0.0
        }
    }

    fun annualCost(sub: SubscriptionEntity): Double {
        return sub.priceUsd * paymentsPerYear(sub)
    }

    fun exportToJson(subscriptions: List<SubscriptionEntity>, primaryCurrency: String): String {
        val exportList = subscriptions.map { sub ->
            SubscriptionExport(
                name = sub.name,
                price_usd = sub.priceUsd,
                secondary_amount = sub.secondaryAmount,
                secondary_currency = sub.secondaryCurrency,
                frequency = sub.frequency,
                start_date = sub.startDate,
                interval_days = sub.intervalDays,
                card = sub.card,
                category = sub.category
            )
        }
        val exportData = ExportData(
            version = 1,
            primary_currency = primaryCurrency,
            subscriptions = exportList
        )
        return jsonConfig.encodeToString(ExportData.serializer(), exportData)
    }

    fun parseImportJson(jsonText: String): Pair<String?, List<SubscriptionEntity>> {
        val exportData = jsonConfig.decodeFromString(ExportData.serializer(), jsonText)
        val entities = exportData.subscriptions.map { exp ->
            SubscriptionEntity(
                name = exp.name,
                priceUsd = exp.price_usd,
                secondaryAmount = exp.secondary_amount,
                secondaryCurrency = exp.secondary_currency,
                frequency = exp.frequency,
                startDate = exp.start_date,
                intervalDays = exp.interval_days,
                card = exp.card,
                category = exp.category ?: "Other"
            )
        }
        return Pair(exportData.primary_currency, entities)
    }

    fun createIcsContent(
        name: String,
        priceUsd: Double,
        frequency: String,
        intervalDays: Int?,
        startDate: LocalDate
    ): String {
        val dtstart = startDate.format(DateTimeFormatter.BASIC_ISO_DATE)
        val dtend = startDate.plusDays(1).format(DateTimeFormatter.BASIC_ISO_DATE)
        val uid = "${UUID.randomUUID()}@subscriptiontracker"
        val stamp = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss'Z'")
            .format(java.time.Instant.now().atZone(java.time.ZoneOffset.UTC))

        val rrule = when (frequency.lowercase()) {
            "quarterly" -> "FREQ=MONTHLY;INTERVAL=3"
            "yearly" -> "FREQ=YEARLY;INTERVAL=1"
            "custom" -> "FREQ=DAILY;INTERVAL=${intervalDays ?: 1}"
            else -> "FREQ=MONTHLY;INTERVAL=1"
        }

        val summary = name.replace("\\", "\\\\").replace(",", "\\,").replace(";", "\\;")
        val description = "Subscription renewal: $name (Price: ${"%.2f".format(priceUsd)})"

        return buildString {
            append("BEGIN:VCALENDAR\r\n")
            append("VERSION:2.0\r\n")
            append("PRODID:-//Subscription Tracker//EN\r\n")
            append("CALSCALE:GREGORIAN\r\n")
            append("BEGIN:VEVENT\r\n")
            append("UID:$uid\r\n")
            append("DTSTAMP:$stamp\r\n")
            append("DTSTART;VALUE=DATE:$dtstart\r\n")
            append("DTEND;VALUE=DATE:$dtend\r\n")
            append("RRULE:$rrule\r\n")
            append("SUMMARY:$summary\r\n")
            append("DESCRIPTION:$description\r\n")
            append("END:VEVENT\r\n")
            append("END:VCALENDAR\r\n")
        }
    }
}
