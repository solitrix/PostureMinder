package com.solitrix.postureminder.shared.data.repository

import com.solitrix.postureminder.shared.data.dao.SetDao
import com.solitrix.postureminder.shared.data.entity.Set as SetEntity
import com.solitrix.postureminder.shared.data.mapper.toDomain
import com.solitrix.postureminder.shared.domain.model.ScheduleModel
import com.solitrix.postureminder.shared.domain.repository.ScheduleRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class ScheduleRepositoryImpl(private val dao: SetDao) : ScheduleRepository {

    override fun getSchedules(): Flow<List<ScheduleModel>> =
        dao.getAllAsFlow().map { list -> list.map { it.toDomain() } }

    override suspend fun addSchedule(name: String): Long = dao.insert(SetEntity(name = name))

    override suspend fun deleteSchedule(id: Long) {
        val entity = dao.getAllAsFlow().first().find { it.id == id } ?: return
        dao.delete(entity)
    }
}
