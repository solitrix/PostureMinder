package com.solitrix.postureminder.shared.domain.usecase

import com.solitrix.postureminder.shared.domain.model.SLOT_COUNT
import com.solitrix.postureminder.shared.mock.MockTaskRepository
import com.solitrix.postureminder.shared.mock.task
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class MoveTaskUseCaseTest {

    private val repo = MockTaskRepository()
    private val useCase = MoveTaskUseCase(repo)

    @Test
    fun `given valid free slot when moved then task is updated with new slot index`() = runTest {
        val moving = task(id = 1L, slotIndex = 2)
        val other = task(id = 2L, slotIndex = 8)

        useCase(taskId = 1L, newSlotIndex = 5, currentTasks = listOf(moving, other))

        assertEquals(1, repo.updateCalls.size)
        assertEquals(5, repo.updateCalls.first().slotIndex)
        assertEquals(1L, repo.updateCalls.first().id)
    }

    @Test
    fun `given unknown task id when moved then no update is made`() = runTest {
        val existing = task(id = 1L, slotIndex = 2)

        useCase(taskId = 99L, newSlotIndex = 5, currentTasks = listOf(existing))

        assertTrue(repo.updateCalls.isEmpty())
    }

    @Test
    fun `given slot occupied by another task when moved then no update is made`() = runTest {
        val moving = task(id = 1L, slotIndex = 2)
        val blocker = task(id = 2L, slotIndex = 5, durationSlots = 2)

        useCase(taskId = 1L, newSlotIndex = 5, currentTasks = listOf(moving, blocker))
        useCase(taskId = 1L, newSlotIndex = 6, currentTasks = listOf(moving, blocker))

        assertTrue(repo.updateCalls.isEmpty())
    }

    @Test
    fun `given new position would exceed slot count when moved then no update is made`() = runTest {
        val moving = task(id = 1L, slotIndex = 2, durationSlots = 2)

        useCase(taskId = 1L, newSlotIndex = SLOT_COUNT - 1, currentTasks = listOf(moving))

        assertTrue(repo.updateCalls.isEmpty())
    }

    @Test
    fun `given task moved to last valid position when moved then task is updated`() = runTest {
        val moving = task(id = 1L, slotIndex = 0, durationSlots = 2)
        val lastValid = SLOT_COUNT - 2

        useCase(taskId = 1L, newSlotIndex = lastValid, currentTasks = listOf(moving))

        assertEquals(1, repo.updateCalls.size)
        assertEquals(lastValid, repo.updateCalls.first().slotIndex)
    }
}
