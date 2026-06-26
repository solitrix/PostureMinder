package com.solitrix.postureminder.shared.data.database

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase

lateinit var appContext: Context

actual fun getDatabaseBuilder(): RoomDatabase.Builder<AppDatabase> =
    Room.databaseBuilder<AppDatabase>(
        context = appContext,
        name = appContext.getDatabasePath("posture.db").absolutePath
    )
