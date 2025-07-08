package com.client.redgifs.db.dao

import androidx.room.*
import com.client.redgifs.db.entity.GifsInfoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GifsInfoDao {

    /** Заменяем по первичному ключу, если запись уже есть */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: GifsInfoEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<GifsInfoEntity>)

    @Update
    suspend fun update(item: GifsInfoEntity)

    /* ---------- удаление ---------- */

    @Delete
    suspend fun delete(item: GifsInfoEntity)

    @Query("DELETE FROM gifs_info WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("DELETE FROM gifs_info")
    suspend fun clear()                       // подчистить кэш

    /* ---------- выборка одной записи ---------- */

    @Query("SELECT * FROM gifs_info WHERE id = :id LIMIT 1")
    suspend fun getById(id: String): GifsInfoEntity?

    /* ---------- стрим списка ---------- */

    /** Все GIF’ы (Flow для реактивного UI) */
    @Query("SELECT * FROM gifs_info ORDER BY createDate DESC")
    fun observeAll(): Flow<List<GifsInfoEntity>>

}