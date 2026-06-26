package com.solitrix.postureminder.shared.data.database

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import androidx.room.migration.Migration
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.execSQL
import com.solitrix.postureminder.shared.data.dao.SetDao
import com.solitrix.postureminder.shared.data.dao.TaskDao
import com.solitrix.postureminder.shared.data.dao.TaskTypeDao
import com.solitrix.postureminder.shared.data.entity.Set as PostureSet
import com.solitrix.postureminder.shared.data.entity.Task
import com.solitrix.postureminder.shared.data.entity.TaskType

@Database(entities = [Task::class, TaskType::class, PostureSet::class], version = 4)
@ConstructedBy(AppDatabaseConstructor::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun getDao(): TaskDao
    abstract fun getTaskTypeDao(): TaskTypeDao
    abstract fun getSetDao(): SetDao
}

// The Room compiler generates the `actual` implementations.
@Suppress("KotlinNoActualForExpect")
expect object AppDatabaseConstructor : RoomDatabaseConstructor<AppDatabase> {
    override fun initialize(): AppDatabase
}

