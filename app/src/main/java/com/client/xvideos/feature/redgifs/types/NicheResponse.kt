package com.client.xvideos.feature.redgifs.types

import com.google.gson.annotations.SerializedName

data class NicheResponse(
    @SerializedName("niches") val niches: List<Niche>
)

data class Niche(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("gifs") val gifs: Int,
    @SerializedName("subscribers") val subscribers: Int,
    @SerializedName("thumbnail") val thumbnail: String,
    @SerializedName("previews") val previews: List<Preview>
)

data class Preview(
    @SerializedName("id") val id: String,
    @SerializedName("thumbnail")  val thumbnail: String
)






