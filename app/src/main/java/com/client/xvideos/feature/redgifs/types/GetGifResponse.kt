package com.client.xvideos.feature.redgifs.types

data class GetGifResponse(
    val gif: MediaInfo,
    val user: UserInfo? // Optional = nullable
)