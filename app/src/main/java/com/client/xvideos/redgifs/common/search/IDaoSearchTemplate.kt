package com.client.xvideos.redgifs.common.search

import kotlinx.coroutines.flow.Flow

interface IDaoSearchTemplate {

    fun observeAllTexts(): Flow<List<String>>

    suspend fun insertAndTrim(text: String, limit: Int = 10)
    suspend fun deleteByTexts(text: String)
    suspend fun deleteAll()
}
