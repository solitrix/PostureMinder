package com.solitrix.postureminder.shared.domain.usecase

import com.solitrix.postureminder.shared.domain.repository.TaskTypeRepository

class UpdateTaskTypeUseCase(private val taskTypeRepository: TaskTypeRepository) {
    suspend operator fun invoke(id: Long, name: String, colorRgb: Int) =
        taskTypeRepository.updateTaskType(id, name.trim(), colorRgb)
}
