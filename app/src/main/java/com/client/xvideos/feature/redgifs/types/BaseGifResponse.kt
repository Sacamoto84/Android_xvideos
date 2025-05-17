package com.client.xvideos.feature.redgifs.types

data class BaseGifResponse(
    val page: Int,
    val pages: Int,
    val total: Int,
    val gifs: List<MediaInfo>,
    val users: List<UserInfo>,
    val niches: List<NichesInfo>,
    val tags: List<String>
)

typealias GifResponse = BaseGifResponse
