package com.solitrix.postureminder.shared.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.solitrix.postureminder.shared.domain.model.ScheduledTask
import com.solitrix.postureminder.shared.domain.model.ScheduleModel
import com.solitrix.postureminder.shared.domain.model.TaskTypeModel
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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// ── MVI contracts ─────────────────────────────────────────────────────────────

data class UiState(
    val screen: AppScreen = AppScreen.Run,
    val placedTasks: List<ScheduledTask> = emptyList(),
    val taskTypes: List<TaskTypeModel> = emptyList(),
    val schedules: List<ScheduleModel> = emptyList(),
    val activeScheduleId: Long = 0L,
    val selectedDay: Int = 0,
    val activeTaskId: Long? = null,
    val remindersRunning: Boolean = false,
    val showAddTypeDialog: Boolean = false,
    val editingTaskTypeId: Long? = null,
)

enum class AppScreen { Run, Edit }

sealed interface Action {
    data class SelectDay(val day: Int) : Action
    data class PlaceTask(val taskTypeId: Long, val slotIndex: Int) : Action
    data class MoveTask(val taskId: Long, val newSlotIndex: Int) : Action
    data class RemoveTask(val taskId: Long) : Action
    data class ResizeTask(val taskId: Long, val newDurationSlots: Int) : Action
    data class ResizeTaskTop(val taskId: Long, val newSlotIndex: Int) : Action
    data class ToggleTaskActive(val taskId: Long) : Action
    data class AddTaskType(val name: String, val colorRgb: Int) : Action
    data class UpdateTaskType(val id: Long, val name: String, val colorRgb: Int) : Action
    data class SetActiveSchedule(val scheduleId: Long) : Action
    data class AddSchedule(val name: String) : Action
    data class DeleteSchedule(val scheduleId: Long) : Action
    data class StartEditingTaskType(val taskTypeId: Long) : Action
    data object ShowAddTypeDialog : Action
    data object DismissAddTypeDialog : Action
    data object DismissEditTaskType : Action
    data object ToggleReminders : Action
    data object NavigateToEdit : Action
    data object NavigateToRun : Action
}

// ── ViewModel ─────────────────────────────────────────────────────────────────

class PostureViewModel(
    private val observeScheduledTasks: ObserveScheduledTasksUseCase,
    private val placeTask: PlaceTaskUseCase,
    private val moveTask: MoveTaskUseCase,
    private val removeTask: RemoveTaskUseCase,
    private val resizeTask: ResizeTaskUseCase,
    private val resizeTaskTop: ResizeTaskTopUseCase,
    private val observeTaskTypes: ObserveTaskTypesUseCase,
    private val addTaskType: AddTaskTypeUseCase,
    private val updateTaskType: UpdateTaskTypeUseCase,
    private val observeSchedules: ObserveSchedulesUseCase,
    private val addSchedule: AddScheduleUseCase,
    private val deleteSchedule: DeleteScheduleUseCase,
    private val observeActiveSchedule: ObserveActiveScheduleUseCase,
    private val setActiveSchedule: SetActiveScheduleUseCase,
    private val observeRemindersRunning: ObserveRemindersRunningUseCase,
    private val toggleReminders: ToggleRemindersUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(UiState())
    val state: StateFlow<UiState> = _state.asStateFlow()

    init {
        // Observe tasks reactively whenever the selected day or active schedule changes.
        combine(
            _state.map { it.selectedDay }.distinctUntilChanged(),
            observeActiveSchedule(),
        ) { day, scheduleId -> day to scheduleId }
            .flatMapLatest { (day, scheduleId) -> observeScheduledTasks(day, scheduleId) }
            .onEach { tasks -> _state.update { it.copy(placedTasks = tasks) } }
            .launchIn(viewModelScope)

        observeTaskTypes()
            .onEach { types -> _state.update { it.copy(taskTypes = types) } }
            .launchIn(viewModelScope)

        observeSchedules()
            .onEach { schedules -> _state.update { it.copy(schedules = schedules) } }
            .launchIn(viewModelScope)

        observeActiveSchedule()
            .onEach { id -> _state.update { it.copy(activeScheduleId = id) } }
            .launchIn(viewModelScope)

        observeRemindersRunning()
            .onEach { running -> _state.update { it.copy(remindersRunning = running) } }
            .launchIn(viewModelScope)
    }

    fun onAction(action: Action) {
        when (action) {
            is Action.SelectDay -> _state.update {
                it.copy(selectedDay = action.day.coerceIn(0, 4), activeTaskId = null)
            }
            is Action.PlaceTask -> viewModelScope.launch {
                val s = _state.value
                placeTask(action.taskTypeId, action.slotIndex, s.selectedDay, s.activeScheduleId, s.placedTasks)
            }
            is Action.MoveTask -> viewModelScope.launch {
                moveTask(action.taskId, action.newSlotIndex, _state.value.placedTasks)
                _state.update { it.copy(activeTaskId = null) }
            }
            is Action.RemoveTask -> viewModelScope.launch {
                removeTask(action.taskId)
                if (_state.value.activeTaskId == action.taskId) {
                    _state.update { it.copy(activeTaskId = null) }
                }
            }
            is Action.ResizeTask -> viewModelScope.launch {
                resizeTask(action.taskId, action.newDurationSlots, _state.value.placedTasks)
            }
            is Action.ResizeTaskTop -> viewModelScope.launch {
                resizeTaskTop(action.taskId, action.newSlotIndex, _state.value.placedTasks)
            }
            is Action.ToggleTaskActive -> _state.update {
                val newId = if (it.activeTaskId == action.taskId) null else action.taskId
                it.copy(activeTaskId = newId)
            }
            is Action.AddTaskType -> viewModelScope.launch { addTaskType(action.name, action.colorRgb) }
            is Action.UpdateTaskType -> viewModelScope.launch { updateTaskType(action.id, action.name, action.colorRgb) }
            is Action.SetActiveSchedule -> setActiveSchedule(action.scheduleId)
            is Action.AddSchedule -> viewModelScope.launch { addSchedule(action.name) }
            is Action.DeleteSchedule -> viewModelScope.launch { deleteSchedule(action.scheduleId) }
            is Action.ShowAddTypeDialog -> _state.update { it.copy(showAddTypeDialog = true) }
            is Action.DismissAddTypeDialog -> _state.update { it.copy(showAddTypeDialog = false) }
            is Action.StartEditingTaskType -> _state.update { it.copy(editingTaskTypeId = action.taskTypeId) }
            is Action.DismissEditTaskType -> _state.update { it.copy(editingTaskTypeId = null) }
            is Action.ToggleReminders -> toggleReminders()
            is Action.NavigateToEdit -> _state.update { it.copy(screen = AppScreen.Edit) }
            is Action.NavigateToRun -> _state.update { it.copy(screen = AppScreen.Run, activeTaskId = null) }
        }
    }
}
