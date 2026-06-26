package com.solitrix.postureminder.shared.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Set(
    val name: String,
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
)
