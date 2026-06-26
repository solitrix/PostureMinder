package com.solitrix.postureminder.shared.domain.usecase

import com.solitrix.postureminder.shared.domain.model.ScheduledTask
import com.solitrix.postureminder.shared.domain.repository.TaskRepository

class ResizeTaskTopUseCase(private val taskRepository: TaskRepository) {
    suspend operator fun invoke(taskId: Long, newSlotIndex: Int, currentTasks: List<ScheduledTask>) {
        val item = currentTasks.find { it.id == taskId } ?: return
        val bottomSlot = item.slotIndex + item.durationSlots
        val clampedSlot = newSlotIndex.coerceIn(0, bottomSlot - 1)
        val newDuration = bottomSlot - clampedSlot
        val occupiedByOthers = currentTasks
            .filter { it.id != taskId }
            .flatMap { t -> (0 until t.durationSlots).map { t.slotIndex + it } }
            .toSet()
        if ((clampedSlot until item.slotIndex).any { it in occupiedByOthers }) return
        taskRepository.updateTask(item.copy(slotIndex = clampedSlot, durationSlots = newDuration))
    }
}
