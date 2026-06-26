package com.solitrix.postureminder.shared.domain.repository

import com.solitrix.postureminder.shared.domain.model.TaskTypeModel
import kotlinx.coroutines.flow.Flow

interface TaskTypeRepository {
    fun getTaskTypes(): Flow<List<TaskTypeModel>>
    suspend fun addTaskType(name: String, colorRgb: Int)
    suspend fun updateTaskType(id: Long, name: String, colorRgb: Int)
}
