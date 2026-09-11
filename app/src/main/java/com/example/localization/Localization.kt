package com.example.localization

import androidx.compose.runtime.compositionLocalOf
import java.time.LocalDate

enum class AppLanguage(val code: String, val label: String) {
    ENGLISH("en", "English"),
    RUSSIAN("ru", "Русский");

    companion object {
        fun fromCode(code: String?): AppLanguage = when (code?.lowercase()) {
            "ru" -> RUSSIAN
            else -> ENGLISH
        }
    }
}

interface AppStrings {
    val language: AppLanguage

    // Nav
    val navSubs: String
    val navOverview: String
    val navAnalytics: String
    val navSettings: String

    // Subscriptions Screen
    val subscriptionsTitle: String
    val thisMonth: String
    fun activeCount(count: Int): String
    fun activeSubscriptionsHeader(count: Int): String
    val totalScheduledSubtitle: String
    val emptySubsTitle: String
    val emptySubsSubtitle: String
    val importExport: String
    val addSubscription: String

    // Overview Screen
    val overviewTitle: String
    val viewMonthly: String
    val viewYearly: String
    val noneDueToday: String
    fun dueTodayCount(count: Int): String
    fun dueOnHeader(dateStr: String, count: Int): String
    fun allInMonthHeader(monthName: String, count: Int): String
    val noPaymentsDay: String
    fun noPaymentsMonth(monthName: String): String
    fun totalForMonth(monthName: String): String
    fun yearTotal(year: Int): String
    val twelveMonths: String
    val currentMonthBadge: String
    fun paymentsCount(count: Int): String
    val selectMonthTitle: String

    // Analytics Screen
    val analyticsTitle: String
    val byCard: String
    val byCategory: String
    val noCardsOrSubs: String
    fun cardStepper(current: Int, total: Int): String
    val unlinkedCard: String
    val noCardAssigned: String
    val totalAnnualSpend: String
    val annualSpend: String
    fun linkedSubsCount(count: Int): String
    val noSubsLinked: String
    val renameCard: String
    val cardColorTitle: String

    // Settings Screen
    val settingsTitle: String
    val languageSectionTitle: String
    val languageOptionTitle: String
    val languageOptionSubtitle: String
    val themeSectionTitle: String
    val themeModeTitle: String
    val themeModeSubtitle: String
    val darkTheme: String
    val lightTheme: String
    val systemTheme: String
    val materialYouTitle: String
    val materialYouSubtitle: String
    val currencySectionTitle: String
    val primaryCurrencyTitle: String
    val primaryCurrencySubtitle: String
    val selectCurrencyTitle: String
    val searchCurrencyPlaceholder: String
    val close: String
    val notificationsSectionTitle: String
    val bgServiceActive: String
    val bgServiceSubtitle: String
    val monthlyNotificationTitle: String
    val monthlyNotificationSubtitle: String
    val enablePermission: String
    val notificationSent: String
    val testNotificationBtn: String
    val testMonthlyNotificationBtn: String
    val monthlyDigestSent: String
    val dataSectionTitle: String
    val backupTitle: String
    fun trackedCountSubtitle(count: Int): String
    val importExportBtn: String

    // Add / Edit Dialog
    val newSubscriptionTitle: String
    val editSubscriptionTitle: String
    val deleteSubscriptionTitle: String
    fun deleteConfirmMessage(name: String): String
    val serviceNameLabel: String
    val priceLabel: String
    val secAmountLabel: String
    val secCurrencyLabel: String
    val frequencyLabel: String
    val repeatEveryDaysLabel: String
    val startDateLabel: String
    val dayLabel: String
    val monthLabel: String
    val yearLabel: String
    val cardLast4Label: String
    val categoryLabel: String
    val addToCalendarBtn: String
    val saveChangesBtn: String
    val addSubscriptionBtn: String
    val cancelAction: String
    val deleteAction: String
    val cardAliasLabel: String

    // Date & Helpers
    fun getMonthName(month: Int, short: Boolean = false): String
    fun formatDayOfWeek(date: LocalDate): String
    fun formatDateShort(date: LocalDate): String
    fun formatMonthYear(year: Int, month: Int): String
    fun getCategoryName(category: String): String
    fun getFrequencyName(freq: String): String
}

class EnglishStrings : AppStrings {
    override val language: AppLanguage = AppLanguage.ENGLISH

    override val navSubs = "Subs"
    override val navOverview = "Overview"
    override val navAnalytics = "Analytics"
    override val navSettings = "Settings"

    override val subscriptionsTitle = "Subscriptions"
    override val thisMonth = "This Month"
    override fun activeCount(count: Int) = "$count active"
    override fun activeSubscriptionsHeader(count: Int) = "Active Subscriptions ($count)"
    override val totalScheduledSubtitle = "Total scheduled payments for the current month"
    override val emptySubsTitle = "Nothing here yet"
    override val emptySubsSubtitle = "Tap the + button to track your first subscription."
    override val importExport = "Import & Export"
    override val addSubscription = "Add Subscription"

    override val overviewTitle = "Overview"
    override val viewMonthly = "Monthly"
    override val viewYearly = "Yearly"
    override val noneDueToday = "None due today"
    override fun dueTodayCount(count: Int) = "$count due today"
    override fun dueOnHeader(dateStr: String, count: Int) = "Due on $dateStr ($count)"
    override fun allInMonthHeader(monthName: String, count: Int) = "All in $monthName ($count)"
    override val noPaymentsDay = "No payments scheduled for this day"
    override fun noPaymentsMonth(monthName: String) = "No payments in $monthName"
    override fun totalForMonth(monthName: String) = "Total for $monthName"
    override fun yearTotal(year: Int) = "Year Total ($year)"
    override val twelveMonths = "12 months"
    override val currentMonthBadge = "Current"
    override fun paymentsCount(count: Int) = "$count ${if (count == 1) "payment" else "payments"}"
    override val selectMonthTitle = "Select Month"

    override val analyticsTitle = "Analytics"
    override val byCard = "By Card"
    override val byCategory = "By Category"
    override val noCardsOrSubs = "No cards or subscriptions added yet."
    override fun cardStepper(current: Int, total: Int) = "Card $current of $total"
    override val unlinkedCard = "Unlinked"
    override val noCardAssigned = "No card assigned"
    override val totalAnnualSpend = "Total Annual Spend"
    override val annualSpend = "Annual Spend"
    override fun linkedSubsCount(count: Int) = "Linked Subscriptions ($count)"
    override val noSubsLinked = "No subscriptions linked to this card."
    override val renameCard = "Rename Card"
    override val cardColorTitle = "Quick Card Color"

    override val settingsTitle = "Settings"
    override val languageSectionTitle = "Language & Region"
    override val languageOptionTitle = "App Language"
    override val languageOptionSubtitle = "Select your preferred application language"
    override val themeSectionTitle = "Theme & Appearance"
    override val themeModeTitle = "Theme Mode"
    override val themeModeSubtitle = "Choose your preferred interface theme"
    override val darkTheme = "Dark"
    override val lightTheme = "Light"
    override val systemTheme = "System"
    override val materialYouTitle = "Material 3 Dynamic Accent"
    override val materialYouSubtitle = "Adaptive Monet colors from wallpaper"
    override val currencySectionTitle = "Account & Currency"
    override val primaryCurrencyTitle = "Primary Currency"
    override val primaryCurrencySubtitle = "Account currency used for totals and conversions"
    override val selectCurrencyTitle = "Select Primary Currency"
    override val searchCurrencyPlaceholder = "Search currency (e.g. UAH, USD, €)"
    override val close = "Close"
    override val notificationsSectionTitle = "Notifications & Reminders"
    override val bgServiceActive = "Background Service Active"
    override val bgServiceSubtitle = "Daily background alerts notify you of upcoming subscription renewals."
    override val monthlyNotificationTitle = "Monthly Summary Notification"
    override val monthlyNotificationSubtitle = "Send a morning digest on the 1st of each month with all upcoming subscriptions and total amount."
    override val enablePermission = "Enable Permission"
    override val notificationSent = "Notification Sent!"
    override val testNotificationBtn = "Test Daily Alert"
    override val testMonthlyNotificationBtn = "Test 1st of Month Digest"
    override val monthlyDigestSent = "Monthly Digest Sent!"
    override val dataSectionTitle = "Data & Backup"
    override val backupTitle = "Backup & Restore"
    override fun trackedCountSubtitle(count: Int) = "$count subscriptions tracked locally"
    override val importExportBtn = "Import / Export"

    override val newSubscriptionTitle = "New Subscription"
    override val editSubscriptionTitle = "Edit Subscription"
    override val deleteSubscriptionTitle = "Delete Subscription"
    override fun deleteConfirmMessage(name: String) = "Are you sure you want to delete '$name'?"
    override val serviceNameLabel = "Service Name"
    override val priceLabel = "Price"
    override val secAmountLabel = "Sec. Amount"
    override val secCurrencyLabel = "Sec. Currency"
    override val frequencyLabel = "Frequency"
    override val repeatEveryDaysLabel = "Repeat every (days)"
    override val startDateLabel = "Start Date"
    override val dayLabel = "Day"
    override val monthLabel = "Month"
    override val yearLabel = "Year"
    override val cardLast4Label = "Card (last 4)"
    override val categoryLabel = "Category"
    override val addToCalendarBtn = "Add to Calendar (.ics)"
    override val saveChangesBtn = "Save Changes"
    override val addSubscriptionBtn = "Add Subscription"
    override val cancelAction = "Cancel"
    override val deleteAction = "Delete"
    override val cardAliasLabel = "Card Alias / Name"

    private val monthNames = listOf(
        "January", "February", "March", "April", "May", "June",
        "July", "August", "September", "October", "November", "December"
    )
    private val shortMonthNames = listOf(
        "Jan", "Feb", "Mar", "Apr", "May", "Jun",
        "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
    )
    private val dayOfWeekNames = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")

    override fun getMonthName(month: Int, short: Boolean): String {
        val index = (month - 1).coerceIn(0, 11)
        return if (short) shortMonthNames[index] else monthNames[index]
    }

    override fun formatDayOfWeek(date: LocalDate): String {
        val dayIndex = (date.dayOfWeek.value - 1).coerceIn(0, 6)
        return dayOfWeekNames[dayIndex]
    }

    override fun formatDateShort(date: LocalDate): String {
        val monthStr = getMonthName(date.monthValue, short = true)
        return "$monthStr %02d".format(date.dayOfMonth)
    }

    override fun formatMonthYear(year: Int, month: Int): String {
        return "${getMonthName(month, short = false)} $year"
    }

    override fun getCategoryName(category: String): String = category

    override fun getFrequencyName(freq: String): String = when (freq.lowercase()) {
        "monthly" -> "Monthly"
        "quarterly" -> "Quarterly"
        "yearly" -> "Yearly"
        "custom" -> "Custom"
        else -> freq.replaceFirstChar { it.uppercase() }
    }
}

class RussianStrings : AppStrings {
    override val language: AppLanguage = AppLanguage.RUSSIAN

    override val navSubs = "Подписки"
    override val navOverview = "Обзор"
    override val navAnalytics = "Аналитика"
    override val navSettings = "Настройки"

    override val subscriptionsTitle = "Подписки"
    override val thisMonth = "В этом месяце"
    override fun activeCount(count: Int): String {
        val mod10 = count % 10
        val mod100 = count % 100
        val word = when {
            mod100 in 11..19 -> "активных"
            mod10 == 1 -> "активная"
            mod10 in 2..4 -> "активные"
            else -> "активных"
        }
        return "$count $word"
    }
    override fun activeSubscriptionsHeader(count: Int) = "Активные подписки ($count)"
    override val totalScheduledSubtitle = "Запланированные списания за текущий месяц"
    override val emptySubsTitle = "Пока ничего нет"
    override val emptySubsSubtitle = "Нажмите кнопку +, чтобы добавить первую подписку."
    override val importExport = "Импорт и Экспорт"
    override val addSubscription = "Добавить подписку"

    override val overviewTitle = "Обзор"
    override val viewMonthly = "По месяцам"
    override val viewYearly = "По годам"
    override val noneDueToday = "Сегодня нет списаний"
    override fun dueTodayCount(count: Int): String {
        val mod10 = count % 10
        val mod100 = count % 100
        val word = when {
            mod100 in 11..19 -> "списаний"
            mod10 == 1 -> "списание"
            mod10 in 2..4 -> "списания"
            else -> "списаний"
        }
        return "$count $word сегодня"
    }
    override fun dueOnHeader(dateStr: String, count: Int) = "Списания $dateStr ($count)"
    override fun allInMonthHeader(monthName: String, count: Int) = "Все в $monthName ($count)"
    override val noPaymentsDay = "Нет запланированных списаний на этот день"
    override fun noPaymentsMonth(monthName: String) = "Нет списаний в этом месяце"
    override fun totalForMonth(monthName: String) = "Итого за месяц"
    override fun yearTotal(year: Int) = "Итого за $year год"
    override val twelveMonths = "12 месяцев"
    override val currentMonthBadge = "Текущий"
    override fun paymentsCount(count: Int): String {
        val mod10 = count % 10
        val mod100 = count % 100
        val word = when {
            mod100 in 11..19 -> "списаний"
            mod10 == 1 -> "списание"
            mod10 in 2..4 -> "списания"
            else -> "списаний"
        }
        return "$count $word"
    }
    override val selectMonthTitle = "Выберите месяц"

    override val analyticsTitle = "Аналитика"
    override val byCard = "По карте"
    override val byCategory = "По категориям"
    override val noCardsOrSubs = "Карты или подписки ещё не добавлены."
    override fun cardStepper(current: Int, total: Int) = "Карта $current из $total"
    override val unlinkedCard = "Без карты"
    override val noCardAssigned = "Карта не привязана"
    override val totalAnnualSpend = "Траты за год"
    override val annualSpend = "Годовые расходы"
    override fun linkedSubsCount(count: Int): String {
        val mod10 = count % 10
        val mod100 = count % 100
        val word = when {
            mod100 in 11..19 -> "подписок"
            mod10 == 1 -> "подписка"
            mod10 in 2..4 -> "подписки"
            else -> "подписок"
        }
        return "Привязано $count $word"
    }
    override val noSubsLinked = "Нет подписок, привязанных к этой карте."
    override val renameCard = "Переименовать"
    override val cardColorTitle = "Быстрый цвет карты"

    override val settingsTitle = "Настройки"
    override val languageSectionTitle = "Язык и регион"
    override val languageOptionTitle = "Язык приложения"
    override val languageOptionSubtitle = "Выберите предпочтительный язык интерфейса"
    override val themeSectionTitle = "Внешний вид и тема"
    override val themeModeTitle = "Тема оформления"
    override val themeModeSubtitle = "Выберите оформление интерфейса"
    override val darkTheme = "Тёмная"
    override val lightTheme = "Светлая"
    override val systemTheme = "Системная"
    override val materialYouTitle = "Динамический акцент Material 3"
    override val materialYouSubtitle = "Адаптивные цвета Monet из обоев"
    override val currencySectionTitle = "Аккаунт и валюта"
    override val primaryCurrencyTitle = "Основная валюта"
    override val primaryCurrencySubtitle = "Используется для расчёта итогов и графиков"
    override val selectCurrencyTitle = "Выберите основную валюту"
    override val searchCurrencyPlaceholder = "Поиск валюты (например: UAH, USD, RUB, EUR)"
    override val close = "Закрыть"
    override val notificationsSectionTitle = "Уведомления и напоминания"
    override val bgServiceActive = "Фоновая служба активна"
    override val bgServiceSubtitle = "Ежедневные фоновые оповещения о предстоящих списаниях."
    override val monthlyNotificationTitle = "Ежемесячный дайджест"
    override val monthlyNotificationSubtitle = "Утреннее уведомление 1-го числа каждого месяца со списком всех подписок и общей суммой."
    override val enablePermission = "Разрешить уведомления"
    override val notificationSent = "Уведомление отправлено!"
    override val testNotificationBtn = "Тест дневного напоминания"
    override val testMonthlyNotificationBtn = "Тест дайджеста на 1-е число"
    override val monthlyDigestSent = "Дайджест отправлен!"
    override val dataSectionTitle = "Данные и резервные копии"
    override val backupTitle = "Резервное копирование"
    override fun trackedCountSubtitle(count: Int): String {
        val mod10 = count % 10
        val mod100 = count % 100
        val word = when {
            mod100 in 11..19 -> "подписок"
            mod10 == 1 -> "подписка"
            mod10 in 2..4 -> "подписки"
            else -> "подписок"
        }
        return "$count $word сохранено локально"
    }
    override val importExportBtn = "Импорт / Экспорт"

    override val newSubscriptionTitle = "Новая подписка"
    override val editSubscriptionTitle = "Редактировать подписку"
    override val deleteSubscriptionTitle = "Удалить подписку"
    override fun deleteConfirmMessage(name: String) = "Вы уверены, что хотите удалить «$name»?"
    override val serviceNameLabel = "Название сервиса"
    override val priceLabel = "Стоимость"
    override val secAmountLabel = "Вторая сумма"
    override val secCurrencyLabel = "Вторая валюта"
    override val frequencyLabel = "Периодичность"
    override val repeatEveryDaysLabel = "Повторять каждые (дней)"
    override val startDateLabel = "Дата первого платежа"
    override val dayLabel = "День"
    override val monthLabel = "Месяц"
    override val yearLabel = "Год"
    override val cardLast4Label = "Карта (последние 4 цифры)"
    override val categoryLabel = "Категория"
    override val addToCalendarBtn = "Добавить в календарь (.ics)"
    override val saveChangesBtn = "Сохранить изменения"
    override val addSubscriptionBtn = "Добавить подписку"
    override val cancelAction = "Отмена"
    override val deleteAction = "Удалить"
    override val cardAliasLabel = "Название карты"

    private val monthNames = listOf(
        "Январь", "Февраль", "Март", "Апрель", "Май", "Июнь",
        "Июль", "Август", "Сентябрь", "Октябрь", "Ноябрь", "Декабрь"
    )
    private val shortMonthNames = listOf(
        "Янв", "Фев", "Мар", "Апр", "Май", "Июн",
        "Июл", "Авг", "Сен", "Окт", "Ноя", "Дек"
    )
    private val dayOfWeekNames = listOf("Пн", "Вт", "Ср", "Чт", "Пт", "Сб", "Вс")

    override fun getMonthName(month: Int, short: Boolean): String {
        val index = (month - 1).coerceIn(0, 11)
        return if (short) shortMonthNames[index] else monthNames[index]
    }

    override fun formatDayOfWeek(date: LocalDate): String {
        val dayIndex = (date.dayOfWeek.value - 1).coerceIn(0, 6)
        return dayOfWeekNames[dayIndex]
    }

    override fun formatDateShort(date: LocalDate): String {
        val monthStr = getMonthName(date.monthValue, short = true)
        return "%02d $monthStr".format(date.dayOfMonth)
    }

    override fun formatMonthYear(year: Int, month: Int): String {
        return "${getMonthName(month, short = false)} $year"
    }

    override fun getCategoryName(category: String): String = when (category) {
        "Entertainment" -> "Развлечения"
        "Essentials" -> "Базовые"
        "Productivity" -> "Продуктивность"
        "Quality of Life" -> "Качество жизни"
        "Streaming" -> "Стриминг"
        "Random" -> "Разное"
        else -> "Другое"
    }

    override fun getFrequencyName(freq: String): String = when (freq.lowercase()) {
        "monthly" -> "Ежемесячно"
        "quarterly" -> "Ежеквартально"
        "yearly" -> "Ежегодно"
        "custom" -> "Свой интервал"
        else -> freq
    }
}

fun getAppStrings(language: String?): AppStrings {
    return if (language?.lowercase() == "ru") RussianStrings() else EnglishStrings()
}

val LocalStrings = compositionLocalOf<AppStrings> { EnglishStrings() }
