package com.solitrix.postureminder.shared.data.repository

import com.solitrix.postureminder.shared.IReminderScheduler
import com.solitrix.postureminder.shared.domain.repository.ReminderRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ReminderRepositoryImpl(private val scheduler: IReminderScheduler) : ReminderRepository {

    private val _isRunning = MutableStateFlow(false)
    override val isRunning: StateFlow<Boolean> = _isRunning.asStateFlow()

    fun start() {
        scheduler.startScheduler()
        _isRunning.value = true
    }

    override fun toggle() {
        if (_isRunning.value) {
            scheduler.stopScheduler()
            _isRunning.value = false
        } else {
            scheduler.startScheduler()
            _isRunning.value = true
        }
    }
}
