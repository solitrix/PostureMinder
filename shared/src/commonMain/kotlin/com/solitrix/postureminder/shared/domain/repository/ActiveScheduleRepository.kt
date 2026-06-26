package com.solitrix.postureminder.shared.domain.repository

import kotlinx.coroutines.flow.StateFlow

interface ActiveScheduleRepository {
    val activeScheduleId: StateFlow<Long>
    fun setActiveSchedule(id: Long)
}
