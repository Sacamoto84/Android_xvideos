package com.client.xvideos.redgifs.common.search

import kotlinx.coroutines.flow.Flow

interface IDaoSearchTemplate{

    fun observeAllTexts(): Flow<List<String>>
    suspend fun insertAndTrim( text: String , limit: Int = 10) //add(String)
    suspend fun deleteByTexts( text: String ) //delete(String)
    suspend fun deleteAll()                   //clear
}