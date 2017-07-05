package io.stepuplabs.settleup.util.extensions

import android.text.format.DateFormat
import cz.pepa.runapp.R
import java.text.SimpleDateFormat
import java.util.*

/**
 * Extensions for working with dates.
 *
 * @author David Vávra (david@stepuplabs.io)
 */

val SUPPORTED_LANGUAGES = listOf("en", "cs") // Don't forget to also update resConfigs in Gradle when you are editing this.

fun Long.formatDateTime(): String {
    return SimpleDateFormat.getDateTimeInstance(SimpleDateFormat.MEDIUM /* Feb 21, 2017 */, SimpleDateFormat.SHORT /* 5:35 PM */, supportedLocale()).format(Date(this)) // -> Feb 21, 2017 5:35 PM
}

fun Long.formatDate(): String {
    return SimpleDateFormat.getDateInstance(SimpleDateFormat.MEDIUM /* Feb 21, 2017 */, supportedLocale()).format(Date(this)) // -> Feb 21, 2017
}

fun Date.toMonthAndYear(): String {
    // See formatting documentation https://docs.oracle.com/javase/8/docs/api/java/text/SimpleDateFormat.html
    return SimpleDateFormat("LLLL yyyy", supportedLocale()).format(this).toString().capitalize() // LLLL = standalone name (leden); MMMM = formatted name (ledna)
}

fun Long.toYearMonthDay(): String {
    return DateFormat.format("yyyyMMdd", this) as String
}

fun Date.firstDayOfMonth(): Date { // 3.3.2017 4:32 -> 1.3.2017 00:00:00
    val calendar = Calendar.getInstance()
    calendar.time = this
    val clearedCalendar = Calendar.getInstance()
    clearedCalendar.clear()
    clearedCalendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR))
    clearedCalendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH))
    return clearedCalendar.time
}

fun Date.lastDayOfMonth(): Date { // 3.3.2017 4:32 -> 28.3.2017 23:59:59
    val calendar = Calendar.getInstance()
    calendar.time = this
    val clearedCalendar = Calendar.getInstance()
    clearedCalendar.clear()
    clearedCalendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.getActualMaximum(Calendar.DAY_OF_MONTH), 23, 59, 59)
    return clearedCalendar.time
}

fun Date.previousMonth(): Date { // 3.1.2017 13:45:12 -> 1.12.2016 00:00:00
    val calendar = Calendar.getInstance()
    calendar.clear()
    calendar.time = this
    val lastMonthCalendar = Calendar.getInstance()
    lastMonthCalendar.clear()
    lastMonthCalendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR))
    lastMonthCalendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH))
    lastMonthCalendar.add(Calendar.MONTH, -1) // Set date to previous month (based on Calendar rules) - no need to check for January -> December
    return lastMonthCalendar.time
}

fun Long.toCalendar(): Calendar {
    val calendar = Calendar.getInstance()
    calendar.timeInMillis = this
    return calendar
}

fun Long.changeDate(year: Int, month: Int, day: Int): Long {
    val calendar = this.toCalendar()
    calendar.set(year, month, day)
    return calendar.timeInMillis
}

fun Long.changeTime(hourOfDay: Int, minute: Int, second: Int): Long {
    val calendar = this.toCalendar()
    calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
    calendar.set(Calendar.MINUTE, minute)
    calendar.set(Calendar.SECOND, second)
    return calendar.timeInMillis
}

fun Long.isToday(): Boolean {
    val cal1 = Calendar.getInstance()
    cal1.timeInMillis = this
    val cal2 = Calendar.getInstance()
    return (cal1.get(Calendar.ERA) == cal2.get(Calendar.ERA) &&
            cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
            cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR))
}

fun Date.toMillis(): Long {
    val calendar = Calendar.getInstance()
    calendar.time = this
    return calendar.timeInMillis
}

fun Long.toDate(): Date {
    val calendar = Calendar.getInstance()
    calendar.timeInMillis = this
    return calendar.time
}

fun currentTime(): Long {
    return Calendar.getInstance().timeInMillis
}

/**
 * Returns current locale or UK English if we don't support this locale.
 */
fun supportedLocale(): Locale {
    val default = Locale.getDefault()
    if (SUPPORTED_LANGUAGES.contains(default.language)) {
        return default
    } else {
        return Locale.UK // UK has nicer formatting for most of the world
    }
}

/**
 * 1 = 1 day
 * 30 = 30 days
 * 31 = 1 month
 * 60 = 2 months
 */
fun Int.formatDuration(): String {
    val months = this / 30
    if (months == 0) {
        return R.plurals.day_count.toPlural(this)
    } else {
        return R.plurals.month_count.toPlural(months)
    }
}
