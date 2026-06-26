package com.solitrix.postureminder.shared.util

import kotlinx.datetime.DayOfWeek

expect fun DayOfWeek.localizedName(short: Boolean = false): String
