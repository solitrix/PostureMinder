package com.solitrix.postureminder.shared.domain.usecase

import com.solitrix.postureminder.shared.mock.MockTaskTypeRepository
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class UpdateTaskTypeUseCaseTest {

    private val repo = MockTaskTypeRepository()
    private val useCase = UpdateTaskTypeUseCase(repo)

    @Test
    fun `given name with whitespace when updated then trimmed name is passed to repository`() = runTest {
        useCase(id = 3L, name = "  Recline  ", colorRgb = 0x2196F3)

        val (id, name, color) = repo.updateCalls.single()
        assertEquals(3L, id)
        assertEquals("Recline", name)
        assertEquals(0x2196F3, color)
    }

    @Test
    fun `given plain name when updated then name and color forwarded unchanged`() = runTest {
        useCase(id = 1L, name = "Stand", colorRgb = 0x4CAF50)

        assertEquals(Triple(1L, "Stand", 0x4CAF50), repo.updateCalls.single())
    }
}
