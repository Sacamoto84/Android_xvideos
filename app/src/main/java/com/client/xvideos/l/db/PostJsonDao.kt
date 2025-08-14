package com.client.xvideos.l.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.redgifs.db.entity.CacheMediaResponseEntity



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