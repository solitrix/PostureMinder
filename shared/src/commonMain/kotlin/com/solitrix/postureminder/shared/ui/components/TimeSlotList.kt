package com.solitrix.postureminder.shared.ui.components

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import com.solitrix.postureminder.shared.domain.model.SLOT_COUNT
import com.solitrix.postureminder.shared.domain.model.ScheduledTask
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
@Composable
fun TimeSlotList(
    placedTasks: List<ScheduledTask>,
    activeTaskId: Long?,
    onToggleActive: (Long) -> Unit,
    onRemove: (Long) -> Unit,
    onResized: (Long, Int) -> Unit,
    onResizedFromTop: (Long, Int) -> Unit,
    onDragEnded: (Offset, Long) -> Unit,
    onBoundsRegistered: (Int, Rect) -> Unit,
    nowIndicator: NowIndicator? = null,
    readOnly: Boolean = false,
    lazyListState: LazyListState = rememberLazyListState(),
    modifier: Modifier = Modifier
) {
    val tasksBySlot = placedTasks.associateBy { it.slotIndex }

    val coveredSlots = placedTasks.flatMap { task ->
        (1 until task.durationSlots).map { task.slotIndex + it }
    }.toSet()

    LazyColumn(state = lazyListState, modifier = modifier) {
        items(SLOT_COUNT) { index ->
            if (index in coveredSlots) return@items

            val durationSlots = tasksBySlot[index]?.durationSlots ?: 1
            val nowLineFraction = nowIndicator
                ?.takeIf { it.slotIndex in index until (index + durationSlots) }
                ?.let { (it.slotIndex - index + it.fraction) / durationSlots }

            TimeSlotItem(
                slotIndex = index,
                task = tasksBySlot[index],
                activeTaskId = activeTaskId,
                onToggleActive = onToggleActive,
                onRemove = onRemove,
                onResized = onResized,
                onResizedFromTop = onResizedFromTop,
                onDragEnded = onDragEnded,
                onBoundsRegistered = onBoundsRegistered,
                nowLineFraction = nowLineFraction,
                readOnly = readOnly
            )
        }
    }
}
