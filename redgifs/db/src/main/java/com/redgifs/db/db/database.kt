package com.redgifs.db.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.redgifs.db.db.converter.Converters
import com.redgifs.db.db.dao.BlockDao
import com.redgifs.db.db.dao.CacheMediaResponseDao
import com.redgifs.db.db.dao.GifsInfoDao
import com.redgifs.db.db.dao.SearchRedHistoryDao
import com.redgifs.db.db.entity.BlockEntity
import com.redgifs.db.db.entity.CacheMediaResponseEntity
import com.redgifs.db.db.entity.GifsInfoEntity
import com.redgifs.db.db.entity.SearchRedHistoryEntity

@Database(
    entities = [CacheMediaResponseEntity::class, BlockEntity::class, GifsInfoEntity::class, SearchRedHistoryEntity::class],
    version = 6,
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
    abstract fun searchHistoryDao(): SearchRedHistoryDao
}
