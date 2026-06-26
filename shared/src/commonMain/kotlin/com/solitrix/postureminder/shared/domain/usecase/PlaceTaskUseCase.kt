package com.solitrix.postureminder.shared.domain.usecase

import com.solitrix.postureminder.shared.domain.model.ScheduledTask
import com.solitrix.postureminder.shared.domain.repository.TaskRepository

class PlaceTaskUseCase(private val taskRepository: TaskRepository) {
    suspend operator fun invoke(
        taskTypeId: Long,
        slotIndex: Int,
        day: Int,
        scheduleId: Long,
        currentTasks: List<ScheduledTask>,
    ) {
        val occupied = currentTasks.flatMap { t -> (0 until t.durationSlots).map { t.slotIndex + it } }.toSet()
        if (slotIndex in occupied) return
        taskRepository.addTask(day = day, slotIndex = slotIndex, durationSlots = 1, taskTypeId = taskTypeId, scheduleId = scheduleId)
    }
}
