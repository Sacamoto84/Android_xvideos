package com.client.xvideos.feature.redgifs.types

data class CreatorResponse(
    val gifs: List<MediaItem> = emptyList(), //MediaItems = emptyList(), MediaItem ImageInfo
    val users: List<UserInfo> = emptyList(), //
    val niches: List<NichesInfo> = emptyList(),//
    val tags: List<String> = emptyList(),
    val page: Int = 0,    //
    val pages: Int = 0,   //
    val total: Int = 0,   //
)
