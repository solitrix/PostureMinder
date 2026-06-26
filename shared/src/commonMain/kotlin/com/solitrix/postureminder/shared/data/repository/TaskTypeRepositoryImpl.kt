package com.solitrix.postureminder.shared.data.repository

import com.solitrix.postureminder.shared.data.dao.TaskTypeDao
import com.solitrix.postureminder.shared.data.entity.TaskType
import com.solitrix.postureminder.shared.data.mapper.toDomain
import com.solitrix.postureminder.shared.domain.model.TaskTypeModel
import com.solitrix.postureminder.shared.domain.repository.TaskTypeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TaskTypeRepositoryImpl(private val dao: TaskTypeDao) : TaskTypeRepository {

    override fun getTaskTypes(): Flow<List<TaskTypeModel>> =
        dao.getAllAsFlow().map { list -> list.map { it.toDomain() } }

    override suspend fun addTaskType(name: String, colorRgb: Int) =
        dao.insert(TaskType(name = name, colorRgb = colorRgb))

    override suspend fun updateTaskType(id: Long, name: String, colorRgb: Int) =
        dao.update(id, name, colorRgb)
}
