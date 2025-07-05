package com.client.xvideos.redgifs.network.types

import com.google.gson.annotations.SerializedName

data class GetGifResponse(
    @SerializedName("gif") val gif: GifsInfo,
    @SerializedName("user") val user: UserInfo?, // Optional = nullable
)