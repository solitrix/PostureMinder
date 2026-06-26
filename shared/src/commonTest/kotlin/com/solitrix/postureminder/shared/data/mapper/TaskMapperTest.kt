package com.solitrix.postureminder.shared.data.mapper

import com.solitrix.postureminder.shared.data.entity.Set as SetEntity
import com.solitrix.postureminder.shared.data.entity.Task
import com.solitrix.postureminder.shared.data.entity.TaskType
import com.solitrix.postureminder.shared.data.model.TaskWithDetails
import com.solitrix.postureminder.shared.domain.model.ScheduledTask
import kotlin.test.Test
import kotlin.test.assertEquals

class TaskMapperTest {

    private val entity = TaskWithDetails(
        task = Task(day = 2, slotIndex = 5, durationSlots = 3, taskType = 7L, set = 4L, id = 10L),
        taskType = TaskType(id = 7L, name = "Sit", colorRgb = 0xFF0000),
        set = SetEntity(name = "Work", id = 4L),
    )

    @Test
    fun `given TaskWithDetails when toDomain then all fields are mapped correctly`() {
        val domain = entity.toDomain()

        assertEquals(10L, domain.id)
        assertEquals(2, domain.day)
        assertEquals(5, domain.slotIndex)
        assertEquals(3, domain.durationSlots)
        assertEquals(7L, domain.taskTypeId)
        assertEquals(4L, domain.scheduleId)
        assertEquals("Sit", domain.taskTypeName)
        assertEquals(0xFF0000, domain.taskTypeColorRgb)
    }

    @Test
    fun `given ScheduledTask when toEntity then all fields are mapped correctly`() {
        val domain = ScheduledTask(
            id = 10L, day = 2, slotIndex = 5, durationSlots = 3,
            taskTypeId = 7L, scheduleId = 4L,
            taskTypeName = "Sit", taskTypeColorRgb = 0xFF0000,
        )

        val result = domain.toEntity()

        assertEquals(10L, result.id)
        assertEquals(2, result.day)
        assertEquals(5, result.slotIndex)
        assertEquals(3, result.durationSlots)
        assertEquals(7L, result.taskType)
        assertEquals(4L, result.set)
    }

    @Test
    fun `given toDomain then toEntity round trips are consistent`() {
        val domain = entity.toDomain()
        val back = domain.toEntity()

        assertEquals(entity.task, back)
    }
}
