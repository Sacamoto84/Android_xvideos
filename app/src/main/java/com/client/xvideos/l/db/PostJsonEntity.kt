package com.client.xvideos.l.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.redgifs.db.entity.getCurrentTimeText


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