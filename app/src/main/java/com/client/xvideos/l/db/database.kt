package com.client.xvideos.l.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [PostJsonEntity::class, PostJsonRamEntity::class],
    version = 2,
    autoMigrations = [
        //AutoMigration(from = 4, to = 5)
    ],
    exportSchema = true
)
abstract class AppLDatabase : RoomDatabase() {
    abstract fun postJsonDao(): PostJsonDao
    abstract fun postJsonRamDao(): PostJsonRamDao
}
