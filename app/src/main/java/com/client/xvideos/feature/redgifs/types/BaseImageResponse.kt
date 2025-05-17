package com.client.xvideos.feature.redgifs.types

data class BaseImageResponse(
    val page: Int = 0,
    val pages: Int = 0,
    val total: Int = 0,
    val gifs: List<MediaInfo> = emptyList(),
    val users: List<UserInfo> = emptyList(),
    val niches: List<NichesInfo> = emptyList(),
    val tags: List<String> = emptyList()
)

typealias TrendingImagesResponse = BaseImageResponse
typealias ImageResponse = BaseImageResponse