package com.client.xvideos.feature.redgifs.types

data class URL(
    val thumbnail: String = "",          // Картинка как SD
    val silent: String? = null,     // * Полное видео в mp4 !!! Без звука в HD Для скачивания
    val poster: String? = null,     // Большая картинка Видео как HD
    val html: String? = null,       // * Ссылка на веб-страницу с медиа. Полноэкранный режим. Типа ссылки
    val sd: String = "",                 // * SD-ссылка на медиафайл.                                 3.5 MB
    val hd: String? = null,         // * HD-ссылка на медиафайл (может отсутствовать). Со звуком 21MB
)

//"urls": {
//    "thumbnail": "https://media.redgifs.com/UnusualAttachedHorseshoecrab-mobile.jpg",
//    "silent": "https://media.redgifs.com/UnusualAttachedHorseshoecrab-silent.mp4",
//    "poster": "https://media.redgifs.com/UnusualAttachedHorseshoecrab-poster.jpg",
//    "html": "https://www.redgifs.com/ifr/unusualattachedhorseshoecrab",
//    "hd": "https://media.redgifs.com/UnusualAttachedHorseshoecrab.mp4",
//    "sd": "https://media.redgifs.com/UnusualAttachedHorseshoecrab-mobile.mp4"
//},