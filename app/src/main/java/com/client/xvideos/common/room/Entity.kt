package com.client.xvideos.common.room

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.client.xvideos.redgifs.db.entity.getCurrentTimeText

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



//--- L ---

@Entity(tableName = "l_album_picture_cache")
data class L_AlbumPictureCacheEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0, //Номер альбома
    val content: String, //Json список PicsDetails
    val timeCreate: Long = System.currentTimeMillis(),
    val timeCreateText: String = getCurrentTimeText()// добавляем поле для времени в текстовом формате = getCurrentTimeText()
)



