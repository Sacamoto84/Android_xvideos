package com.client.xvideos.common.room.entity.r

import androidx.room.Entity
import androidx.room.PrimaryKey


//История в эдитке поиска Gifs и Niches


interface SearchHistory {
    val text: String
    val timeCreate: Long
}

/**
 * Таблица историей запросов в окне поиска Explorer
 */
@Entity(tableName = "r_search_history_explorer")
data class R_SearchHistoryExplorerEntity(
    @PrimaryKey
    override val text: String,
    override val timeCreate: Long = System.currentTimeMillis(),
) : SearchHistory

/**
 * Таблица историей запросов в окне поиска Niches
 */
@Entity(tableName = "r_search_history_niches")
data class R_SearchHistoryNichesEntity(
    @PrimaryKey
    override val text: String,
    override val timeCreate: Long = System.currentTimeMillis(),
) : SearchHistory



