package com.solitrix.postureminder.shared.data.database

import androidx.room.Room
import androidx.room.RoomDatabase

actual fun getDatabaseBuilder(): RoomDatabase.Builder<AppDatabase> =
    Room.databaseBuilder<AppDatabase>(
        name = System.getProperty("user.home") + "/PostureMinder/posture.db"
    )
