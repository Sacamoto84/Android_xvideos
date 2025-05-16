package com.client.xvideos.feature.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.client.xvideos.feature.room.entity.FavoriteWithItem
import com.client.xvideos.feature.room.entity.Favorites
import com.client.xvideos.feature.room.entity.Items
import kotlinx.coroutines.flow.Flow
import java.util.Date

////    @Query("SELECT * FROM favorite")
////    fun getAll(): List<FavoriteGalleryItem>
//
//    @Query("SELECT * FROM favorite")
//    fun getAll(): Flow<List<FavoriteGalleryItem>>
//
//    //@Query("SELECT * FROM favorite WHERE id = :id")
//    //fun getById(id: Long): FavoriteGalleryItem?
//
//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    fun insert(employee: FavoriteGalleryItem)
//
//    @Update
//    fun update(employee: FavoriteGalleryItem)
//
//    @Delete
//    fun delete(employee: FavoriteGalleryItem)
//
//    @Query("DELETE FROM favorite WHERE id = :id")
//    fun deleteById(id: Long)
//
//    @Query("SELECT favorite FROM favorite WHERE id = :id")
//    fun isFavorite(id: Long): Boolean?
//
//    @Query("UPDATE favorite SET favorite = :isFavorite WHERE id = :id")
//    fun updateFavorite(id: Long, isFavorite: Boolean)
//
//    idItems = :itemId")
//    fun deleteByItemId(itemId: Long)
@Dao
interface ItemsDao{

    @Query("SELECT EXISTS(SELECT 1 FROM items WHERE id = :id)")
    fun existsItem(id: Long): Boolean

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertItem(item: Items)


}


@Dao
interface FavoriteGalleryDao {



    // Получить все избранные с деталями Items
    @Transaction
    @Query("SELECT * FROM favorites ORDER BY date DESC")
    fun getAllFavoritesWithItemsOrderDateDesc(): Flow<List<FavoriteWithItem>>

    /**
     * Проверка, есть ли элемент в избранном
     */
    @Query("SELECT EXISTS(SELECT 1 FROM favorites WHERE itemId = :itemId)")
    fun isFavorite(itemId: Long): Boolean

    // Добавить в избранное
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertFavorite(favorite: Favorites)



    // Удалить из избранного по itemId
    @Query("DELETE FROM favorites WHERE itemId = :itemId")
    fun deleteByItemId(itemId: Long)

    // Переключить статус избранного
    @Transaction
    fun toggleFavorite(itemId: Long) {
        if (isFavorite(itemId)) {
            deleteByItemId(itemId)
        } else {
            insertFavorite(
                Favorites(
                    itemId = itemId,
                    id = itemId
                )
            )
        }
    }
}