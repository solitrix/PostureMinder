package com.solitrix.postureminder.shared.domain.usecase

import com.solitrix.postureminder.shared.domain.model.SLOT_COUNT
import com.solitrix.postureminder.shared.domain.model.ScheduledTask
import com.solitrix.postureminder.shared.domain.repository.TaskRepository

class ResizeTaskUseCase(private val taskRepository: TaskRepository) {
    suspend operator fun invoke(taskId: Long, newDurationSlots: Int, currentTasks: List<ScheduledTask>) {
        val item = currentTasks.find { it.id == taskId } ?: return
        val newDuration = newDurationSlots.coerceAtLeast(1)
        val delta = newDuration - item.durationSlots

        if (delta <= 0) {
            taskRepository.updateTask(item.copy(durationSlots = newDuration))
            return
        }

        val currentEnd = item.slotIndex + item.durationSlots
        currentTasks.forEach { t ->
            when {
                t.id == taskId -> taskRepository.updateTask(t.copy(durationSlots = newDuration))
                t.slotIndex >= currentEnd -> {
                    val pushed = t.slotIndex + delta
                    if (pushed < SLOT_COUNT) taskRepository.updateTask(t.copy(slotIndex = pushed))
                    else taskRepository.removeTask(t.id)
                }
            }
        }
    }
}
