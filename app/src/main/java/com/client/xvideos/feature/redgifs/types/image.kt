package com.client.xvideos.feature.redgifs.types

data class ImageInfo(
    val cta: String? = null,

    val clientId: String? = null,                 // Optional -> nullable
    val createDate: Long = 0,                     // int → Long (если timestamp)

    val height: Int = 100,

    val description: String = "avgColor",
                            // Literal[1,2] — можно enum при желании
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


data class BaseImageResponse(
    val page: Int = 0,
    val pages: Int = 0,
    val total: Int = 0,
    val gifs: List<ImageInfo> = emptyList(),
    val users: List<UserInfo> = emptyList(),
    val niches: List<NichesInfo> = emptyList(),
    val tags: List<String> = emptyList()
)

typealias TrendingImagesResponse = BaseImageResponse
typealias ImageResponse = BaseImageResponse