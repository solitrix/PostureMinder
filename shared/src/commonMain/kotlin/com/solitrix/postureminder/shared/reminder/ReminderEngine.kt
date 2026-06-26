package com.solitrix.postureminder.shared.reminder

import com.solitrix.postureminder.shared.domain.repository.ActiveScheduleRepository
import com.solitrix.postureminder.shared.domain.repository.TaskRepository
import com.solitrix.postureminder.shared.notify
import com.solitrix.postureminder.shared.util.dayIndexAt
import com.solitrix.postureminder.shared.util.slotIndexAt
import com.solitrix.postureminder.shared.util.slotStartIndexAt
import com.solitrix.postureminder.shared.util.slotIndexToLabel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock

class ReminderEngine(
    private val taskRepository: TaskRepository,
    private val activeScheduleRepository: ActiveScheduleRepository,
    private val timeZone: TimeZone = TimeZone.currentSystemDefault(),
) {
    private var job: Job? = null

    private fun tickerFlow() = flow {
        while (true) {
            emit(Clock.System.now())
            delay(1_000L)
        }
    }

    fun start(scope: CoroutineScope) {
        if (job?.isActive == true) return
        job = tickerFlow()
            .map { instant ->
                val dateTime = instant.toLocalDateTime(timeZone)
                SlotTransition(
                    dayIndex = dayIndexAt(dateTime.dayOfWeek),
                    slotIndex = slotIndexAt(dateTime.time),
                )
            }
            .distinctUntilChanged()
            .onEach { transition -> checkAndNotify(transition) }
            .launchIn(scope)
    }

    fun stop() {
        job?.cancel()
        job = null
    }

    suspend fun checkNow() {
        val now = Clock.System.now().toLocalDateTime(timeZone)
        val slot = slotStartIndexAt(now.time) ?: return
        checkAndNotify(SlotTransition(dayIndex = dayIndexAt(now.dayOfWeek), slotIndex = slot))
    }

    private suspend fun checkAndNotify(transition: SlotTransition) {
        try {
            val day = transition.dayIndex ?: return
            val slot = transition.slotIndex ?: return
            val tasks = taskRepository.getTasksByDayAndSchedule(day, activeScheduleRepository.activeScheduleId.value).first()
            val due = findDueTask(transition, tasks) ?: return
            notify("${due.taskTypeName} time — ${slotIndexToLabel(slot)}")
        } catch (_: Exception) {
            // Best-effort: never let a transient DB/notify error kill the ticker.
        }
    }
}
