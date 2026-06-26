package com.solitrix.postureminder.shared.data.repository

import com.solitrix.postureminder.shared.mock.MockScheduleRepository
import com.solitrix.postureminder.shared.mock.schedule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class ActiveScheduleRepositoryImplTest {

    @Test
    fun `given schedules when init then active schedule id snaps to first schedule`() =
        runTest(UnconfinedTestDispatcher()) {
            val scheduleRepo = MockScheduleRepository()
            scheduleRepo.setSchedules(schedule(5L, "Work"), schedule(6L, "Home"))

            val repo = ActiveScheduleRepositoryImpl(scheduleRepo, this)

            assertEquals(5L, repo.activeScheduleId.value)
        }

    @Test
    fun `given empty schedule list when init then active schedule id keeps default`() =
        runTest(UnconfinedTestDispatcher()) {
            val scheduleRepo = MockScheduleRepository()

            val repo = ActiveScheduleRepositoryImpl(scheduleRepo, this)

            assertEquals(1L, repo.activeScheduleId.value)
        }

    @Test
    fun `given new id when setActiveSchedule then value is updated`() =
        runTest(UnconfinedTestDispatcher()) {
            val scheduleRepo = MockScheduleRepository()
            scheduleRepo.setSchedules(schedule(1L))
            val repo = ActiveScheduleRepositoryImpl(scheduleRepo, this)

            repo.setActiveSchedule(99L)

            assertEquals(99L, repo.activeScheduleId.value)
        }
}
