package com.client.xvideos.feature.redgifs.types.search

import com.google.gson.annotations.SerializedName



//"type": "niche",
//"text": "Anal Sex",
//"id": "anal-sex",
//"image": "https://userpic.redgifs.com/niches/thumbnails/anal-sex-f764b259.jpg",
//"subscribers": "276347"
data class SearchItemNichesResponse(
    @SerializedName("type")  val type: String ,
    @SerializedName("text")  val  text: String ,
    @SerializedName("id")  val id: String,
    @SerializedName("image")  val image: String?,
    @SerializedName("subscribers") val subscribers : Long
)