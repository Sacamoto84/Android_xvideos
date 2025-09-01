package com.client.xvideos.l.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.client.xvideos.redgifs.db.entity.getCurrentTimeText




@Entity(tableName = "repository_cache_full")
data class RepositoryCacheFullEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val url: String,
    val content: String,
    val timeCreate: Long = System.currentTimeMillis(),
    val timeCreateText: String = getCurrentTimeText()// добавляем поле для времени в текстовом формате = getCurrentTimeText()
)

@Entity(tableName = "repository_cache_temp")
data class RepositoryCacheTempEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val url: String,
    val content: String,
    val timeCreate: Long = System.currentTimeMillis(),
    val timeCreateText: String = getCurrentTimeText()// добавляем поле для времени в текстовом формате = getCurrentTimeText()
)



/**
 * Таблица с кешем строк ответов от сервера
 */
@Entity(tableName = "cache_post_json")
data class PostJsonEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val url: String,
    val content: String,
    val timeCreate: Long = System.currentTimeMillis(),
    val timeCreateText: String = getCurrentTimeText()// добавляем поле для времени в текстовом формате = getCurrentTimeText()
)

@Entity(tableName = "cache_post_json_ram")
data class PostJsonRamEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val url: String,
    val content: String,
    val timeCreate: Long = System.currentTimeMillis(),
    val timeCreateText: String = getCurrentTimeText()// добавляем поле для времени в текстовом формате = getCurrentTimeText()
)

@Entity(tableName = "album_picture_cache")
data class AlbumPictureCacheEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0, //Номер альбома
    val content: String, //Json список PicsDetails
    val timeCreate: Long = System.currentTimeMillis(),
    val timeCreateText: String = getCurrentTimeText()// добавляем поле для времени в текстовом формате = getCurrentTimeText()
)