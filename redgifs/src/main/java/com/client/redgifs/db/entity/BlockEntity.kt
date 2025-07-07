package com.client.redgifs.db.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.Relation


@Entity(
    tableName = "block",
    foreignKeys = [
        ForeignKey(
            entity        = GifsInfoEntity::class,
            parentColumns = ["id"],
            childColumns  = ["gifId"],
            onDelete      = ForeignKey.SET_NULL       // GIF исчез → gifId = NULL
        )
    ],
    indices = [Index("gifId")]                         // ускоряет JOIN’ы
)
data class BlockEntity(
    @PrimaryKey
    val id: String,
    val gifId: String?                                // необязательное
)

data class BlockWithGif(
    @Embedded val block: BlockEntity,

    /** Может быть null, если gifId == null или GIF ещё не докачан */
    @Relation(
        parentColumn = "gifId",
        entityColumn = "id"
    )
    val gif: GifsInfoEntity?
)