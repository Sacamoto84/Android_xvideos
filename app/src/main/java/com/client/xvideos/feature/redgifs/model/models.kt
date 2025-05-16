package com.client.xvideos.feature.redgifs.model

data class Gif(
    val id: String,
    val createDate: String?, // ISO-8601, например: "2023-10-12T14:52:00Z"
    val hasAudio: Boolean,
    val width: Int,
    val height: Int,
    val likes: Int,
    val tags: List<String>,
    val verified: Boolean,
    val views: Int?,         // Может быть null
    val duration: Float,
    val published: Boolean,
    val urls: Url1,           // Отдельная модель, см. ниже
    val username: String,
    val type: Int,
    val avgColor: String
)

data class Url1(
    val gif: String?,
    val mp4: String?,
    val webm: String?,
    val poster: String?,
    val thumbnail: String?
)

data class Image(
    val id: String,
    val createDate: String?,       // ISO 8601 строка, например: "2024-03-11T18:02:31"
    val width: Int,
    val height: Int,
    val likes: Int,
    val tags: List<String>,
    val verified: Boolean,
    val views: Int?,               // Может быть null
    val published: Boolean,
    val urls: Url2,                 // См. ниже
    val username: String,
    val type: Int,
    val avgColor: String
)

data class Url2(
    val poster: String?,     // или другие поля: gif, webm, mp4 — в зависимости от структуры
    val thumbnail: String?,
    val sd: String?,
    val hd: String?
)

