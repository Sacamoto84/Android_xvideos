package com.client.xvideos.feature.redgifs.types

data class MediaInfo(
    val html: String = "",            //
    val poster: String = "",          //
    val sd: String = "",              //
    val silent: String? = null,        // Optional = nullable
    val thumbnail: String = "",       //
    val hd: String? = null          // Optional = nullable в Kotlin
)

data class GifInfo(
    val id: String = "",                   //
    val clientId: String? = null,            // Optional -> nullable
    val createDate: Long = 0,             // int → Long, если это Unix-время
    val hasAudio: Boolean = false,
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
    val userName: String = "name",
    val avgColor: String ="avgColor",
    val gallery: String  = "gallery"
)

data class GetGifResponse(
    val gif: GifInfo,
    val user: UserInfo? // Optional = nullable
)

data class BaseGifResponse(
    val page: Int,
    val pages: Int,
    val total: Int,
    val gifs: List<GifInfo>,
    val users: List<UserInfo>,
    val niches: List<NichesInfo>,
    val tags: List<String>
)

typealias GifResponse = BaseGifResponse
