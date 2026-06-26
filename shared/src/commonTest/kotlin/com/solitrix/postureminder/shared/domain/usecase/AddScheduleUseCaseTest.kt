package com.solitrix.postureminder.shared.domain.usecase

import com.solitrix.postureminder.shared.mock.MockScheduleRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class AddScheduleUseCaseTest {

    private val repo = MockScheduleRepository()
    private val useCase = AddScheduleUseCase(repo)

    @Test
    fun `given name with whitespace when invoked then trimmed name is stored`() = runTest {
        useCase("  Work  ")

        val stored = repo.getSchedules().first()
        assertEquals("Work", stored.first().name)
    }

    @Test
    fun `given plain name when invoked then returned id is non-zero`() = runTest {
        val id = useCase("Home")

        assertEquals(1L, id)
    }

    @Test
    fun `given two schedules added when invoked then ids are sequential`() = runTest {
        val id1 = useCase("Work")
        val id2 = useCase("Home")

        assertEquals(1L, id1)
        assertEquals(2L, id2)
    }
}
