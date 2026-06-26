package com.solitrix.postureminder.shared.mock

import com.solitrix.postureminder.shared.domain.model.ScheduledTask
import com.solitrix.postureminder.shared.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class MockTaskRepository : TaskRepository {

    private val _tasks = MutableStateFlow<List<ScheduledTask>>(emptyList())

    val addCalls = mutableListOf<Map<String, Any>>()
    val updateCalls = mutableListOf<ScheduledTask>()
    val removeCalls = mutableListOf<Long>()

    fun setTasks(vararg tasks: ScheduledTask) {
        _tasks.value = tasks.toList()
    }

    fun setTasks(tasks: List<ScheduledTask>) {
        _tasks.value = tasks
    }

    override fun getTasksByDayAndSchedule(day: Int, scheduleId: Long): Flow<List<ScheduledTask>> =
        _tasks.asStateFlow()

    override suspend fun addTask(
        day: Int,
        slotIndex: Int,
        durationSlots: Int,
        taskTypeId: Long,
        scheduleId: Long,
    ) {
        addCalls.add(
            mapOf(
                "day" to day,
                "slotIndex" to slotIndex,
                "durationSlots" to durationSlots,
                "taskTypeId" to taskTypeId,
                "scheduleId" to scheduleId,
            )
        )
        val id = (_tasks.value.maxOfOrNull { it.id } ?: 0L) + 1L
        val newTask = ScheduledTask(
            id = id, day = day, slotIndex = slotIndex, durationSlots = durationSlots,
            taskTypeId = taskTypeId, scheduleId = scheduleId, taskTypeName = "", taskTypeColorRgb = 0,
        )
        _tasks.value = _tasks.value + newTask
    }

    override suspend fun updateTask(task: ScheduledTask) {
        updateCalls.add(task)
        _tasks.value = _tasks.value.map { if (it.id == task.id) task else it }
    }

    override suspend fun removeTask(taskId: Long) {
        removeCalls.add(taskId)
        _tasks.value = _tasks.value.filter { it.id != taskId }
    }
}
