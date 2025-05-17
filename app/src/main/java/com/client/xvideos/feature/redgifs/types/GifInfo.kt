package com.client.xvideos.feature.redgifs.types

data class GifInfo(
    val id: String = "",                   //
    val client_id: String? = null,            // Optional -> nullable
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
    val urls: MediaInfo = MediaInfo(),              // Вложенный объект
    val user_name: String = "name",
    val avgColor: String ="avgColor",
    val gallery: String  = "gallery"
)