package com.solitrix.postureminder.shared.domain.usecase

import com.solitrix.postureminder.shared.mock.MockActiveScheduleRepository
import com.solitrix.postureminder.shared.mock.MockScheduleRepository
import com.solitrix.postureminder.shared.mock.schedule
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class DeleteScheduleUseCaseTest {

    private fun useCase(
        scheduleRepo: MockScheduleRepository,
        activeRepo: MockActiveScheduleRepository,
    ) = DeleteScheduleUseCase(scheduleRepo, activeRepo)

    @Test
    fun `given non-active schedule deleted when invoked then active schedule unchanged`() = runTest {
        val scheduleRepo = MockScheduleRepository()
        scheduleRepo.setSchedules(schedule(1L, "Work"), schedule(2L, "Home"))
        val activeRepo = MockActiveScheduleRepository(initialId = 1L)

        useCase(scheduleRepo, activeRepo)(scheduleId = 2L)

        assertEquals(1L, activeRepo.activeScheduleId.value)
        assertTrue(2L in scheduleRepo.deletedIds)
    }

    @Test
    fun `given active schedule deleted when remaining schedules exist then switches to first remaining`() = runTest {
        val scheduleRepo = MockScheduleRepository()
        scheduleRepo.setSchedules(schedule(1L, "Work"), schedule(2L, "Home"))
        val activeRepo = MockActiveScheduleRepository(initialId = 1L)

        useCase(scheduleRepo, activeRepo)(scheduleId = 1L)

        assertEquals(2L, activeRepo.activeScheduleId.value)
        assertTrue(1L in scheduleRepo.deletedIds)
    }

    @Test
    fun `given active schedule deleted when no remaining schedules then active schedule is not changed`() = runTest {
        val scheduleRepo = MockScheduleRepository()
        scheduleRepo.setSchedules(schedule(1L, "Work"))
        val activeRepo = MockActiveScheduleRepository(initialId = 1L)

        useCase(scheduleRepo, activeRepo)(scheduleId = 1L)

        // deleteSchedule removes it; remaining = []; firstOrNull is null so setActiveSchedule is never called
        assertTrue(activeRepo.setCalls.isEmpty())
    }
}
