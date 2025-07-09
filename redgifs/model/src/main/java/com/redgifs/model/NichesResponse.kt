package com.redgifs.model

import com.google.gson.annotations.SerializedName

data class NichesResponse(
    @SerializedName("niches") val niches: List<Niche>
)

data class Niche(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("gifs") val gifs: Long,
    @SerializedName("subscribers") val subscribers: Long,
    @SerializedName("thumbnail") val thumbnail: String,
    @SerializedName("previews") val previews: List<Preview>
)

data class Preview(
    @SerializedName("id") val id: String,
    @SerializedName("thumbnail")  val thumbnail: String
)






