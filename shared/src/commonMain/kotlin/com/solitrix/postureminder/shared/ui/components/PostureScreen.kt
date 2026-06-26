package com.solitrix.postureminder.shared.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.solitrix.postureminder.shared.notify
import com.solitrix.postureminder.shared.ui.state.DragInfo
import com.solitrix.postureminder.shared.ui.state.LocalDragState
import androidx.compose.runtime.CompositionLocalProvider
import com.solitrix.postureminder.shared.ui.viewmodel.Action
import com.solitrix.postureminder.shared.ui.viewmodel.UiState
import com.solitrix.postureminder.shared.generated.resources.Res
import com.solitrix.postureminder.shared.generated.resources.*
import org.jetbrains.compose.resources.stringResource
import com.solitrix.postureminder.shared.util.dayIndexAt
import com.solitrix.postureminder.shared.util.slotIndexAt
import kotlinx.coroutines.delay
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.uuid.ExperimentalUuidApi

enum class PostureScreenMode { EDIT, RUN }

data class NowIndicator(val slotIndex: Int, val fraction: Float)

@OptIn(ExperimentalUuidApi::class)
@Composable
fun PostureScreen(
    state: UiState,
    onAction: (Action) -> Unit,
    mode: PostureScreenMode = PostureScreenMode.EDIT,
    modifier: Modifier = Modifier
) {
    val dragState = remember { mutableStateOf<DragInfo?>(null) }
    val slotBoundsMap = remember { mutableStateMapOf<Int, Rect>() }
    val listState = rememberLazyListState()
    val nowIndicator = rememberNowIndicator(mode, state.selectedDay)

    val density = LocalDensity.current
    val slotHeightPx = with(density) { SLOT_HEIGHT_DP.dp.toPx() }
    var listHeightPx by remember { mutableStateOf(0) }
    val latestIndicator by rememberUpdatedState(nowIndicator)
    val latestSelectedDay by rememberUpdatedState(state.selectedDay)
    val latestOnAction by rememberUpdatedState(onAction)

    fun centerOffset(indicator: NowIndicator): Int =
        (indicator.fraction * slotHeightPx - listHeightPx / 2f).toInt()

    LaunchedEffect(mode, nowIndicator, listHeightPx) {
        if (mode != PostureScreenMode.RUN || listHeightPx == 0) return@LaunchedEffect
        val indicator = nowIndicator ?: return@LaunchedEffect
        if (!listState.isScrollInProgress) {
            listState.scrollToItem(indicator.slotIndex, centerOffset(indicator))
        }
    }

    LaunchedEffect(mode, listState) {
        if (mode != PostureScreenMode.RUN) return@LaunchedEffect
        var isSnapping = false
        var lastSnapMs = 0L
        snapshotFlow { listState.isScrollInProgress }.collect { inProgress ->
            val elapsedSinceSnap = Clock.System.now().toEpochMilliseconds() - lastSnapMs
            if (!inProgress && !isSnapping && elapsedSinceSnap > 800L) {
                isSnapping = true
                delay(200)
                val indicator = latestIndicator
                if (indicator != null && listHeightPx > 0 && !listState.isScrollInProgress) {
                    listState.animateScrollToItem(indicator.slotIndex, centerOffset(indicator))
                    lastSnapMs = Clock.System.now().toEpochMilliseconds()
                }
                isSnapping = false
            }
        }
    }

    LaunchedEffect(mode) {
        if (mode != PostureScreenMode.RUN) return@LaunchedEffect
        while (true) {
            val today = dayIndexAt(Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).dayOfWeek)
            if (today != null && today != latestSelectedDay) {
                latestOnAction(Action.SelectDay(today))
            }
            delay(60_000L)
        }
    }

    fun onDragEnded(finalOffset: Offset, taskTypeId: Long) {
        val visibleSlots = listState.layoutInfo.visibleItemsInfo.map { it.index }.toSet()
        val target = slotBoundsMap.entries
            .firstOrNull { (index, rect) -> index in visibleSlots && rect.contains(finalOffset) }
            ?.key
        val existingId = dragState.value?.existingTaskId
        if (target != null) {
            if (existingId != null) onAction(Action.MoveTask(existingId, target))
            else onAction(Action.PlaceTask(taskTypeId, target))
        }
        dragState.value = null
    }

    CompositionLocalProvider(LocalDragState provides dragState) {
        Surface(
            modifier = modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                Column(modifier = Modifier.fillMaxSize()) {
                    if (mode == PostureScreenMode.EDIT) {
                        SourcePillRow(
                            taskTypes = state.taskTypes,
                            onDragEnded = ::onDragEnded,
                            onAddClick = { onAction(Action.ShowAddTypeDialog) },
                            onLongPress = { onAction(Action.StartEditingTaskType(it.id)) },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    if (mode == PostureScreenMode.EDIT) {
                        DayTabRow(
                            selectedDay = state.selectedDay,
                            onDaySelected = { onAction(Action.SelectDay(it)) }
                        )
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .onSizeChanged { listHeightPx = it.height }
                    ) {
                        TimeSlotList(
                            placedTasks = state.placedTasks,
                            activeTaskId = state.activeTaskId,
                            onToggleActive = { id -> onAction(Action.ToggleTaskActive(id)) },
                            onRemove = { id -> onAction(Action.RemoveTask(id)) },
                            onResized = { id, slots -> onAction(Action.ResizeTask(id, slots)) },
                            onResizedFromTop = { id, newSlot -> onAction(Action.ResizeTaskTop(id, newSlot)) },
                            onDragEnded = ::onDragEnded,
                            onBoundsRegistered = { index, rect -> slotBoundsMap[index] = rect },
                            nowIndicator = nowIndicator,
                            readOnly = mode == PostureScreenMode.RUN,
                            lazyListState = listState,
                            modifier = Modifier.fillMaxSize()
                        )

                        if (mode == PostureScreenMode.RUN) {
                            val fadeColor = MaterialTheme.colorScheme.background
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(80.dp)
                                    .align(Alignment.TopCenter)
                                    .background(
                                        Brush.verticalGradient(listOf(fadeColor, Color.Transparent))
                                    )
                            )
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(80.dp)
                                    .align(Alignment.BottomCenter)
                                    .background(
                                        Brush.verticalGradient(listOf(Color.Transparent, fadeColor))
                                    )
                            )
                        }
                    }

                    if (mode == PostureScreenMode.EDIT) {
                        Button(onClick = { notify("Test Notification") }) {
                            Text(stringResource(Res.string.btn_test_notify))
                        }
                    }
                }

                if (mode == PostureScreenMode.EDIT) {
                    Box(modifier = Modifier.fillMaxSize().zIndex(10f)) {
                        DragOverlay()
                    }
                }
            }
        }
    }

    if (state.showAddTypeDialog) {
        AddTaskTypeDialog(
            onSave = { name, colorRgb -> onAction(Action.AddTaskType(name, colorRgb)) },
            onDismiss = { onAction(Action.DismissAddTypeDialog) }
        )
    }

    val editingTaskType = state.taskTypes.find { it.id == state.editingTaskTypeId }
    editingTaskType?.let { tt ->
        AddTaskTypeDialog(
            title = stringResource(Res.string.edit_task_type_title),
            initialName = tt.name,
            initialColorRgb = tt.colorRgb,
            onSave = { name, colorRgb -> onAction(Action.UpdateTaskType(tt.id, name, colorRgb)) },
            onDismiss = { onAction(Action.DismissEditTaskType) }
        )
    }
}

@Composable
private fun rememberNowIndicator(mode: PostureScreenMode, selectedDay: Int): NowIndicator? {
    var indicator by remember { mutableStateOf<NowIndicator?>(null) }

    LaunchedEffect(mode, selectedDay) {
        if (mode != PostureScreenMode.RUN) {
            indicator = null
            return@LaunchedEffect
        }
        while (true) {
            indicator = currentNowIndicator(selectedDay)
            delay(60_000L)
        }
    }

    return indicator
}

private fun currentNowIndicator(selectedDay: Int): NowIndicator? {
    val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
    if (dayIndexAt(now.dayOfWeek) != selectedDay) return null
    val slot = slotIndexAt(now.time) ?: return null
    val minutesSinceStart = now.time.hour * 60 + now.time.minute - 8 * 60
    return NowIndicator(slotIndex = slot, fraction = (minutesSinceStart % 30) / 30f)
}
