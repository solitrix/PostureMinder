package com.solitrix.postureminder.shared.domain.usecase

import com.solitrix.postureminder.shared.mock.MockTaskTypeRepository
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class AddTaskTypeUseCaseTest {

    private val repo = MockTaskTypeRepository()
    private val useCase = AddTaskTypeUseCase(repo)

    @Test
    fun `given name with whitespace when invoked then trimmed name is passed to repository`() = runTest {
        useCase("  Sit  ", 0xFF0000)

        assertEquals("Sit", repo.addCalls.first().first)
        assertEquals(0xFF0000, repo.addCalls.first().second)
    }

    @Test
    fun `given color when invoked then color is passed unchanged`() = runTest {
        useCase("Stand", 0x4CAF50)

        assertEquals(0x4CAF50, repo.addCalls.first().second)
    }
}
