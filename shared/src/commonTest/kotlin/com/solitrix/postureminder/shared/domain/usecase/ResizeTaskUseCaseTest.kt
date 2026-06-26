package com.solitrix.postureminder.shared.domain.usecase

import com.solitrix.postureminder.shared.domain.model.SLOT_COUNT
import com.solitrix.postureminder.shared.mock.MockTaskRepository
import com.solitrix.postureminder.shared.mock.task
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ResizeTaskUseCaseTest {

    private val repo = MockTaskRepository()
    private val useCase = ResizeTaskUseCase(repo)

    @Test
    fun `given unknown task id when resized then nothing happens`() = runTest {
        useCase(taskId = 99L, newDurationSlots = 3, currentTasks = listOf(task(id = 1L)))

        assertTrue(repo.updateCalls.isEmpty())
        assertTrue(repo.removeCalls.isEmpty())
    }

    @Test
    fun `given shrink when resized then only the target task is updated`() = runTest {
        val target = task(id = 1L, slotIndex = 2, durationSlots = 4)
        val other = task(id = 2L, slotIndex = 8)

        useCase(taskId = 1L, newDurationSlots = 2, currentTasks = listOf(target, other))

        assertEquals(1, repo.updateCalls.size)
        assertEquals(2, repo.updateCalls.first().durationSlots)
        assertTrue(repo.removeCalls.isEmpty())
    }

    @Test
    fun `given duration below 1 when resized then duration is clamped to 1`() = runTest {
        val target = task(id = 1L, slotIndex = 2, durationSlots = 3)

        useCase(taskId = 1L, newDurationSlots = 0, currentTasks = listOf(target))

        assertEquals(1, repo.updateCalls.first().durationSlots)
    }

    @Test
    fun `given grow when trailing task fits after push then both tasks are updated`() = runTest {
        val target = task(id = 1L, slotIndex = 2, durationSlots = 1)
        val trailing = task(id = 2L, slotIndex = 3, durationSlots = 1)

        useCase(taskId = 1L, newDurationSlots = 3, currentTasks = listOf(target, trailing))

        val updatedIds = repo.updateCalls.map { it.id }
        assertTrue(1L in updatedIds)
        assertTrue(2L in updatedIds)

        val updatedTarget = repo.updateCalls.first { it.id == 1L }
        val updatedTrailing = repo.updateCalls.first { it.id == 2L }

        assertEquals(3, updatedTarget.durationSlots)
        assertEquals(5, updatedTrailing.slotIndex)
        assertTrue(repo.removeCalls.isEmpty())
    }

    @Test
    fun `given grow when trailing task pushed beyond slot count then it is removed`() = runTest {
        val target = task(id = 1L, slotIndex = 0, durationSlots = 1)
        val trailing = task(id = 2L, slotIndex = 1, durationSlots = 1)
        val delta = SLOT_COUNT - 1

        useCase(taskId = 1L, newDurationSlots = delta + 1, currentTasks = listOf(target, trailing))

        assertTrue(2L in repo.removeCalls)
    }

    @Test
    fun `given task before target when grown then it is not pushed`() = runTest {
        val before = task(id = 2L, slotIndex = 0, durationSlots = 1)
        val target = task(id = 1L, slotIndex = 2, durationSlots = 1)

        useCase(taskId = 1L, newDurationSlots = 3, currentTasks = listOf(before, target))

        val updatedIds = repo.updateCalls.map { it.id }
        assertFalse(2L in updatedIds, "task before target should not be pushed")
    }
}
