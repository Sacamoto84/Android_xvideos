package com.client.redgifs.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.client.redgifs.common.search.SearchRedHistoryEntity
import com.client.redgifs.db.dao.BlockDao
import com.client.redgifs.db.dao.CacheMediaResponseDao
import com.client.redgifs.db.dao.GifsInfoDao
import com.client.redgifs.db.entity.CacheMediaResponseEntity
import com.google.common.base.Converter

@Database(
    entities = [CacheMediaResponseEntity::class, SearchRedHistoryEntity::class],
    version = 1,
    exportSchema = true
)
@TypeConverters(Converter::class)
abstract class AppRedGifsDatabase : RoomDatabase() {
    abstract fun cacheMedaResponseDao(): CacheMediaResponseDao
    abstract fun blockDao() : BlockDao
    abstract fun gifInfoDao(): GifsInfoDao
}
