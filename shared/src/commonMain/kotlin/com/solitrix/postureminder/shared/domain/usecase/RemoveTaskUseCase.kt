package com.solitrix.postureminder.shared.domain.usecase

import com.solitrix.postureminder.shared.domain.repository.TaskRepository

class RemoveTaskUseCase(private val taskRepository: TaskRepository) {
    suspend operator fun invoke(taskId: Long) = taskRepository.removeTask(taskId)
}
