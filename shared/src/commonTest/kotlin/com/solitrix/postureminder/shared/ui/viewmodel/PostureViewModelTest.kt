package com.solitrix.postureminder.shared.ui.viewmodel

import app.cash.turbine.test
import com.solitrix.postureminder.shared.domain.usecase.AddScheduleUseCase
import com.solitrix.postureminder.shared.domain.usecase.AddTaskTypeUseCase
import com.solitrix.postureminder.shared.domain.usecase.DeleteScheduleUseCase
import com.solitrix.postureminder.shared.domain.usecase.MoveTaskUseCase
import com.solitrix.postureminder.shared.domain.usecase.ObserveActiveScheduleUseCase
import com.solitrix.postureminder.shared.domain.usecase.ObserveRemindersRunningUseCase
import com.solitrix.postureminder.shared.domain.usecase.ObserveScheduledTasksUseCase
import com.solitrix.postureminder.shared.domain.usecase.ObserveSchedulesUseCase
import com.solitrix.postureminder.shared.domain.usecase.ObserveTaskTypesUseCase
import com.solitrix.postureminder.shared.domain.usecase.PlaceTaskUseCase
import com.solitrix.postureminder.shared.domain.usecase.RemoveTaskUseCase
import com.solitrix.postureminder.shared.domain.usecase.ResizeTaskTopUseCase
import com.solitrix.postureminder.shared.domain.usecase.ResizeTaskUseCase
import com.solitrix.postureminder.shared.domain.usecase.SetActiveScheduleUseCase
import com.solitrix.postureminder.shared.domain.usecase.ToggleRemindersUseCase
import com.solitrix.postureminder.shared.domain.usecase.UpdateTaskTypeUseCase
import com.solitrix.postureminder.shared.mock.MockActiveScheduleRepository
import com.solitrix.postureminder.shared.mock.MockReminderRepository
import com.solitrix.postureminder.shared.mock.MockScheduleRepository
import com.solitrix.postureminder.shared.mock.MockTaskRepository
import com.solitrix.postureminder.shared.mock.MockTaskTypeRepository
import com.solitrix.postureminder.shared.mock.schedule
import com.solitrix.postureminder.shared.mock.task
import com.solitrix.postureminder.shared.mock.taskType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class PostureViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // ── helpers ───────────────────────────────────────────────────────────────

    private fun buildViewModel(
        taskRepo: MockTaskRepository = MockTaskRepository(),
        taskTypeRepo: MockTaskTypeRepository = MockTaskTypeRepository(),
        scheduleRepo: MockScheduleRepository = MockScheduleRepository(),
        activeRepo: MockActiveScheduleRepository = MockActiveScheduleRepository(),
        reminderRepo: MockReminderRepository = MockReminderRepository(),
    ): PostureViewModel = PostureViewModel(
        observeScheduledTasks = ObserveScheduledTasksUseCase(taskRepo),
        placeTask = PlaceTaskUseCase(taskRepo),
        moveTask = MoveTaskUseCase(taskRepo),
        removeTask = RemoveTaskUseCase(taskRepo),
        resizeTask = ResizeTaskUseCase(taskRepo),
        resizeTaskTop = ResizeTaskTopUseCase(taskRepo),
        observeTaskTypes = ObserveTaskTypesUseCase(taskTypeRepo),
        addTaskType = AddTaskTypeUseCase(taskTypeRepo),
        updateTaskType = UpdateTaskTypeUseCase(taskTypeRepo),
        observeSchedules = ObserveSchedulesUseCase(scheduleRepo),
        addSchedule = AddScheduleUseCase(scheduleRepo),
        deleteSchedule = DeleteScheduleUseCase(scheduleRepo, activeRepo),
        observeActiveSchedule = ObserveActiveScheduleUseCase(activeRepo),
        setActiveSchedule = SetActiveScheduleUseCase(activeRepo),
        observeRemindersRunning = ObserveRemindersRunningUseCase(reminderRepo),
        toggleReminders = ToggleRemindersUseCase(reminderRepo),
    )

    // ── init observers ────────────────────────────────────────────────────────

    @Test
    fun `given task types in repo when init then state contains task types`() = runTest(testDispatcher) {
        val taskTypeRepo = MockTaskTypeRepository()
        taskTypeRepo.setTypes(taskType(1L, "Stand"), taskType(2L, "Sit"))

        val vm = buildViewModel(taskTypeRepo = taskTypeRepo)

        assertEquals(2, vm.state.value.taskTypes.size)
        assertEquals("Stand", vm.state.value.taskTypes[0].name)
    }

    @Test
    fun `given schedules in repo when init then state contains schedules`() = runTest(testDispatcher) {
        val scheduleRepo = MockScheduleRepository()
        scheduleRepo.setSchedules(schedule(1L, "Work"), schedule(2L, "Home"))

        val vm = buildViewModel(scheduleRepo = scheduleRepo)

        assertEquals(2, vm.state.value.schedules.size)
    }

    @Test
    fun `given active schedule id when init then state reflects active schedule id`() = runTest(testDispatcher) {
        val activeRepo = MockActiveScheduleRepository(initialId = 7L)

        val vm = buildViewModel(activeRepo = activeRepo)

        assertEquals(7L, vm.state.value.activeScheduleId)
    }

    @Test
    fun `given reminders running when init then state reflects running state`() = runTest(testDispatcher) {
        val reminderRepo = MockReminderRepository(initiallyRunning = true)

        val vm = buildViewModel(reminderRepo = reminderRepo)

        assertTrue(vm.state.value.remindersRunning)
    }

    @Test
    fun `given tasks in repo when init then placed tasks are loaded`() = runTest(testDispatcher) {
        val taskRepo = MockTaskRepository()
        taskRepo.setTasks(task(id = 1L, slotIndex = 5))

        val vm = buildViewModel(taskRepo = taskRepo)

        assertEquals(1, vm.state.value.placedTasks.size)
        assertEquals(5, vm.state.value.placedTasks[0].slotIndex)
    }

    // ── SelectDay ─────────────────────────────────────────────────────────────

    @Test
    fun `given valid day when SelectDay then selectedDay is updated`() = runTest(testDispatcher) {
        val vm = buildViewModel()
        vm.onAction(Action.SelectDay(3))
        assertEquals(3, vm.state.value.selectedDay)
    }

    @Test
    fun `given day below 0 when SelectDay then selectedDay is clamped to 0`() = runTest(testDispatcher) {
        val vm = buildViewModel()
        vm.onAction(Action.SelectDay(-1))
        assertEquals(0, vm.state.value.selectedDay)
    }

    @Test
    fun `given day above 4 when SelectDay then selectedDay is clamped to 4`() = runTest(testDispatcher) {
        val vm = buildViewModel()
        vm.onAction(Action.SelectDay(10))
        assertEquals(4, vm.state.value.selectedDay)
    }

    @Test
    fun `given active task when SelectDay then activeTaskId is cleared`() = runTest(testDispatcher) {
        val vm = buildViewModel()
        vm.onAction(Action.ToggleTaskActive(taskId = 5L))
        assertEquals(5L, vm.state.value.activeTaskId)

        vm.onAction(Action.SelectDay(2))

        assertNull(vm.state.value.activeTaskId)
    }

    // ── ToggleTaskActive ───────────────────────────────────────────────────────

    @Test
    fun `given no active task when ToggleTaskActive then task becomes active`() = runTest(testDispatcher) {
        val vm = buildViewModel()
        vm.onAction(Action.ToggleTaskActive(taskId = 3L))
        assertEquals(3L, vm.state.value.activeTaskId)
    }

    @Test
    fun `given same task active when ToggleTaskActive then task is deactivated`() = runTest(testDispatcher) {
        val vm = buildViewModel()
        vm.onAction(Action.ToggleTaskActive(taskId = 3L))
        vm.onAction(Action.ToggleTaskActive(taskId = 3L))
        assertNull(vm.state.value.activeTaskId)
    }

    @Test
    fun `given different task active when ToggleTaskActive then switches to new task`() = runTest(testDispatcher) {
        val vm = buildViewModel()
        vm.onAction(Action.ToggleTaskActive(taskId = 1L))
        vm.onAction(Action.ToggleTaskActive(taskId = 2L))
        assertEquals(2L, vm.state.value.activeTaskId)
    }

    // ── Dialog actions ────────────────────────────────────────────────────────

    @Test
    fun `given ShowAddTypeDialog when invoked then showAddTypeDialog becomes true`() = runTest(testDispatcher) {
        val vm = buildViewModel()
        vm.onAction(Action.ShowAddTypeDialog)
        assertTrue(vm.state.value.showAddTypeDialog)
    }

    @Test
    fun `given DismissAddTypeDialog when invoked then showAddTypeDialog becomes false`() = runTest(testDispatcher) {
        val vm = buildViewModel()
        vm.onAction(Action.ShowAddTypeDialog)
        vm.onAction(Action.DismissAddTypeDialog)
        assertFalse(vm.state.value.showAddTypeDialog)
    }

    @Test
    fun `given StartEditingTaskType when invoked then editingTaskTypeId is set`() = runTest(testDispatcher) {
        val vm = buildViewModel()
        vm.onAction(Action.StartEditingTaskType(taskTypeId = 4L))
        assertEquals(4L, vm.state.value.editingTaskTypeId)
    }

    @Test
    fun `given DismissEditTaskType when invoked then editingTaskTypeId is cleared`() = runTest(testDispatcher) {
        val vm = buildViewModel()
        vm.onAction(Action.StartEditingTaskType(taskTypeId = 4L))
        vm.onAction(Action.DismissEditTaskType)
        assertNull(vm.state.value.editingTaskTypeId)
    }

    // ── Navigation ────────────────────────────────────────────────────────────

    @Test
    fun `given NavigateToEdit when invoked then screen becomes Edit`() = runTest(testDispatcher) {
        val vm = buildViewModel()
        vm.onAction(Action.NavigateToEdit)
        assertEquals(AppScreen.Edit, vm.state.value.screen)
    }

    @Test
    fun `given NavigateToRun when invoked then screen becomes Run and activeTaskId cleared`() = runTest(testDispatcher) {
        val vm = buildViewModel()
        vm.onAction(Action.NavigateToEdit)
        vm.onAction(Action.ToggleTaskActive(taskId = 9L))
        vm.onAction(Action.NavigateToRun)

        assertEquals(AppScreen.Run, vm.state.value.screen)
        assertNull(vm.state.value.activeTaskId)
    }

    // ── Async task actions ────────────────────────────────────────────────────

    @Test
    fun `given PlaceTask when invoked then repository addTask is called`() = runTest(testDispatcher) {
        val taskRepo = MockTaskRepository()
        val vm = buildViewModel(taskRepo = taskRepo)

        vm.onAction(Action.PlaceTask(taskTypeId = 2L, slotIndex = 7))

        assertEquals(1, taskRepo.addCalls.size)
        assertEquals(7, taskRepo.addCalls.first()["slotIndex"])
    }

    @Test
    fun `given MoveTask when invoked then repository updateTask is called and activeTaskId cleared`() = runTest(testDispatcher) {
        val taskRepo = MockTaskRepository()
        taskRepo.setTasks(task(id = 1L, slotIndex = 2))
        val vm = buildViewModel(taskRepo = taskRepo)
        vm.onAction(Action.ToggleTaskActive(taskId = 1L))

        vm.onAction(Action.MoveTask(taskId = 1L, newSlotIndex = 8))

        assertEquals(1, taskRepo.updateCalls.size)
        assertEquals(8, taskRepo.updateCalls.first().slotIndex)
        assertNull(vm.state.value.activeTaskId)
    }

    @Test
    fun `given RemoveTask when active task removed then activeTaskId is cleared`() = runTest(testDispatcher) {
        val taskRepo = MockTaskRepository()
        taskRepo.setTasks(task(id = 1L))
        val vm = buildViewModel(taskRepo = taskRepo)
        vm.onAction(Action.ToggleTaskActive(taskId = 1L))

        vm.onAction(Action.RemoveTask(taskId = 1L))

        assertEquals(listOf(1L), taskRepo.removeCalls)
        assertNull(vm.state.value.activeTaskId)
    }

    @Test
    fun `given RemoveTask when non-active task removed then activeTaskId unchanged`() = runTest(testDispatcher) {
        val taskRepo = MockTaskRepository()
        taskRepo.setTasks(task(id = 1L), task(id = 2L, slotIndex = 5))
        val vm = buildViewModel(taskRepo = taskRepo)
        vm.onAction(Action.ToggleTaskActive(taskId = 1L))

        vm.onAction(Action.RemoveTask(taskId = 2L))

        assertEquals(1L, vm.state.value.activeTaskId)
    }

    @Test
    fun `given ResizeTask when invoked then repository updateTask is called`() = runTest(testDispatcher) {
        val taskRepo = MockTaskRepository()
        taskRepo.setTasks(task(id = 1L, durationSlots = 1))
        val vm = buildViewModel(taskRepo = taskRepo)

        vm.onAction(Action.ResizeTask(taskId = 1L, newDurationSlots = 3))

        assertEquals(1, taskRepo.updateCalls.size)
        assertEquals(3, taskRepo.updateCalls.first().durationSlots)
    }

    @Test
    fun `given ResizeTaskTop when invoked then repository updateTask is called with adjusted slot`() = runTest(testDispatcher) {
        val taskRepo = MockTaskRepository()
        taskRepo.setTasks(task(id = 1L, slotIndex = 5, durationSlots = 3))
        val vm = buildViewModel(taskRepo = taskRepo)

        vm.onAction(Action.ResizeTaskTop(taskId = 1L, newSlotIndex = 3))

        assertEquals(1, taskRepo.updateCalls.size)
        assertEquals(3, taskRepo.updateCalls.first().slotIndex)
        assertEquals(5, taskRepo.updateCalls.first().durationSlots)
    }

    // ── Task type actions ──────────────────────────────────────────────────────

    @Test
    fun `given AddTaskType when invoked then repository addTaskType is called`() = runTest(testDispatcher) {
        val taskTypeRepo = MockTaskTypeRepository()
        val vm = buildViewModel(taskTypeRepo = taskTypeRepo)

        vm.onAction(Action.AddTaskType(name = "Break", colorRgb = 0x888888))

        assertEquals(1, taskTypeRepo.addCalls.size)
        assertEquals("Break", taskTypeRepo.addCalls.first().first)
    }

    @Test
    fun `given UpdateTaskType when invoked then repository updateTaskType is called`() = runTest(testDispatcher) {
        val taskTypeRepo = MockTaskTypeRepository()
        val vm = buildViewModel(taskTypeRepo = taskTypeRepo)

        vm.onAction(Action.UpdateTaskType(id = 2L, name = "Sit", colorRgb = 0x2196F3))

        assertEquals(Triple(2L, "Sit", 0x2196F3), taskTypeRepo.updateCalls.single())
    }

    // ── Schedule actions ───────────────────────────────────────────────────────

    @Test
    fun `given SetActiveSchedule when invoked then activeScheduleId is updated`() = runTest(testDispatcher) {
        val activeRepo = MockActiveScheduleRepository(initialId = 1L)
        val vm = buildViewModel(activeRepo = activeRepo)

        vm.onAction(Action.SetActiveSchedule(scheduleId = 3L))

        assertEquals(3L, vm.state.value.activeScheduleId)
    }

    @Test
    fun `given AddSchedule when invoked then schedule appears in state`() = runTest(testDispatcher) {
        val scheduleRepo = MockScheduleRepository()
        val vm = buildViewModel(scheduleRepo = scheduleRepo)

        vm.onAction(Action.AddSchedule(name = "Gym"))

        assertEquals(1, vm.state.value.schedules.size)
        assertEquals("Gym", vm.state.value.schedules.first().name)
    }

    @Test
    fun `given DeleteSchedule when non-active schedule deleted then schedules list shrinks`() = runTest(testDispatcher) {
        val scheduleRepo = MockScheduleRepository()
        scheduleRepo.setSchedules(schedule(1L, "Work"), schedule(2L, "Home"))
        val activeRepo = MockActiveScheduleRepository(initialId = 1L)
        val vm = buildViewModel(scheduleRepo = scheduleRepo, activeRepo = activeRepo)

        vm.onAction(Action.DeleteSchedule(scheduleId = 2L))

        assertEquals(1, vm.state.value.schedules.size)
    }

    // ── Reminders ─────────────────────────────────────────────────────────────

    @Test
    fun `given ToggleReminders when reminders off then reminders start and state updates`() = runTest(testDispatcher) {
        val reminderRepo = MockReminderRepository(initiallyRunning = false)
        val vm = buildViewModel(reminderRepo = reminderRepo)

        vm.onAction(Action.ToggleReminders)

        assertTrue(vm.state.value.remindersRunning)
        assertEquals(1, reminderRepo.toggleCount)
    }

    @Test
    fun `given ToggleReminders when reminders on then reminders stop and state updates`() = runTest(testDispatcher) {
        val reminderRepo = MockReminderRepository(initiallyRunning = true)
        val vm = buildViewModel(reminderRepo = reminderRepo)

        vm.onAction(Action.ToggleReminders)

        assertFalse(vm.state.value.remindersRunning)
    }

    // ── Reactive state via Turbine ────────────────────────────────────────────

    @Test
    fun `given active schedule changes when observed then placed tasks reload`() = runTest(testDispatcher) {
        val taskRepo = MockTaskRepository()
        val firstTasks = listOf(task(id = 1L, slotIndex = 2))
        taskRepo.setTasks(firstTasks)
        val activeRepo = MockActiveScheduleRepository(initialId = 1L)
        val vm = buildViewModel(taskRepo = taskRepo, activeRepo = activeRepo)

        vm.state.test {
            assertEquals(firstTasks, awaitItem().placedTasks)

            val nextTasks = listOf(task(id = 2L, slotIndex = 9))
            taskRepo.setTasks(nextTasks)

            assertEquals(nextTasks, awaitItem().placedTasks)
            cancelAndIgnoreRemainingEvents()
        }
    }
}
