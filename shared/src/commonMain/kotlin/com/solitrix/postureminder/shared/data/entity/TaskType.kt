package com.solitrix.postureminder.shared.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "TaskType")
data class TaskType(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val colorRgb: Int = 0x4CAF50,  // stored as 0xRRGGBB; alpha is always 0xFF at render time
)
