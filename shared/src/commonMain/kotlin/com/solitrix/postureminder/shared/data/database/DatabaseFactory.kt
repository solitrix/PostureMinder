package com.solitrix.postureminder.shared.data.database

import androidx.room.RoomDatabase
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import androidx.sqlite.execSQL

expect fun getDatabaseBuilder(): RoomDatabase.Builder<AppDatabase>

fun createDatabase(): AppDatabase = getDatabaseBuilder()
    .setDriver(BundledSQLiteDriver())
    .addMigrations()
    .addCallback(object : RoomDatabase.Callback() {
        /** Called once when the database is first created (fresh install). */
        override fun onCreate(db: SQLiteConnection) {
            db.execSQL("INSERT INTO `TaskType` (`name`,`colorRgb`) VALUES ('Stand',   ${0x4CAF50})")
            db.execSQL("INSERT INTO `TaskType` (`name`,`colorRgb`) VALUES ('Sit',     ${0x2196F3})")
            db.execSQL("INSERT INTO `TaskType` (`name`,`colorRgb`) VALUES ('Recline', ${0xFF9800})")
            db.execSQL("INSERT INTO `TaskType` (`name`,`colorRgb`) VALUES ('Activity',${0x9C27B0})")
            db.execSQL("INSERT INTO `TaskType` (`name`,`colorRgb`) VALUES ('Break',   ${0xF44336})")
            db.execSQL("INSERT INTO `Set` (`name`) VALUES ('Default Work Posture Schedule')")
        }
    })
    .build()
