package com.solitrix.postureminder.shared.domain.usecase

import app.cash.turbine.test
import com.solitrix.postureminder.shared.mock.MockActiveScheduleRepository
import com.solitrix.postureminder.shared.mock.MockReminderRepository
import com.solitrix.postureminder.shared.mock.MockScheduleRepository
import com.solitrix.postureminder.shared.mock.MockTaskRepository
import com.solitrix.postureminder.shared.mock.MockTaskTypeRepository
import com.solitrix.postureminder.shared.mock.schedule
import com.solitrix.postureminder.shared.mock.task
import com.solitrix.postureminder.shared.mock.taskType
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ObserveScheduledTasksUseCaseTest {

    @Test
    fun `given tasks in repository when observed then tasks are emitted`() = runTest {
        val repo = MockTaskRepository()
        val tasks = listOf(task(id = 1L, slotIndex = 3))
        repo.setTasks(tasks)

        ObserveScheduledTasksUseCase(repo)(day = 0, scheduleId = 1L).test {
            assertEquals(tasks, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `given repository updates when observed then new tasks are emitted`() = runTest {
        val repo = MockTaskRepository()
        val useCase = ObserveScheduledTasksUseCase(repo)

        useCase(day = 0, scheduleId = 1L).test {
            assertEquals(emptyList(), awaitItem())

            val newTasks = listOf(task(id = 1L, slotIndex = 5))
            repo.setTasks(newTasks)

            assertEquals(newTasks, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }
}

class ObserveTaskTypesUseCaseTest {

    @Test
    fun `given types in repository when observed then types are emitted`() = runTest {
        val repo = MockTaskTypeRepository()
        val types = listOf(taskType(1L, "Stand"))
        repo.setTypes(types)

        ObserveTaskTypesUseCase(repo)().test {
            assertEquals(types, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }
}

class ObserveSchedulesUseCaseTest {

    @Test
    fun `given schedules in repository when observed then schedules are emitted`() = runTest {
        val repo = MockScheduleRepository()
        repo.setSchedules(schedule(1L, "Work"), schedule(2L, "Home"))

        ObserveSchedulesUseCase(repo)().test {
            assertEquals(2, awaitItem().size)
            cancelAndIgnoreRemainingEvents()
        }
    }
}

class ObserveActiveScheduleUseCaseTest {

    @Test
    fun `given active schedule when observed then current id is returned`() = runTest {
        val repo = MockActiveScheduleRepository(initialId = 7L)

        val flow = ObserveActiveScheduleUseCase(repo)()

        assertEquals(7L, flow.value)
    }
}

class ObserveRemindersRunningUseCaseTest {

    @Test
    fun `given reminders running when observed then true is returned`() = runTest {
        val repo = MockReminderRepository(initiallyRunning = true)

        val flow = ObserveRemindersRunningUseCase(repo)()

        assertTrue(flow.value)
    }

    @Test
    fun `given reminders not running when observed then false is returned`() = runTest {
        val repo = MockReminderRepository(initiallyRunning = false)

        val flow = ObserveRemindersRunningUseCase(repo)()

        assertFalse(flow.value)
    }
}

class SetActiveScheduleUseCaseTest {

    @Test
    fun `given schedule id when invoked then repository setActiveSchedule is called`() = runTest {
        val repo = MockActiveScheduleRepository(initialId = 1L)

        SetActiveScheduleUseCase(repo)(scheduleId = 5L)

        assertEquals(5L, repo.activeScheduleId.value)
        assertEquals(listOf(5L), repo.setCalls)
    }
}

class ToggleRemindersUseCaseTest {

    @Test
    fun `given reminders off when toggled then reminders become running`() = runTest {
        val repo = MockReminderRepository(initiallyRunning = false)

        ToggleRemindersUseCase(repo)()

        assertTrue(repo.isRunning.value)
        assertEquals(1, repo.toggleCount)
    }

    @Test
    fun `given reminders on when toggled then reminders stop`() = runTest {
        val repo = MockReminderRepository(initiallyRunning = true)

        ToggleRemindersUseCase(repo)()

        assertFalse(repo.isRunning.value)
    }
}
