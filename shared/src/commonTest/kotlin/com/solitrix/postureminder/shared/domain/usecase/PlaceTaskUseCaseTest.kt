package com.solitrix.postureminder.shared.domain.usecase

import com.solitrix.postureminder.shared.mock.MockTaskRepository
import com.solitrix.postureminder.shared.mock.task
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class PlaceTaskUseCaseTest {

    private val repo = MockTaskRepository()
    private val useCase = PlaceTaskUseCase(repo)

    @Test
    fun `given empty slot when placed then task is added to repository`() = runTest {
        useCase(taskTypeId = 2L, slotIndex = 5, day = 0, scheduleId = 1L, currentTasks = emptyList())

        assertEquals(1, repo.addCalls.size)
        assertEquals(5, repo.addCalls.first()["slotIndex"])
        assertEquals(2L, repo.addCalls.first()["taskTypeId"])
    }

    @Test
    fun `given occupied slot when placed then repository is not called`() = runTest {
        val existing = task(id = 1L, slotIndex = 5, durationSlots = 2)

        useCase(taskTypeId = 2L, slotIndex = 5, day = 0, scheduleId = 1L, currentTasks = listOf(existing))
        useCase(taskTypeId = 2L, slotIndex = 6, day = 0, scheduleId = 1L, currentTasks = listOf(existing))

        assertTrue(repo.addCalls.isEmpty())
    }

    @Test
    fun `given multi-slot task when placing at covered slot then repository is not called`() = runTest {
        val existing = task(id = 1L, slotIndex = 3, durationSlots = 3)

        useCase(taskTypeId = 2L, slotIndex = 4, day = 0, scheduleId = 1L, currentTasks = listOf(existing))

        assertTrue(repo.addCalls.isEmpty())
    }

    @Test
    fun `given adjacent free slot when placed then task is added`() = runTest {
        val existing = task(id = 1L, slotIndex = 3, durationSlots = 2)

        useCase(taskTypeId = 2L, slotIndex = 5, day = 0, scheduleId = 1L, currentTasks = listOf(existing))

        assertEquals(1, repo.addCalls.size)
        assertEquals(5, repo.addCalls.first()["slotIndex"])
    }
}
