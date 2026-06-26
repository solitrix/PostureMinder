package com.solitrix.postureminder.shared.domain.usecase

import com.solitrix.postureminder.shared.domain.model.TaskTypeModel
import com.solitrix.postureminder.shared.domain.repository.TaskTypeRepository
import kotlinx.coroutines.flow.Flow

class ObserveTaskTypesUseCase(private val taskTypeRepository: TaskTypeRepository) {
    operator fun invoke(): Flow<List<TaskTypeModel>> = taskTypeRepository.getTaskTypes()
}
