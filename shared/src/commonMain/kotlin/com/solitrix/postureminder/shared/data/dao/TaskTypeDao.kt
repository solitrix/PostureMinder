package com.solitrix.postureminder.shared.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.solitrix.postureminder.shared.data.entity.TaskType
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskTypeDao {
    @Query("SELECT * FROM TaskType ORDER BY id ASC")
    fun getAllAsFlow(): Flow<List<TaskType>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(type: TaskType)

    @Query("UPDATE TaskType SET name = :name, colorRgb = :colorRgb WHERE id = :id")
    suspend fun update(id: Long, name: String, colorRgb: Int)

    @Query("SELECT COUNT(*) FROM TaskType")
    suspend fun count(): Int
}
