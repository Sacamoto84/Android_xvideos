package com.client.xvideos.common.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.client.xvideos.common.room.entity.CacheUrlStringRamEntity
import com.client.xvideos.common.room.entity.CacheUrlStringRomEntity

/**
 * ## Сохранение данных только пока работает программа, удаление при следующем запуске
 */
@Dao
interface CacheUrlStringRamDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(doc: CacheUrlStringRamEntity)

    @Query("SELECT * FROM cache_url_string_ram WHERE url = :url")
    suspend fun get(url: String): CacheUrlStringRamEntity?

    // ✅ Удаление всего кеша
    @Query("DELETE FROM cache_url_string_ram")
    suspend fun deleteAll()

}


@Dao
interface CacheUrlStringRomDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(doc: CacheUrlStringRomEntity)

    @Query("SELECT * FROM cache_url_string_rom WHERE url = :url")
    suspend fun get(url: String): CacheUrlStringRomEntity?

    @Query("DELETE FROM cache_url_string_rom WHERE timeCreate < :time")
    suspend fun deleteOld(time: Long)

    // ✅ Удаление всего кеша
    @Query("DELETE FROM cache_url_string_rom")
    suspend fun deleteAll()

}


