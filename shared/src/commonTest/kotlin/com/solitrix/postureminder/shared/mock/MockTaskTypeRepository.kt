package com.solitrix.postureminder.shared.mock

import com.solitrix.postureminder.shared.domain.model.TaskTypeModel
import com.solitrix.postureminder.shared.domain.repository.TaskTypeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class MockTaskTypeRepository : TaskTypeRepository {

    private val _types = MutableStateFlow<List<TaskTypeModel>>(emptyList())

    val addCalls = mutableListOf<Pair<String, Int>>()
    val updateCalls = mutableListOf<Triple<Long, String, Int>>()

    fun setTypes(vararg types: TaskTypeModel) {
        _types.value = types.toList()
    }

    fun setTypes(types: List<TaskTypeModel>) {
        _types.value = types
    }

    override fun getTaskTypes(): Flow<List<TaskTypeModel>> = _types.asStateFlow()

    override suspend fun addTaskType(name: String, colorRgb: Int) {
        addCalls.add(name to colorRgb)
        val id = (_types.value.maxOfOrNull { it.id } ?: 0L) + 1L
        _types.value = _types.value + TaskTypeModel(id, name, colorRgb)
    }

    override suspend fun updateTaskType(id: Long, name: String, colorRgb: Int) {
        updateCalls.add(Triple(id, name, colorRgb))
        _types.value = _types.value.map { if (it.id == id) it.copy(name = name, colorRgb = colorRgb) else it }
    }
}
