package com.solitrix.postureminder.shared.util

import kotlinx.datetime.DayOfWeek
import platform.Foundation.NSCalendar
import platform.Foundation.NSCalendarIdentifierGregorian

actual fun DayOfWeek.localizedName(short: Boolean): String {
    val cal = NSCalendar(NSCalendarIdentifierGregorian)
    // NSCalendar symbols[0]=Sunday, [1]=Monday, ..., [6]=Saturday
    // isoDayNumber: Mon=1..Sun=7, so isoDayNumber % 7 maps Mon→1..Sat→6..Sun→0
    @Suppress("UNCHECKED_CAST")
    val symbols = (if (short) cal.shortWeekdaySymbols else cal.weekdaySymbols) as List<String>
    return symbols[isoDayNumber % 7]
}
