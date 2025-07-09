package com.redgifs.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Таблица с кешем строк ответов от сервера
 */
@Entity(tableName = "search_red_history")
data class SearchRedHistoryEntity(
    @PrimaryKey
    val text: String,
    val timeCreate: Long = System.currentTimeMillis(),
)
