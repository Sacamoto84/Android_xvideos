package com.client.xvideos.common.room.entity.r

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Таблица с кешем строк ответов от сервера
 */
@Entity(tableName = "search_red_history")
data class R_SearchHistoryEntity(
    @PrimaryKey
    val text: String,
    val timeCreate: Long = System.currentTimeMillis(),
)