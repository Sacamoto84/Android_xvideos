package com.client.xvideos.feature.redgifs.types

import com.client.xvideos.feature.redgifs.types.model.User

// Результат вашего поиска. Он возвращается в :meth:`~redgifs.API.search()`.
data class SearchResult(
    val searched_for: String = "searched_for",  // Что было найдено (может отличаться от исходного запроса query).
    val page: Int = 0,                          // Номер текущей страницы.
    val pages: Int = 0,                         // Общее количество страниц по данному запросу.
    val total: Int = 0,                         // Общее количество найденных GIF-файлов.
    val gifs: List<GifInfo>? = null,                // Список GIF-файлов, подходящих под поисковый запрос, если есть.
    val images: List<ImageInfo>? = null,            // Список изображений, подходящих под поисковый запрос, если есть.
    val users: List<User> = emptyList(),        // Список пользователей, релевантных поисковому запросу.
    val tags: List<String> = emptyList()        // Список тегов, связанных с поисковым запросом и результатами.
)