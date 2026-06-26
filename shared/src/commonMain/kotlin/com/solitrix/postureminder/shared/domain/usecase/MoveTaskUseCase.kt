package com.solitrix.postureminder.shared.domain.usecase

import com.solitrix.postureminder.shared.domain.model.SLOT_COUNT
import com.solitrix.postureminder.shared.domain.model.ScheduledTask
import com.solitrix.postureminder.shared.domain.repository.TaskRepository

class MoveTaskUseCase(private val taskRepository: TaskRepository) {
    suspend operator fun invoke(taskId: Long, newSlotIndex: Int, currentTasks: List<ScheduledTask>) {
        val item = currentTasks.find { it.id == taskId } ?: return
        val occupiedByOthers = currentTasks
            .filter { it.id != taskId }
            .flatMap { t -> (0 until t.durationSlots).map { t.slotIndex + it } }
            .toSet()
        val needed = (0 until item.durationSlots).map { newSlotIndex + it }
        if (needed.any { it in occupiedByOthers || it >= SLOT_COUNT }) return
        taskRepository.updateTask(item.copy(slotIndex = newSlotIndex))
    }
}
