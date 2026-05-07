package com.client.xvideos.common.room.dao.r

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.client.xvideos.common.room.entity.r.R_SearchHistoryExplorerEntity
import com.client.xvideos.r.common.search.IDaoSearchTemplate
import kotlinx.coroutines.flow.Flow

@Dao
interface R_SearchHistoryExplorerDao : IDaoSearchTemplate  {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: R_SearchHistoryExplorerEntity)

    @Query("SELECT text FROM r_search_history_explorer ORDER BY timeCreate DESC")
    override fun observeAllTexts(): Flow<List<String>>

    @Query("DELETE FROM r_search_history_explorer")
    override suspend fun deleteAll()

    @Transaction
    override suspend fun insertAndTrim(text: String, limit: Int) {
        insert(R_SearchHistoryExplorerEntity(text))
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
        DELETE FROM r_search_history_explorer 
        WHERE rowid NOT IN (
            SELECT rowid 
            FROM r_search_history_explorer 
            ORDER BY timeCreate DESC 
            LIMIT :limit
        )
        """
    )
    suspend fun deleteOlderThanLimit(limit: Int)

    @Query("DELETE FROM r_search_history_explorer WHERE text = :text")
    override suspend fun deleteByTexts(text: String)

}
