package com.client.xvideos.feature.redgifs.types

// Результат поиска создателя.
data class CreatorResult(
    val creator: UserInfo,                     // Детали автора/пользователя.
    val page: Int = 0,                     // Текущий номер страницы.
    val pages: Int = 0,                    // Общее количество доступных страниц.
    val total: Int = 0,                    // Общее количество GIF, созданных этим автором/пользователем.
    val gifs: List<MediaInfo> = emptyList(),     // Список GIF, загруженных этим автором.
    val images: List<MediaInfo> = emptyList()  // Список изображений, загруженных этим автором.
)