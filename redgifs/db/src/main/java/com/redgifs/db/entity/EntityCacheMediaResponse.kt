package com.redgifs.db.entity

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import com.redgifs.db.dao.getCurrentTimeText
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





