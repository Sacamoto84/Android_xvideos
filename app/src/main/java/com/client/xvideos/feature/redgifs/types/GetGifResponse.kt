package com.client.xvideos.feature.redgifs.types

import com.google.gson.annotations.SerializedName

data class GetGifResponse(
    @SerializedName("gif") val gif: MediaInfo,
    @SerializedName("user") val user: UserInfo?, // Optional = nullable
)