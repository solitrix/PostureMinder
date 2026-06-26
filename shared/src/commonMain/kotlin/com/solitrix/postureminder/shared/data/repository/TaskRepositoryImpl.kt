package com.solitrix.postureminder.shared.data.repository

import com.solitrix.postureminder.shared.data.dao.TaskDao
import com.solitrix.postureminder.shared.data.entity.Task
import com.solitrix.postureminder.shared.data.mapper.toDomain
import com.solitrix.postureminder.shared.data.mapper.toEntity
import com.solitrix.postureminder.shared.domain.model.ScheduledTask
import com.solitrix.postureminder.shared.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TaskRepositoryImpl(private val dao: TaskDao) : TaskRepository {

    override fun getTasksByDayAndSchedule(day: Int, scheduleId: Long): Flow<List<ScheduledTask>> =
        dao.getByDayAndSetWithDetailsAsFlow(day, scheduleId).map { list -> list.map { it.toDomain() } }

    override suspend fun addTask(day: Int, slotIndex: Int, durationSlots: Int, taskTypeId: Long, scheduleId: Long) {
        dao.insert(Task(day = day, slotIndex = slotIndex, durationSlots = durationSlots, taskType = taskTypeId, set = scheduleId))
    }

    override suspend fun updateTask(task: ScheduledTask) = dao.update(task.toEntity())

    override suspend fun removeTask(taskId: Long) = dao.deleteById(taskId)
}
