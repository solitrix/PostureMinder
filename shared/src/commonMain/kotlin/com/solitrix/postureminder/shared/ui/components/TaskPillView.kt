package com.solitrix.postureminder.shared.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DragHandle
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.layout
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.solitrix.postureminder.shared.domain.model.ScheduledTask
import com.solitrix.postureminder.shared.ui.state.DragInfo
import com.solitrix.postureminder.shared.ui.state.LocalDragState
import com.solitrix.postureminder.shared.ui.theme.postureColorFromRgb
import com.solitrix.postureminder.shared.generated.resources.Res
import com.solitrix.postureminder.shared.generated.resources.*
import org.jetbrains.compose.resources.stringResource
import kotlin.math.roundToInt
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

private enum class ResizeEdge { TOP, BOTTOM }

@OptIn(ExperimentalUuidApi::class)
@Composable
fun TaskView(
    item: ScheduledTask,
    isActive: Boolean,
    onToggleActive: (Long) -> Unit,
    onRemove: (Long) -> Unit,
    onResized: (Long, Int) -> Unit,
    onResizedFromTop: (Long, Int) -> Unit,
    onDragEnded: (Offset, Long) -> Unit,
    readOnly: Boolean = false,
    modifier: Modifier = Modifier
) {
    val effectiveActive = isActive && !readOnly

    val dragState = LocalDragState.current
    val isBeingDragged = dragState.value?.existingTaskId == item.id
    var pillPositionInWindow by remember { mutableStateOf(Offset.Zero) }

    val baseHeightDp by animateDpAsState(
        targetValue = (item.durationSlots * SLOT_HEIGHT_DP - (SLOT_HEIGHT_DP - PILL_HEIGHT_DP)).dp,
        animationSpec = tween(durationMillis = 120),
        label = "baseHeight"
    )

    val activeExpandFraction by animateFloatAsState(
        targetValue = if (effectiveActive) 1f else 0f,
        animationSpec = tween(durationMillis = 280),
        label = "activeExpand"
    )
    val layoutHeightDp = baseHeightDp + ACTIVE_EXTRA_HEIGHT_DP.dp * activeExpandFraction

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseProgressState = infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseProgress"
    )

    val density = LocalDensity.current
    val layoutHeightPx = with(density) { layoutHeightDp.toPx() }
    val layoutHeightPxState = rememberUpdatedState(layoutHeightPx)
    val pulseExtraHeightPx = with(density) { PULSE_EXTRA_HEIGHT_DP.dp.toPx() }

    var topResizeDragPx by remember { mutableFloatStateOf(0f) }
    var isResizing by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .layout { measurable, constraints ->
                val extraTopPx = (-topResizeDragPx).coerceAtLeast(0f)
                val totalHeightPx = layoutHeightPx + extraTopPx
                val placeable = measurable.measure(
                    constraints.copy(
                        minHeight = totalHeightPx.roundToInt(),
                        maxHeight = totalHeightPx.roundToInt()
                    )
                )
                layout(placeable.width, layoutHeightPx.roundToInt()) {
                    placeable.place(0, -extraTopPx.roundToInt())
                }
            }
            .onGloballyPositioned { coords ->
                pillPositionInWindow = coords.positionInWindow()
            }
            .then(
                if (readOnly) Modifier else Modifier.pointerInput(item.id, effectiveActive) {
                    if (effectiveActive) return@pointerInput
                    detectDragGestures(
                        onDragStart = { localOffset ->
                            dragState.value = DragInfo(
                                typeName = item.taskTypeName,
                                currentOffset = pillPositionInWindow + localOffset,
                                anchorOffset = localOffset,
                                existingTaskId = item.id,
                                typeColorRgb = item.taskTypeColorRgb,
                                taskTypeId = item.taskTypeId
                            )
                        },
                        onDrag = { change, _ ->
                            dragState.value = dragState.value?.copy(
                                currentOffset = pillPositionInWindow + change.position
                            )
                        },
                        onDragEnd = {
                            val finalOffset = dragState.value?.currentOffset ?: return@detectDragGestures
                            onDragEnded(finalOffset, item.taskTypeId)
                        },
                        onDragCancel = {
                            dragState.value = null
                        }
                    )
                }
            )
            .graphicsLayer(alpha = if (isBeingDragged) 0f else 1f)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    val lhPx = layoutHeightPxState.value
                    scaleY = if (effectiveActive && !isResizing && lhPx > 0f)
                        1f + pulseProgressState.value * pulseExtraHeightPx / lhPx
                    else 1f
                }
                .background(postureColorFromRgb(item.taskTypeColorRgb), RoundedCornerShape(16.dp))
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(16.dp))
                .then(if (readOnly) Modifier else Modifier.clickable { onToggleActive(item.id) }),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            if (effectiveActive) {
                ResizeHandle(
                    edge = ResizeEdge.TOP,
                    task = item,
                    onResized = onResizedFromTop,
                    onDragProgress = { topResizeDragPx = it },
                    onResizeActive = { isResizing = it }
                )
            } else {
                Box(modifier = Modifier.height(4.dp))
            }

            Text(
                text = item.taskTypeName,
                color = Color.White,
                fontSize = 14.sp
            )

            if (effectiveActive) {
                ResizeHandle(edge = ResizeEdge.BOTTOM, task = item, onResized = onResized, onResizeActive = { isResizing = it })
            } else {
                Box(modifier = Modifier.height(4.dp))
            }
        }

        if (effectiveActive) {
            IconButton(
                onClick = { onRemove(item.id) },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(2.dp)
                    .size(28.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = stringResource(Res.string.cd_remove_task),
                    tint = Color.White.copy(alpha = 0.9f),
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@OptIn(ExperimentalUuidApi::class)
@Composable
private fun ResizeHandle(
    edge: ResizeEdge,
    task: ScheduledTask,
    onResized: (Long, Int) -> Unit,
    onDragProgress: ((Float) -> Unit)? = null,
    onResizeActive: ((Boolean) -> Unit)? = null
) {
    val density = LocalDensity.current
    val slotHeightPx = with(density) { SLOT_HEIGHT_DP.dp.toPx() }

    val currentTask by rememberUpdatedState(task)

    var cumulativeDragPx by remember { mutableFloatStateOf(0f) }
    var originalSlotIndex by remember { mutableIntStateOf(task.slotIndex) }
    var originalDuration by remember { mutableIntStateOf(task.durationSlots) }

    Icon(
        imageVector = Icons.Default.DragHandle,
        contentDescription = if (edge == ResizeEdge.TOP) stringResource(Res.string.cd_resize_top) else stringResource(Res.string.cd_resize_bottom),
        tint = Color.White.copy(alpha = 0.8f),
        modifier = Modifier
            .pointerInput(task.id, edge) {
                detectDragGestures(
                    onDragStart = { _ ->
                        cumulativeDragPx = 0f
                        originalSlotIndex = currentTask.slotIndex
                        originalDuration = currentTask.durationSlots
                        onResizeActive?.invoke(true)
                    },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        cumulativeDragPx += dragAmount.y
                        when (edge) {
                            ResizeEdge.BOTTOM -> {
                                val slotsDelta = (cumulativeDragPx / slotHeightPx).roundToInt()
                                val value = (originalDuration + slotsDelta).coerceAtLeast(1)
                                onResized(currentTask.id, value)
                            }
                            ResizeEdge.TOP -> onDragProgress?.invoke(cumulativeDragPx)
                        }
                    },
                    onDragEnd = {
                        if (edge == ResizeEdge.TOP) {
                            onDragProgress?.invoke(0f)
                            val slotsDelta = (cumulativeDragPx / slotHeightPx).roundToInt()
                            val value = (originalSlotIndex + slotsDelta).coerceAtLeast(0)
                            onResized(currentTask.id, value)
                        }
                        cumulativeDragPx = 0f
                        onResizeActive?.invoke(false)
                    },
                    onDragCancel = {
                        if (edge == ResizeEdge.TOP) onDragProgress?.invoke(0f)
                        cumulativeDragPx = 0f
                        onResizeActive?.invoke(false)
                    }
                )
            }
    )
}
