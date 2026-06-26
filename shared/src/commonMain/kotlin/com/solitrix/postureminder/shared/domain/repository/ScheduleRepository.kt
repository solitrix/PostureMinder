package com.solitrix.postureminder.shared.domain.repository

import com.solitrix.postureminder.shared.domain.model.ScheduleModel
import kotlinx.coroutines.flow.Flow

interface ScheduleRepository {
    fun getSchedules(): Flow<List<ScheduleModel>>
    suspend fun addSchedule(name: String): Long
    suspend fun deleteSchedule(id: Long)
}
