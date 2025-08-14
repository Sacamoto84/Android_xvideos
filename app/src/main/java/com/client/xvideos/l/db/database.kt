package com.client.xvideos.l.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [PostJsonEntity::class],
    version = 1,
    autoMigrations = [
        //AutoMigration(from = 4, to = 5)
    ],
    exportSchema = true
)
abstract class AppLDatabase : RoomDatabase() {
    abstract fun postJsonDao(): PostJsonDao
}
