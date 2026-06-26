package com.solitrix.postureminder.shared.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.solitrix.postureminder.shared.data.entity.Task
import com.solitrix.postureminder.shared.data.model.TaskWithDetails
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {
    @Insert
    suspend fun insert(item: Task)

    @Update
    suspend fun update(item: Task)

    @Delete
    suspend fun delete(item: Task)

    @Transaction
    @Query("SELECT * FROM Task WHERE day = :day AND `set` = :setId")
    fun getByDayAndSetWithDetailsAsFlow(day: Int, setId: Long): Flow<List<TaskWithDetails>>

    @Query("DELETE FROM Task WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("SELECT count(*) FROM Task")
    suspend fun count(): Int
}
