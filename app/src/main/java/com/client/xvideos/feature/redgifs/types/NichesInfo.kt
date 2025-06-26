package com.client.xvideos.feature.redgifs.types

import com.google.gson.annotations.SerializedName

data class NicheResponse(
    @SerializedName("niche") val niche: NichesInfo
)

// Проверен
data class NichesInfo(
    @SerializedName("cover") val cover: String? = "cover",
    @SerializedName("description") val description: String = "description",
    @SerializedName("gifs") val gifs: Long = 0,
    @SerializedName("id") val id: String = "id",
    @SerializedName("name") val name: String = "name",
    @SerializedName("owner") val owner: String = "owner",
    @SerializedName("subscribers") val subscribers: Long = 0,
    @SerializedName("thumbnail") val thumbnail: String = "thumbnail",
    @SerializedName("rules") val rules: String? = "rules",
)





