package com.client.xvideos.common.room.dao.r

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.client.xvideos.common.room.entity.r.R_CacheMediaResponseEntity
import com.client.xvideos.common.room.entity.r.getStartOfTodayMillis

@Dao
interface R_CacheMediaResponseDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(doc: R_CacheMediaResponseEntity)

    @Query("SELECT * FROM cache_media_response WHERE url = :url")
    suspend fun get(url: String): R_CacheMediaResponseEntity?

    @Query("DELETE FROM cache_media_response WHERE timeCreate < :time")
    suspend fun deleteOld(time: Long)

    // ✅ Удаление всего кеша
    @Query("DELETE FROM cache_media_response")
    suspend fun deleteAll()

}

/** Удалить все записи созданные в прошлых сутках */
suspend fun clearOldCache(cacheDao: R_CacheMediaResponseDao) {
    val todayStartMillis = getStartOfTodayMillis()
    cacheDao.deleteOld(todayStartMillis)
}