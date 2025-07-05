package com.client.xvideos.redgifs.network.types.search

import com.google.gson.annotations.SerializedName

//"type": "tag",
//"text": "Anal",
//"gifs": 752986
data class SearchItemTagsResponse(
    @SerializedName("type")  val type: String = "tag",
    @SerializedName("text")  val text: String,
    @SerializedName("gifs")  val gifs: Long
)