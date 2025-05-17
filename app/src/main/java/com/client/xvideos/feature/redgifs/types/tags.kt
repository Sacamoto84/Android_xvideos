package com.client.xvideos.feature.redgifs.types

data class TagInfo(
    val name: String, //
    val count: Int    //
)

data class TagsResponse(
    val tags: List<TagInfo> //
)

data class TagSuggestion(
    val gifs: Int,          //
    val text: String,       //
    val type: String        //
)