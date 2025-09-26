package com.client.xvideos.common.room.entity.l

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.client.xvideos.common.room.entity.r.getCurrentTimeText

@Entity(tableName = "l_album_picture_cache")
data class L_AlbumPictureCacheEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0, //Номер альбома
    val content: String, //Json список PicsDetails
    val timeCreate: Long = System.currentTimeMillis(),
    val timeCreateText: String = getCurrentTimeText()// добавляем поле для времени в текстовом формате = getCurrentTimeText()
)