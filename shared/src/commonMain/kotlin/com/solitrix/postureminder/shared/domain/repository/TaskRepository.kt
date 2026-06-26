package com.solitrix.postureminder.shared.domain.repository

import com.solitrix.postureminder.shared.domain.model.ScheduledTask
import kotlinx.coroutines.flow.Flow

interface TaskRepository {
    fun getTasksByDayAndSchedule(day: Int, scheduleId: Long): Flow<List<ScheduledTask>>
    suspend fun addTask(day: Int, slotIndex: Int, durationSlots: Int, taskTypeId: Long, scheduleId: Long)
    suspend fun updateTask(task: ScheduledTask)
    suspend fun removeTask(taskId: Long)
}
