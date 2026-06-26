package com.solitrix.postureminder.shared.mock

import com.solitrix.postureminder.shared.domain.repository.ReminderRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class MockReminderRepository(initiallyRunning: Boolean = false) : ReminderRepository {

    private val _isRunning = MutableStateFlow(initiallyRunning)
    override val isRunning: StateFlow<Boolean> = _isRunning.asStateFlow()

    var toggleCount = 0

    override fun toggle() {
        toggleCount++
        _isRunning.value = !_isRunning.value
    }
}
