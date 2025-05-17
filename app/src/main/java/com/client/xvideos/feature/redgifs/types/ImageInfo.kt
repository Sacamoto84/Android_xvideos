package com.client.xvideos.feature.redgifs.types

data class ImageInfo(
    val id: String = "id",
    val duration: Double = 0.0,                  //
    val createDate: Long = 0,                    // int → Long (если timestamp)
    val height: Int = 100,
    val description: String = "avgColor",
    val gallery: String? = null,
    val hls: Boolean? = null,
    val likes: Int = 0,                          //
    val niches: List<String> = emptyList(),
    val published: Boolean = false,              //
    val type: Int = 1,
    val tags: List<String> = emptyList(),        //Список тегов в медиа
    val urls: URL = URL(),
    val userName: String = "userName",           // "lilijunex"
    val verified: Boolean = false,               //
    val views: Int? = null,                      // Количество просмотров медиа
    val width: Int = 100,                        // Ширина медиа
)


