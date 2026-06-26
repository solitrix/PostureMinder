package com.solitrix.postureminder.shared.domain.usecase

import com.solitrix.postureminder.shared.domain.model.ScheduledTask
import com.solitrix.postureminder.shared.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow

class ObserveScheduledTasksUseCase(private val taskRepository: TaskRepository) {
    operator fun invoke(day: Int, scheduleId: Long): Flow<List<ScheduledTask>> =
        taskRepository.getTasksByDayAndSchedule(day, scheduleId)
}
