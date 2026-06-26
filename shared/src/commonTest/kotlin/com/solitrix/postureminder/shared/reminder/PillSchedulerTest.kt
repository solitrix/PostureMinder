package com.solitrix.postureminder.shared.reminder

import com.solitrix.postureminder.shared.mock.task
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class PillSchedulerTest {

    @Test
    fun `given null dayIndex when findDueTask then returns null`() {
        val transition = SlotTransition(dayIndex = null, slotIndex = 3)

        assertNull(findDueTask(transition, listOf(task(slotIndex = 3))))
    }

    @Test
    fun `given null slotIndex when findDueTask then returns null`() {
        val transition = SlotTransition(dayIndex = 0, slotIndex = null)

        assertNull(findDueTask(transition, listOf(task(slotIndex = 3))))
    }

    @Test
    fun `given matching slot index when findDueTask then returns matching task`() {
        val expected = task(id = 2L, slotIndex = 5)
        val transition = SlotTransition(dayIndex = 0, slotIndex = 5)

        val result = findDueTask(transition, listOf(task(id = 1L, slotIndex = 3), expected))

        assertEquals(expected, result)
    }

    @Test
    fun `given no task at slot when findDueTask then returns null`() {
        val transition = SlotTransition(dayIndex = 0, slotIndex = 10)

        assertNull(findDueTask(transition, listOf(task(slotIndex = 3), task(slotIndex = 7))))
    }

    @Test
    fun `given empty task list when findDueTask then returns null`() {
        val transition = SlotTransition(dayIndex = 0, slotIndex = 5)

        assertNull(findDueTask(transition, emptyList()))
    }
}
