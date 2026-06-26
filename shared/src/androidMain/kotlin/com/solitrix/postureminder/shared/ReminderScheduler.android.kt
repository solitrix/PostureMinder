package com.solitrix.postureminder.shared

import androidx.work.WorkManager
import kotlinx.coroutines.CoroutineScope

actual fun buildReminderScheduler(
    scope: CoroutineScope,
    onTick: suspend () -> Unit,
): IReminderScheduler {
    return AndroidReminderScheduler()
}

class AndroidReminderScheduler() : IReminderScheduler {
    override fun startScheduler() {
        TODO("Not yet implemented")
    }

    override fun stopScheduler() {
        TODO("Not yet implemented")
    }
}
