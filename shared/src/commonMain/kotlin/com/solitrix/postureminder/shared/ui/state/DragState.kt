package com.solitrix.postureminder.shared.ui.state

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.geometry.Offset
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
data class DragInfo(
    val typeName: String,
    val currentOffset: Offset,
    val anchorOffset: Offset = Offset.Zero,  // where within the pill the user pressed
    val existingTaskId: Long? = null,         // non-null when moving a placed task pill
    val typeColorRgb: Int? = null,            // stored entity colour; null → name-based fallback
    val taskTypeId: Long = 0                  // TaskType.id — needed when placing a new task pill
)

val LocalDragState = compositionLocalOf<MutableState<DragInfo?>> { error("LocalDragState not provided") }
