package com.solitrix.postureminder.shared.domain.usecase

import com.solitrix.postureminder.shared.domain.repository.ScheduleRepository

class AddScheduleUseCase(private val scheduleRepository: ScheduleRepository) {
    suspend operator fun invoke(name: String): Long = scheduleRepository.addSchedule(name.trim())
}
