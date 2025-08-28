package com.client.xvideos.l.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface PostJsonDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(doc: PostJsonEntity)

    @Query("SELECT * FROM cache_post_json WHERE url = :url")
    suspend fun get(url: String): PostJsonEntity?

    @Query("DELETE FROM cache_post_json WHERE timeCreate < :time")
    suspend fun deleteOld(time: Long)

    // ✅ Удаление всего кеша
    @Query("DELETE FROM cache_post_json")
    suspend fun deleteAll()

}

@Dao
interface PostJsonRamDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(doc: PostJsonRamEntity)

    @Query("SELECT * FROM cache_post_json_ram WHERE url = :url")
    suspend fun get(url: String): PostJsonRamEntity?

    // ✅ Удаление всего кеша
    @Query("DELETE FROM cache_post_json_ram")
    suspend fun deleteAll()

}

@Dao
interface AlbumPictureCacheDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(doc: AlbumPictureCacheEntity)

    @Query("SELECT * FROM album_picture_cache WHERE id = :id")
    suspend fun get(id: Long): AlbumPictureCacheEntity?

    // ✅ Удаление всего кеша
    @Query("DELETE FROM album_picture_cache")
    suspend fun deleteAll()

}