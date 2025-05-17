package com.client.xvideos.feature.redgifs.types

data class MediaInfo(

    val id: String = "id",
    val createDate: Long = 0,
    val likes: Int = 0,
    val width: Int = 100,
    val height: Int = 100,
    val tags: List<String> = emptyList(),
    val description: String = "avgColor",
    val views: Int? = null,
    val type: Int = 0,  //1-Gif 2-Image
    val userName: String = "userName",           // "lilijunex"
    val urls: URL = URL(),                // Вложенный объект

    val duration: Double? = null,
    val hls: Boolean? = null,
    val niches: List<String>? = null,
    )