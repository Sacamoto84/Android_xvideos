package com.redgifs.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.redgifs.db.entity.SearchRedHistoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SearchRedHistoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: SearchRedHistoryEntity)

    @Query("SELECT text FROM search_red_history ORDER BY timeCreate DESC")
    fun observeAllTexts(): Flow<List<String>>   // <‑‑ поток изменений

    @Query("DELETE FROM search_red_history")
    suspend fun deleteAll()


    @Transaction
    suspend fun insertAndTrim(item: SearchRedHistoryEntity, limit: Int = 10) {
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
        DELETE FROM search_red_history 
        WHERE rowid NOT IN (
            SELECT rowid 
            FROM search_red_history 
            ORDER BY timeCreate DESC 
            LIMIT :limit
        )
        """
    )
    suspend fun deleteOlderThanLimit(limit: Int)


    @Query("DELETE FROM search_red_history WHERE text = :text")
    suspend fun deleteByTexts(text: String)

}