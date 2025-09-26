package com.client.xvideos.common.room.dao.l

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.client.xvideos.common.room.entity.l.L_AlbumPictureCacheEntity

@Dao
interface L_AlbumPictureCacheDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(doc: L_AlbumPictureCacheEntity)

    @Query("SELECT * FROM l_album_picture_cache WHERE id = :id")
    suspend fun get(id: Long): L_AlbumPictureCacheEntity?

    // ✅ Удаление всего кеша
    @Query("DELETE FROM l_album_picture_cache")
    suspend fun deleteAll()

}