package com.client.xvideos.redgifs.common.search

import androidx.room.Query
import kotlinx.coroutines.flow.Flow

interface IDaoSearchTemplate{

    @Query("SELECT text FROM r_search_history_niches ORDER BY timeCreate DESC")
    fun observeAllTexts(): Flow<List<String>>

    suspend fun insertAndTrim( text: String , limit: Int = 10) //add(String)
    suspend fun deleteByTexts( text: String ) //delete(String)
    suspend fun deleteAll()                   //clear
}