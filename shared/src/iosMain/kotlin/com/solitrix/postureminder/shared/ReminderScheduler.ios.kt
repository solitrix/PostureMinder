package com.solitrix.postureminder.shared

import kotlinx.coroutines.CoroutineScope

actual fun buildReminderScheduler(
    scope: CoroutineScope,
    onTick: suspend () -> Unit,
): IReminderScheduler = IosReminderScheduler()

/*
 * iOS scheduling strategy (not yet implemented):
 * Never count down in the background; always calculate time remaining from a
 * saved start date, and let notifications + Live Activities provide the
 * user-facing experience. Pre-schedule a rolling window of local notifications
 * on startScheduler(), cancel them on stopScheduler().
 * Live Activities (iOS 16.1+) are ideal for timer-style apps.
 */
class IosReminderScheduler : IReminderScheduler {
    override fun startScheduler() {}
    override fun stopScheduler() {}
}