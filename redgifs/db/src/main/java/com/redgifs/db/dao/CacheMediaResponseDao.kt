package com.redgifs.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.redgifs.db.entity.CacheMediaResponseEntity
import com.redgifs.db.entity.getStartOfTodayMillis

@Dao
interface CacheMediaResponseDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(doc: CacheMediaResponseEntity)

    @Query("SELECT * FROM cache_media_response WHERE url = :url")
    suspend fun get(url: String): CacheMediaResponseEntity?

    @Query("DELETE FROM cache_media_response WHERE timeCreate < :time")
    suspend fun deleteOld(time: Long)

    // ✅ Удаление всего кеша
    @Query("DELETE FROM cache_media_response")
    suspend fun deleteAll()

}

/** Удалить все записи созданные в прошлых сутках */
suspend fun clearOldCache(cacheDao: CacheMediaResponseDao) {
    val todayStartMillis = getStartOfTodayMillis()
    cacheDao.deleteOld(todayStartMillis)
}