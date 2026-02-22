package com.client.xvideos.common.room.dao.r

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.client.xvideos.common.room.entity.r.R_SearchHistoryNichesEntity
import com.client.xvideos.redgifs.common.search.IDaoSearchTemplate
import kotlinx.coroutines.flow.Flow

@Dao
interface R_SearchHistoryNichesDao : IDaoSearchTemplate  {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: R_SearchHistoryNichesEntity)

    @Query("SELECT text FROM r_search_history_niches ORDER BY timeCreate DESC")
    override fun observeAllTexts(): Flow<List<String>>   // <‑‑ поток изменений

    @Query("DELETE FROM r_search_history_niches")
    override suspend fun deleteAll()


    @Transaction
    suspend fun insertAndTrim(item: R_SearchHistoryNichesEntity, limit: Int = 10) {
        insert(item)
        deleteOlderThanLimit(limit)
    }


    /**
     * Удаляем всё, что не входит в последние [limit] строк,
     * отсортированные по времени создания (DESC).
     *
     * `rowid` уникален, поэтому подходит для подзапроса.
     */
    @Query(
        """
        DELETE FROM r_search_history_niches 
        WHERE rowid NOT IN (
            SELECT rowid 
            FROM r_search_history_niches 
            ORDER BY timeCreate DESC 
            LIMIT :limit
        )
        """
    )
    suspend fun deleteOlderThanLimit(limit: Int)


    @Query("DELETE FROM r_search_history_niches WHERE text = :text")
    override suspend fun deleteByTexts(text: String)

}