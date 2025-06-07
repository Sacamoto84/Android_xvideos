package com.client.xvideos.feature.redgifs.db

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import java.util.Calendar

/**
 * Таблица с кешем строк ответов от сервера
 */
@Entity(tableName = "cache_media_response")
data class CacheMedaResponseEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val url: String,
    val content: String,
    val timeCreate: Long = System.currentTimeMillis()
)

@Dao
interface CacheMedaResponseDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(doc: CacheMedaResponseEntity)

    @Query("SELECT * FROM cache_media_response WHERE url = :url")
    suspend fun get(url: String): CacheMedaResponseEntity?

    @Query("DELETE FROM cache_media_response WHERE timeCreate < :time")
    suspend fun deleteOld(time: Long)

    // ✅ Удаление всего кеша
    @Query("DELETE FROM cache_media_response")
    suspend fun deleteAll()

}


fun getStartOfTodayMillis(): Long {
    val calendar = Calendar.getInstance()
    calendar.set(Calendar.HOUR_OF_DAY, 0)
    calendar.set(Calendar.MINUTE, 0)
    calendar.set(Calendar.SECOND, 0)
    calendar.set(Calendar.MILLISECOND, 0)
    return calendar.timeInMillis
}

/** Удалить все записи созданные в прошлых сутках */
suspend fun clearOldCache(cacheDao: CacheMedaResponseDao) {
    val todayStartMillis = getStartOfTodayMillis()
    cacheDao.deleteOld(todayStartMillis)
}