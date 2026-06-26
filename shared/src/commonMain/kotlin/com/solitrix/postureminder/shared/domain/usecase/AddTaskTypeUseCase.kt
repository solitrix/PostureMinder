package com.solitrix.postureminder.shared.domain.usecase

import com.solitrix.postureminder.shared.domain.repository.TaskTypeRepository

class AddTaskTypeUseCase(private val taskTypeRepository: TaskTypeRepository) {
    suspend operator fun invoke(name: String, colorRgb: Int) =
        taskTypeRepository.addTaskType(name.trim(), colorRgb)
}
