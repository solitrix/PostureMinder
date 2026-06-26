package com.solitrix.postureminder.shared.domain.usecase

import com.solitrix.postureminder.shared.mock.MockTaskRepository
import com.solitrix.postureminder.shared.mock.task
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ResizeTaskTopUseCaseTest {

    private val repo = MockTaskRepository()
    private val useCase = ResizeTaskTopUseCase(repo)

    @Test
    fun `given unknown task id when top resized then nothing happens`() = runTest {
        useCase(taskId = 99L, newSlotIndex = 0, currentTasks = listOf(task(id = 1L, slotIndex = 5)))

        assertTrue(repo.updateCalls.isEmpty())
    }

    @Test
    fun `given top moved up into free slots when resized then slot decreases and duration increases`() = runTest {
        val target = task(id = 1L, slotIndex = 5, durationSlots = 2)

        useCase(taskId = 1L, newSlotIndex = 3, currentTasks = listOf(target))

        val updated = repo.updateCalls.single()
        assertEquals(3, updated.slotIndex)
        assertEquals(4, updated.durationSlots)
    }

    @Test
    fun `given top moved down within task when resized then slot increases and duration decreases`() = runTest {
        val target = task(id = 1L, slotIndex = 3, durationSlots = 4)

        useCase(taskId = 1L, newSlotIndex = 5, currentTasks = listOf(target))

        val updated = repo.updateCalls.single()
        assertEquals(5, updated.slotIndex)
        assertEquals(2, updated.durationSlots)
    }

    @Test
    fun `given new slot below zero when resized then slot is clamped to zero`() = runTest {
        val target = task(id = 1L, slotIndex = 2, durationSlots = 3)

        useCase(taskId = 1L, newSlotIndex = -5, currentTasks = listOf(target))

        val updated = repo.updateCalls.single()
        assertEquals(0, updated.slotIndex)
        assertEquals(5, updated.durationSlots)
    }

    @Test
    fun `given new slot at bottom minus one when resized then duration is one`() = runTest {
        val target = task(id = 1L, slotIndex = 3, durationSlots = 3)

        useCase(taskId = 1L, newSlotIndex = 5, currentTasks = listOf(target))

        val updated = repo.updateCalls.single()
        assertEquals(5, updated.slotIndex)
        assertEquals(1, updated.durationSlots)
    }

    @Test
    fun `given another task occupies a slot in the expanded range when resized then no update`() = runTest {
        val other = task(id = 2L, slotIndex = 4, durationSlots = 1)
        val target = task(id = 1L, slotIndex = 6, durationSlots = 2)

        useCase(taskId = 1L, newSlotIndex = 3, currentTasks = listOf(other, target))

        assertTrue(repo.updateCalls.isEmpty())
    }
}
