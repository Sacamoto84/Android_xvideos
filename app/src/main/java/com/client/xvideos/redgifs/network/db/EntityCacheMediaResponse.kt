package com.client.xvideos.redgifs.network.db

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

/**
 * Таблица с кешем строк ответов от сервера
 */
@Entity(tableName = "cache_media_response")
data class CacheMediaResponseEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val url: String,
    val content: String,
    val timeCreate: Long = System.currentTimeMillis(),
    val timeCreateText: String = getCurrentTimeText()// добавляем поле для времени в текстовом формате = getCurrentTimeText()
)

@Dao
interface CacheMedaResponseDao {
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

fun getCurrentTimeText(): String {
    val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US)
    sdf.timeZone = TimeZone.getTimeZone("UTC")
    return sdf.format(Date())
}

