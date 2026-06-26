package com.solitrix.postureminder.shared.util

import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.isoDayNumber
import java.time.DayOfWeek as JavaDayOfWeek
import java.time.format.TextStyle
import java.util.Locale

actual fun DayOfWeek.localizedName(short: Boolean): String {
    val style = if (short) TextStyle.SHORT else TextStyle.FULL
    return JavaDayOfWeek.of(isoDayNumber).getDisplayName(style, Locale.getDefault())
}
