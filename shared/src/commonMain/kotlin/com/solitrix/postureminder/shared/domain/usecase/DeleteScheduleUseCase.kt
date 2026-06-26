package com.solitrix.postureminder.shared.domain.usecase

import com.solitrix.postureminder.shared.domain.repository.ActiveScheduleRepository
import com.solitrix.postureminder.shared.domain.repository.ScheduleRepository
import kotlinx.coroutines.flow.first

class DeleteScheduleUseCase(
    private val scheduleRepository: ScheduleRepository,
    private val activeScheduleRepository: ActiveScheduleRepository,
) {
    suspend operator fun invoke(scheduleId: Long) {
        scheduleRepository.deleteSchedule(scheduleId)
        if (activeScheduleRepository.activeScheduleId.value == scheduleId) {
            val remaining = scheduleRepository.getSchedules().first()
            remaining.firstOrNull()?.id?.let { activeScheduleRepository.setActiveSchedule(it) }
        }
    }
}
