package com.client.xvideos.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.client.xvideos.room.entity.FavoriteGalleryItem
import kotlinx.coroutines.flow.Flow


@Dao
interface FavoriteGalleryDao {

//    @Query("SELECT * FROM favorite")
//    fun getAll(): List<FavoriteGalleryItem>

    @Query("SELECT * FROM favorite")
    fun getAll(): Flow<List<FavoriteGalleryItem>>

    //@Query("SELECT * FROM favorite WHERE id = :id")
    //fun getById(id: Long): FavoriteGalleryItem?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(employee: FavoriteGalleryItem)

    @Update
    fun update(employee: FavoriteGalleryItem)

    @Delete
    fun delete(employee: FavoriteGalleryItem)

}