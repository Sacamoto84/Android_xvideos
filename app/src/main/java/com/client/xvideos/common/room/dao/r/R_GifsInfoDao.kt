package com.client.xvideos.common.room.dao.r

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.client.xvideos.common.room.entity.r.R_GifsInfoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface R_GifsInfoDao {

    /** Заменяем по первичному ключу, если запись уже есть */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: R_GifsInfoEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<R_GifsInfoEntity>)

    @Update
    suspend fun update(item: R_GifsInfoEntity)

    /* ---------- удаление ---------- */

    @Delete
    suspend fun delete(item: R_GifsInfoEntity)

    @Query("DELETE FROM gifs_info WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("DELETE FROM gifs_info")
    suspend fun clear()                       // подчистить кэш

    /* ---------- выборка одной записи ---------- */

    @Query("SELECT * FROM gifs_info WHERE id = :id LIMIT 1")
    suspend fun getById(id: String): R_GifsInfoEntity?

    /* ---------- стрим списка ---------- */

    /** Все GIF’ы (Flow для реактивного UI) */
    @Query("SELECT * FROM gifs_info ORDER BY createDate DESC")
    fun observeAll(): Flow<List<R_GifsInfoEntity>>

}