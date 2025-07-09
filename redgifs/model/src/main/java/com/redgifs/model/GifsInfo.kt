package com.redgifs.model

import com.google.gson.annotations.SerializedName

data class GifsInfo(
    @SerializedName("id") val id: String = "id",
    @SerializedName("createDate") val createDate: Long = 0,
    @SerializedName("likes") val likes: Int = 0,
    @SerializedName("width") val width: Int = 100,
    @SerializedName("height") val height: Int = 100,
    @SerializedName("tags") val tags: List<String> = emptyList(),
    @SerializedName("description") val description: String = "description",
    @SerializedName("views") val views: Long? = null,
    @SerializedName("type") val type: Int = 0,  //1-Gif 2-Image
    @SerializedName("userName") val userName: String = "userName",           // "lilijunex"
    @SerializedName("urls") val urls: URL1 = URL1(),
    @SerializedName("duration") val duration: Double? = null,
    @SerializedName("hls") val hls: Boolean? = null,
    @SerializedName("niches") val niches: List<String>? = null,
)