package com.solitrix.postureminder.shared.util

import com.solitrix.postureminder.shared.domain.model.SLOT_COUNT
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalTime

fun slotIndexToLabel(index: Int): String {
    // Slot 0 = 8:00 AM, each slot is 30 minutes
    val totalMinutes = index * 30
    val hour24 = 8 + totalMinutes / 60
    val minute = totalMinutes % 60
    val hour12 = when {
        hour24 == 0 -> 12
        hour24 > 12 -> hour24 - 12
        else -> hour24
    }
    val amPm = if (hour24 < 12) "AM" else "PM"
    return "$hour12:${minute.toString().padStart(2, '0')} $amPm"
}

/**
 * Inverse of [slotIndexToLabel]: maps a wall-clock [time] to a slot index
 * (0 until [SLOT_COUNT]), or null if it falls outside the scheduled
 * 8:00 AM - 6:00 PM window.
 */
fun slotIndexAt(time: LocalTime): Int? {
    val minutesSinceStart = time.hour * 60 + time.minute - 8 * 60
    if (minutesSinceStart < 0) return null
    val index = minutesSinceStart / 30
    return if (index in 0 until SLOT_COUNT) index else null
}

/**
 * Like [slotIndexAt] but returns non-null only when [time] is the exact
 * start minute of a slot (i.e. the minute is a multiple of 30 relative to
 * 8:00 AM). Use this for minute-aligned schedulers so a notification fires
 * only at the first minute of a task, not for every minute within the slot.
 */
fun slotStartIndexAt(time: LocalTime): Int? {
    val minutesSinceStart = time.hour * 60 + time.minute - 8 * 60
    if (minutesSinceStart < 0 || minutesSinceStart % 30 != 0) return null
    val index = minutesSinceStart / 30
    return if (index in 0 until SLOT_COUNT) index else null
}

/**
 * Maps a [dayOfWeek] to the app's 0..4 (Mon..Fri) day index used by [Task.day],
 * or null for Saturday/Sunday.
 */
fun dayIndexAt(dayOfWeek: DayOfWeek): Int? = when (dayOfWeek) {
    DayOfWeek.MONDAY -> 0
    DayOfWeek.TUESDAY -> 1
    DayOfWeek.WEDNESDAY -> 2
    DayOfWeek.THURSDAY -> 3
    DayOfWeek.FRIDAY -> 4
    else -> null
}
