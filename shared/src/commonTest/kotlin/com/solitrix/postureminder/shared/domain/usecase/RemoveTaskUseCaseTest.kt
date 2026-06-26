package com.solitrix.postureminder.shared.domain.usecase

import com.solitrix.postureminder.shared.mock.MockTaskRepository
import com.solitrix.postureminder.shared.mock.task
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class RemoveTaskUseCaseTest {

    private val repo = MockTaskRepository()
    private val useCase = RemoveTaskUseCase(repo)

    @Test
    fun `given task id when invoked then repository removeTask is called`() = runTest {
        useCase(taskId = 42L)

        assertEquals(listOf(42L), repo.removeCalls)
    }

    @Test
    fun `given multiple calls when invoked then each id is forwarded`() = runTest {
        useCase(taskId = 1L)
        useCase(taskId = 2L)
        useCase(taskId = 3L)

        assertEquals(listOf(1L, 2L, 3L), repo.removeCalls)
    }
}
