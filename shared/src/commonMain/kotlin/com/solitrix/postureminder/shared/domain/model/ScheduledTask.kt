package com.solitrix.postureminder.shared.domain.model

data class ScheduledTask(
    val id: Long,
    val day: Int,
    val slotIndex: Int,
    val durationSlots: Int,
    val taskTypeId: Long,
    val scheduleId: Long,
    val taskTypeName: String,
    val taskTypeColorRgb: Int,
)
