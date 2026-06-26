package com.solitrix.postureminder.shared.reminder

import com.solitrix.postureminder.shared.domain.model.ScheduledTask

data class SlotTransition(val dayIndex: Int?, val slotIndex: Int?)

fun findDueTask(transition: SlotTransition, tasks: List<ScheduledTask>): ScheduledTask? {
    if (transition.dayIndex == null) return null
    val slot = transition.slotIndex ?: return null
    return tasks.firstOrNull { it.slotIndex == slot }
}
