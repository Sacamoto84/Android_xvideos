package com.client.xvideos.redgifs.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.client.xvideos.redgifs.db.entity.BlockEntity
import com.client.xvideos.redgifs.db.entity.BlockWithGif
import kotlinx.coroutines.flow.Flow

@Dao
interface BlockDao {

    /* Cписок всех блоков с их GIF‑ами (gif может быть null) */
    @Transaction
    @Query("SELECT * FROM block")
    fun observeAllBlocks(): Flow<List<BlockWithGif>>

    /* Только те, у кого GIF есть на месте */
    @Transaction
    @Query("""
        SELECT * FROM block
        WHERE gifId IS NOT NULL
    """)
    fun observeBlocksWithGif(): Flow<List<BlockWithGif>>

    /* Блок по id  (gif может быть null) */
    @Transaction
    @Query("SELECT * FROM block WHERE id = :id")
    suspend fun getBlock(id: String): BlockWithGif?

    /* Вставка / обновление */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBlock(block: BlockEntity)

}