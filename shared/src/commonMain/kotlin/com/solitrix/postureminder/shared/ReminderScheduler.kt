package com.solitrix.postureminder.shared

import kotlinx.coroutines.CoroutineScope

expect fun buildReminderScheduler(
    scope: CoroutineScope,
    onTick: suspend () -> Unit,
): IReminderScheduler

interface IReminderScheduler {
    fun startScheduler()
    fun stopScheduler()
}