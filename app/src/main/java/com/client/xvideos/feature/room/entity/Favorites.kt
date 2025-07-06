package com.client.xvideos.feature.room.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.Relation
import java.util.Date

/**
 * @see <img height="640"  src="https://ah-img.luscious.net/Joking42/499900/gk4rhayxqaadbfo_01JN9493Z724XCWD4KGPD740CY.1680x0.jpg"/>
 */

data class FavoriteWithItem(
    @Embedded
    val favorite: Favorites,

    @Relation(
        parentColumn = "itemId",
        entityColumn = "id"
    )
    val item: Items
)


@Entity(
    tableName = "favorites",
    foreignKeys = [
        ForeignKey(
            entity = Items::class,
            parentColumns = ["id"],
            childColumns = ["itemId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["itemId"])]
)
data class Favorites(
    @PrimaryKey
    val id: Long = 0,
    val itemId: Long, // Внешний ключ на Items.id
    val date: Date = Date(),   // Время создания записи
)



