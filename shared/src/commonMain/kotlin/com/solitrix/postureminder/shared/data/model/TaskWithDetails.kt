package com.solitrix.postureminder.shared.data.model

import androidx.room.Embedded
import androidx.room.Relation
import com.solitrix.postureminder.shared.data.entity.Set as PostureSet
import com.solitrix.postureminder.shared.data.entity.Task
import com.solitrix.postureminder.shared.data.entity.TaskType

/**
 * Room multimap result that joins a Task with its related TaskType and Set.
 * Room generates two additional queries (one per @Relation) inside a single
 * @Transaction so all three rows are always consistent.
 */
data class TaskWithDetails(
    @Embedded val task: Task,
    @Relation(parentColumn = "taskType", entityColumn = "id")
    val taskType: TaskType,
    @Relation(parentColumn = "set", entityColumn = "id")
    val set: PostureSet,
)
