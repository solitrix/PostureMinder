package com.solitrix.postureminder.shared.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.solitrix.postureminder.shared.data.entity.Set as PostureSet
import kotlinx.coroutines.flow.Flow

@Dao
interface SetDao {
    @Query("SELECT * FROM `Set` ORDER BY id ASC")
    fun getAllAsFlow(): Flow<List<PostureSet>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(set: PostureSet): Long

    @Delete
    suspend fun delete(set: PostureSet)

    @Query("SELECT COUNT(*) FROM `Set`")
    suspend fun count(): Int
}
