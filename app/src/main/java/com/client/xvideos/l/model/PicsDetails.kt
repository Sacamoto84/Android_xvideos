package com.client.xvideos.l.model

import com.google.gson.annotations.SerializedName

data class PicsDetails(
    @SerializedName("height")          val height: Int, //"846"
    @SerializedName("width")           val width: Int, //"1280"
    @SerializedName("is_animated")     val is_animated: Boolean,
    @SerializedName("url_to_original") val url_to_original: String?,
    @SerializedName("url_to_video")    val url_to_video: String?,
)