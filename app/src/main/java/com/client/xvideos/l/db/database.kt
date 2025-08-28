package com.client.xvideos.l.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [PostJsonEntity::class, PostJsonRamEntity::class, AlbumPictureCacheEntity::class, RepositoryCacheFullEntity::class, RepositoryCacheTempEntity::class],
    version = 5,
    autoMigrations = [
        //AutoMigration(from = 4, to = 5)
    ],
    exportSchema = true
)
abstract class AppLDatabase : RoomDatabase() {
    abstract fun postJsonDao(): PostJsonDao
    abstract fun postJsonRamDao(): PostJsonRamDao
    abstract fun albumPictureCacheDao(): AlbumPictureCacheDao
    abstract fun repositoryCacheFullDao(): RepositoryCacheFullDao
    abstract fun repositoryCacheTempDao(): RepositoryCacheTempDao
}
