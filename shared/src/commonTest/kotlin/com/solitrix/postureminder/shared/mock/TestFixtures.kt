package com.solitrix.postureminder.shared.mock

import com.solitrix.postureminder.shared.domain.model.ScheduledTask
import com.solitrix.postureminder.shared.domain.model.ScheduleModel
import com.solitrix.postureminder.shared.domain.model.TaskTypeModel

fun task(
    id: Long = 1L,
    day: Int = 0,
    slotIndex: Int = 0,
    durationSlots: Int = 1,
    taskTypeId: Long = 1L,
    scheduleId: Long = 1L,
    taskTypeName: String = "Stand",
    taskTypeColorRgb: Int = 0x4CAF50,
): ScheduledTask = ScheduledTask(
    id = id,
    day = day,
    slotIndex = slotIndex,
    durationSlots = durationSlots,
    taskTypeId = taskTypeId,
    scheduleId = scheduleId,
    taskTypeName = taskTypeName,
    taskTypeColorRgb = taskTypeColorRgb,
)

fun schedule(id: Long = 1L, name: String = "Work") = ScheduleModel(id, name)

fun taskType(id: Long = 1L, name: String = "Stand", colorRgb: Int = 0x4CAF50) =
    TaskTypeModel(id, name, colorRgb)
