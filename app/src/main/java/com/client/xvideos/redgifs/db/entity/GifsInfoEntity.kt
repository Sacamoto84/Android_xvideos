package com.client.xvideos.redgifs.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.client.xvideos.redgifs.network.types.GifsInfo
import com.client.xvideos.redgifs.network.types.URL1

@Entity(tableName = "gifs_info")
data class GifsInfoEntity(
    @PrimaryKey
    val id: String,
    val createDate: Long,
    val likes: Int,
    val width: Int,
    val height: Int,
    val description: String,
    val views: Long?,
    val type: Int,
    val userName: String,
    val urls: URL1,
    val tags: List<String>,
    val duration: Double?,
    val hls: Boolean?,
    val niches: List<String>?
)

/* ------ маппинг сети -> база ------ */
fun GifsInfo.toEntity(): GifsInfoEntity =
    GifsInfoEntity(
        id          = id,
        createDate  = createDate,
        likes       = likes,
        width       = width,
        height      = height,
        description = description,
        views       = views,
        type        = type,
        userName    = userName,
        urls        = urls,
        tags        = tags,
        duration    = duration,
        hls         = hls,
        niches      = niches
    )

/* ------ маппинг базы -> доменная модель (если пригодится) ------ */
fun GifsInfoEntity.toDomain(): GifsInfo =
    GifsInfo(
        id          = id,
        createDate  = createDate,
        likes       = likes,
        width       = width,
        height      = height,
        tags        = tags,
        description = description,
        views       = views,
        type        = type,
        userName    = userName,
        urls        = urls,
        duration    = duration,
        hls         = hls,
        niches      = niches
    )

/* ------ вспомогательный bulk‑маппинг ------ */
fun List<GifsInfo>.toEntityList(): List<GifsInfoEntity> = map { it.toEntity() }

