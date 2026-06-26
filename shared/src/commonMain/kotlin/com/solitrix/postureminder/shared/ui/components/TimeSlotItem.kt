package com.solitrix.postureminder.shared.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.solitrix.postureminder.shared.domain.model.ScheduledTask
import com.solitrix.postureminder.shared.util.slotIndexToLabel
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
@Composable
fun TimeSlotItem(
    slotIndex: Int,
    task: ScheduledTask?,
    activeTaskId: Long?,
    onToggleActive: (Long) -> Unit,
    onRemove: (Long) -> Unit,
    onResized: (Long, Int) -> Unit,
    onResizedFromTop: (Long, Int) -> Unit,
    onDragEnded: (Offset, Long) -> Unit,
    onBoundsRegistered: (Int, Rect) -> Unit,
    nowLineFraction: Float? = null,
    readOnly: Boolean = false,
    modifier: Modifier = Modifier
) {
    val minSlotHeightDp = ((task?.durationSlots ?: 1) * SLOT_HEIGHT_DP).dp

    Box(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = minSlotHeightDp)
            .onGloballyPositioned { onBoundsRegistered(slotIndex, it.boundsInWindow()) }
    ) {
        HorizontalDivider(
            modifier = Modifier.fillMaxWidth().align(Alignment.TopCenter),
            color = MaterialTheme.colorScheme.outlineVariant,
            thickness = 0.5.dp
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .width(72.dp)
                    .height(SLOT_HEIGHT_DP.dp)
                    .padding(start = 8.dp, top = 2.dp),
                contentAlignment = Alignment.TopStart
            ) {
                Text(
                    text = slotIndexToLabel(slotIndex),
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp, vertical = 0.dp),
                contentAlignment = Alignment.Center
            ) {
                if (task != null) {
                    TaskView(
                        item = task,
                        isActive = task.id == activeTaskId,
                        onToggleActive = onToggleActive,
                        onRemove = onRemove,
                        onResized = onResized,
                        onResizedFromTop = onResizedFromTop,
                        onDragEnded = onDragEnded,
                        readOnly = readOnly,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        if (nowLineFraction != null) {
            HorizontalDivider(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter)
                    .offset(y = minSlotHeightDp * nowLineFraction)
                    .zIndex(1f),
                color = Color.Red,
                thickness = 2.dp
            )
        }
    }
}
