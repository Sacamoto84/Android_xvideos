package com.client.xvideos.feature.redgifs.types

data class GifInfo(
    val id: String = "id",                   //
    val createDate: Long = 0,             // int → Long, если это Unix-время
    val has_audio: Boolean = false,
    val width: Int = 100,
    val height: Int = 100,
    val likes: Int = 0,
    val tags: List<String> = emptyList(),
    val verified: Boolean = false,
    val views: Int? = null,                  // Optional
    val duration: Double = 0.0,             // float → Double
    val published: Boolean = false,
    val type: Int = 2,                    // Literal[1,2] — можно ограничить enum'ом, если нужно
    val urls: URL = URL(),              // Вложенный объект
    val userName: String = "name",
)