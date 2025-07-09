package com.redgifs.model

import com.google.gson.annotations.SerializedName

data class URL1(
    @SerializedName("thumbnail") val thumbnail: String = "",     // Картинка как SD
    @SerializedName("silent") val silent: String? = null,     // * Полное видео в mp4 !!! Без звука в HD Для скачивания
    @SerializedName("poster") val poster: String? = null,     // Большая картинка Видео как HD
    @SerializedName("html") val html: String? = null,       // * Ссылка на веб-страницу с медиа. Полноэкранный режим. Типа ссылки
    @SerializedName("sd") val sd: String = "",            // * SD-ссылка на медиафайл.                                 3.5 MB
    @SerializedName("hd") val hd: String? = null,         // * HD-ссылка на медиафайл (может отсутствовать). Со звуком 21MB
)

//"urls": {
//    "thumbnail": "https://media.redgifs.com/UnusualAttachedHorseshoecrab-mobile.jpg",
//    "silent": "https://media.redgifs.com/UnusualAttachedHorseshoecrab-silent.mp4",
//    "poster": "https://media.redgifs.com/UnusualAttachedHorseshoecrab-poster.jpg",
//    "html": "https://www.redgifs.com/ifr/unusualattachedhorseshoecrab",
//    "hd": "https://media.redgifs.com/UnusualAttachedHorseshoecrab.mp4",
//    "sd": "https://media.redgifs.com/UnusualAttachedHorseshoecrab-mobile.mp4"
//},