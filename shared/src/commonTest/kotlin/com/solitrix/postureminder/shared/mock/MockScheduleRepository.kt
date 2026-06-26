package com.solitrix.postureminder.shared.mock

import com.solitrix.postureminder.shared.domain.model.ScheduleModel
import com.solitrix.postureminder.shared.domain.repository.ScheduleRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class MockScheduleRepository : ScheduleRepository {

    private val _schedules = MutableStateFlow<List<ScheduleModel>>(emptyList())
    private var nextId = 1L

    val deletedIds = mutableListOf<Long>()

    fun setSchedules(vararg schedules: ScheduleModel) {
        _schedules.value = schedules.toList()
        nextId = (schedules.maxOfOrNull { it.id } ?: 0L) + 1L
    }

    fun setSchedules(schedules: List<ScheduleModel>) {
        _schedules.value = schedules
        nextId = (schedules.maxOfOrNull { it.id } ?: 0L) + 1L
    }

    override fun getSchedules(): Flow<List<ScheduleModel>> = _schedules.asStateFlow()

    override suspend fun addSchedule(name: String): Long {
        val id = nextId++
        _schedules.value = _schedules.value + ScheduleModel(id, name)
        return id
    }

    override suspend fun deleteSchedule(id: Long) {
        deletedIds.add(id)
        _schedules.value = _schedules.value.filter { it.id != id }
    }
}
