package com.client.xvideos.feature.redgifs.types

data class ImageInfo(
    val cta: String? = null,
    val client_id: String? = null,                 // Optional -> nullable
    val createDate: Long = 0,                     // int → Long (если timestamp)
    val height: Int = 100,
    val description: String = "avgColor",
    val avgColor: String = "avgColor",
    val gallery: String? = null,
    val hls: Boolean? = null,
    val id: String = "id",
    val likes: Int = 0,
    val niches: List<String> = emptyList(),
    val published: Boolean = false,
    val type: Int = 1,
    val sexuality: List<String>? = null,          // Optional список
    val tags: List<String> = emptyList(),
    val urls: MediaInfo = MediaInfo(),
    val userName: String = "userName",
    val verified: Boolean = false,
    val views: Int? = null,                      // Optional
    val width: Int = 100,
)


