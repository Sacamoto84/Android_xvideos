package com.redgifs.model

import com.google.gson.annotations.SerializedName

data class CreatorResponse(
    @SerializedName("gifs") val gifs: List<GifsInfo> = emptyList(), //MediaItems = emptyList(), MediaItem ImageInfo
    @SerializedName("users") val users: List<UserInfo> = emptyList(), //
    @SerializedName("niches") val niches: List<NichesInfo> = emptyList(),//
    @SerializedName("tags") val tags: List<String> = emptyList(),
    @SerializedName("page") val page: Int = 0,    //
    @SerializedName("pages") val pages: Int = 0,   //
    @SerializedName("total") val total: Int = 0,   //
)
