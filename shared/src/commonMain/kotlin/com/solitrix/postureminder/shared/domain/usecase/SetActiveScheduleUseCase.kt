package com.solitrix.postureminder.shared.domain.usecase

import com.solitrix.postureminder.shared.domain.repository.ActiveScheduleRepository

class SetActiveScheduleUseCase(private val activeScheduleRepository: ActiveScheduleRepository) {
    operator fun invoke(scheduleId: Long) = activeScheduleRepository.setActiveSchedule(scheduleId)
}
