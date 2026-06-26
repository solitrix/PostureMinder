package com.solitrix.postureminder.shared.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = TaskType::class,
            parentColumns = ["id"],
            childColumns = ["taskType"],
            onDelete = ForeignKey.RESTRICT
        ),
        ForeignKey(
            entity = Set::class,
            parentColumns = ["id"],
            childColumns = ["set"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("taskType"), Index("set")]
)
data class Task(
    val day: Int,
    val slotIndex: Int,
    val durationSlots: Int = 1,
    val taskType: Long = 0,
    val set: Long = 0,
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
)
