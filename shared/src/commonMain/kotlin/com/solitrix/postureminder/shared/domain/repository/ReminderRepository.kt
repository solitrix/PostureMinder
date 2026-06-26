package com.solitrix.postureminder.shared.domain.repository

import kotlinx.coroutines.flow.StateFlow

interface ReminderRepository {
    val isRunning: StateFlow<Boolean>
    fun toggle()
}
