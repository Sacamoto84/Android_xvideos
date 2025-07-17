package com.redgifs.model.tag

import com.google.gson.annotations.SerializedName

data class TagSuggestion(
    @SerializedName("gifs") val gifs: Long,          //
    @SerializedName("text") val text: String,       //
    @SerializedName("type") val type: String        //
)