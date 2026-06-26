package com.solitrix.postureminder.shared.domain.usecase

import com.solitrix.postureminder.shared.domain.model.ScheduleModel
import com.solitrix.postureminder.shared.domain.repository.ScheduleRepository
import kotlinx.coroutines.flow.Flow

class ObserveSchedulesUseCase(private val scheduleRepository: ScheduleRepository) {
    operator fun invoke(): Flow<List<ScheduleModel>> = scheduleRepository.getSchedules()
}
