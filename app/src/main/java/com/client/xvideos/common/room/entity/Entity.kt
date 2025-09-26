package com.client.xvideos.common.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.client.xvideos.common.room.entity.r.getCurrentTimeText

/**
 * ## Сохранение данных только пока работает программа, удаление при следующем запуске
 */
@Entity(tableName = "cache_url_string_ram")
data class CacheUrlStringRamEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val url: String,
    val content: String,
    val timeCreate: Long = System.currentTimeMillis(),
    val timeCreateText: String = getCurrentTimeText()// добавляем поле для времени в текстовом формате = getCurrentTimeText()
)


@Entity(tableName = "cache_url_string_rom")
data class CacheUrlStringRomEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val url: String,
    val content: String,
    val timeCreate: Long = System.currentTimeMillis(),
    val timeCreateText: String = getCurrentTimeText()// добавляем поле для времени в текстовом формате = getCurrentTimeText()
)
