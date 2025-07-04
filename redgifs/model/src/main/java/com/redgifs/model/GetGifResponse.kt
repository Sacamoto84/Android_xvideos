package com.redgifs.model

import com.google.gson.annotations.SerializedName

data class GetGifResponse(
    @SerializedName("gif") val gif: GifsInfo,
    @SerializedName("user") val user: UserInfo?, // Optional = nullable
)