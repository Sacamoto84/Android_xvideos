package com.client.xvideos.feature.redgifs.types

data class GetGifResponse(
    val gif: GifInfo,
    val user: UserInfo? // Optional = nullable
)