package com.solitrix.postureminder.shared.data.repository

import com.solitrix.postureminder.shared.domain.repository.ActiveScheduleRepository
import com.solitrix.postureminder.shared.domain.repository.ScheduleRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class ActiveScheduleRepositoryImpl(
    scheduleRepository: ScheduleRepository,
    appScope: CoroutineScope,
) : ActiveScheduleRepository {

    private val _activeScheduleId = MutableStateFlow(1L)
    override val activeScheduleId: StateFlow<Long> = _activeScheduleId.asStateFlow()

    init {
        // Snap to the real first DB id on startup (seed id may differ from 1).
        appScope.launch {
            scheduleRepository.getSchedules().first().firstOrNull()?.id
                ?.let { _activeScheduleId.value = it }
        }
    }

    override fun setActiveSchedule(id: Long) {
        _activeScheduleId.value = id
    }
}
