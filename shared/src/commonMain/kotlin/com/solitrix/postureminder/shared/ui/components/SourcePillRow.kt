package com.solitrix.postureminder.shared.ui.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.solitrix.postureminder.shared.domain.model.TaskTypeModel
import com.solitrix.postureminder.shared.ui.state.DragInfo
import com.solitrix.postureminder.shared.ui.state.LocalDragState
import com.solitrix.postureminder.shared.ui.theme.postureColorFromRgb
import com.solitrix.postureminder.shared.generated.resources.Res
import com.solitrix.postureminder.shared.generated.resources.*
import org.jetbrains.compose.resources.stringResource
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.uuid.ExperimentalUuidApi

@Composable
fun SourcePillRow(
    taskTypes: List<TaskTypeModel>,
    onDragEnded: (Offset, Long) -> Unit,
    onAddClick: () -> Unit,
    onLongPress: (TaskTypeModel) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier
                .weight(1f)
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            taskTypes.forEach { taskType ->
                SourcePill(
                    taskType = taskType,
                    onDragEnded = onDragEnded,
                    onLongPress = onLongPress,
                )
            }
        }

        IconButton(
            onClick = onAddClick,
            modifier = Modifier.padding(end = 8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = stringResource(Res.string.cd_add_task_type),
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@OptIn(ExperimentalUuidApi::class)
@Composable
fun SourcePill(
    taskType: TaskTypeModel,
    onDragEnded: (Offset, Long) -> Unit,
    onLongPress: (TaskTypeModel) -> Unit,
) {
    val dragState = LocalDragState.current
    var pillPositionInWindow by remember { mutableStateOf(Offset.Zero) }

    Surface(
        modifier = Modifier
            .width(100.dp)
            .height(PILL_HEIGHT_DP.dp)
            .onGloballyPositioned { coords ->
                pillPositionInWindow = coords.positionInWindow()
            }
            .pointerInput(taskType.id) {
                while (true) {
                    val down = awaitPointerEventScope {
                        while (true) {
                            val e = awaitPointerEvent()
                            val d = e.changes.firstOrNull { !it.previousPressed && it.pressed }
                            if (d != null) { d.consume(); return@awaitPointerEventScope d }
                        }
                        @Suppress("UNREACHABLE_CODE") error("unreachable")
                    }

                    val startPos = down.position
                    dragState.value = DragInfo(
                        typeName = taskType.name,
                        currentOffset = pillPositionInWindow + startPos,
                        anchorOffset = startPos,
                        typeColorRgb = taskType.colorRgb,
                        taskTypeId = taskType.id
                    )

                    var dragStarted = false
                    val completed = withTimeoutOrNull(viewConfiguration.longPressTimeoutMillis) {
                        awaitPointerEventScope {
                            while (true) {
                                val next = awaitPointerEvent()
                                val change = next.changes.firstOrNull { it.id == down.id }
                                if (change == null || !change.pressed) {
                                    return@awaitPointerEventScope
                                }
                                change.consume()
                                if ((change.position - startPos).getDistance() > viewConfiguration.touchSlop) {
                                    dragStarted = true
                                    dragState.value = dragState.value?.copy(
                                        currentOffset = pillPositionInWindow + change.position
                                    )
                                    return@awaitPointerEventScope
                                }
                            }
                        }
                    }

                    when {
                        completed == null -> {
                            dragState.value = null
                            onLongPress(taskType)
                            awaitPointerEventScope {
                                while (true) {
                                    val e = awaitPointerEvent()
                                    val c = e.changes.firstOrNull { it.id == down.id }
                                    c?.consume()
                                    if (c == null || !c.pressed) break
                                }
                            }
                        }
                        dragStarted -> {
                            awaitPointerEventScope {
                                while (true) {
                                    val next = awaitPointerEvent()
                                    val change = next.changes.firstOrNull { it.id == down.id }
                                    if (change == null || !change.pressed) {
                                        val finalOffset = dragState.value?.currentOffset
                                        if (finalOffset != null) onDragEnded(finalOffset, taskType.id)
                                        dragState.value = null
                                        break
                                    }
                                    change.consume()
                                    dragState.value = dragState.value?.copy(
                                        currentOffset = pillPositionInWindow + change.position
                                    )
                                }
                            }
                        }
                        else -> {
                            dragState.value = null
                        }
                    }
                }
            },
        shape = RoundedCornerShape(16.dp),
        color = postureColorFromRgb(taskType.colorRgb)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = taskType.name,
                color = Color.White,
                fontSize = 14.sp
            )
        }
    }
}
