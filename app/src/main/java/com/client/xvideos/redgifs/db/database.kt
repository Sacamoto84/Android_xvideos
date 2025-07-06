package com.client.xvideos.redgifs.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.client.xvideos.redgifs.db.converter.Converters
import com.client.xvideos.redgifs.db.dao.BlockDao
import com.client.xvideos.redgifs.db.dao.CacheMediaResponseDao
import com.client.xvideos.redgifs.db.dao.GifsInfoDao
import com.client.xvideos.redgifs.db.entity.BlockEntity
import com.client.xvideos.redgifs.db.entity.CacheMediaResponseEntity
import com.client.xvideos.redgifs.db.entity.GifsInfoEntity

@Database(
    entities = [CacheMediaResponseEntity::class, BlockEntity::class, GifsInfoEntity::class],
    version = 4,
    autoMigrations = [
        //AutoMigration(from = 4, to = 5)
    ],
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class AppRedGifsDatabase : RoomDatabase() {
    abstract fun cacheMediaResponseDao(): CacheMediaResponseDao
    abstract fun blockDao(): BlockDao
    abstract fun gifInfoDao(): GifsInfoDao
}
