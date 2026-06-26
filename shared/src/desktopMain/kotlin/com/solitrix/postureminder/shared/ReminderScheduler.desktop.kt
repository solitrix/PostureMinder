package com.solitrix.postureminder.shared

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Clock

actual fun buildReminderScheduler(
    scope: CoroutineScope,
    onTick: suspend () -> Unit,
): IReminderScheduler = DesktopReminderScheduler(scope, onTick)

class DesktopReminderScheduler(
    private val scope: CoroutineScope,
    private val onTick: suspend () -> Unit,
) : IReminderScheduler {
    private var job: Job? = null

    override fun startScheduler() {
        if (job?.isActive == true) return
        job = scope.launch {
            // Align to the next wall-clock minute boundary before starting the loop.
            val secondsIntoMinute = Clock.System.now().epochSeconds % 60
            delay((60 - secondsIntoMinute) * 1_000L)
            while (true) {
                onTick()
                delay(60_000L)
            }
        }
    }

    override fun stopScheduler() {
        job?.cancel()
        job = null
    }
}
