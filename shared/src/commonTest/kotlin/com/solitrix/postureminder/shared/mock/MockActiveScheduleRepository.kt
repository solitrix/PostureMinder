package com.solitrix.postureminder.shared.mock

import com.solitrix.postureminder.shared.domain.repository.ActiveScheduleRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class MockActiveScheduleRepository(initialId: Long = 1L) : ActiveScheduleRepository {

    private val _activeScheduleId = MutableStateFlow(initialId)
    override val activeScheduleId: StateFlow<Long> = _activeScheduleId.asStateFlow()

    val setCalls = mutableListOf<Long>()

    override fun setActiveSchedule(id: Long) {
        setCalls.add(id)
        _activeScheduleId.value = id
    }
}
