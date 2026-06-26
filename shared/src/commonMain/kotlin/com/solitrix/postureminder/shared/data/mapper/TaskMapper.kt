package com.solitrix.postureminder.shared.data.mapper

import com.solitrix.postureminder.shared.data.entity.Task
import com.solitrix.postureminder.shared.data.model.TaskWithDetails
import com.solitrix.postureminder.shared.domain.model.ScheduledTask

fun TaskWithDetails.toDomain() = ScheduledTask(
    id = task.id,
    day = task.day,
    slotIndex = task.slotIndex,
    durationSlots = task.durationSlots,
    taskTypeId = task.taskType,
    scheduleId = task.set,
    taskTypeName = taskType.name,
    taskTypeColorRgb = taskType.colorRgb,
)

fun ScheduledTask.toEntity() = Task(
    id = id,
    day = day,
    slotIndex = slotIndex,
    durationSlots = durationSlots,
    taskType = taskTypeId,
    set = scheduleId,
)
