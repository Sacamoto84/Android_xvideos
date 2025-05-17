package com.client.xvideos.feature.redgifs.types

// Результаты поиска по создателям.
data class CreatorsResult(
    val items: List<UserInfo> = emptyList(), // Список авторов (пользователей).
    val page: Int = 0,                   // Номер текущей страницы.
    val pages: Int = 0,                  // Общее количество доступных страниц.
    val total: Int = 0                   // Общее количество авторов (пользователей).
)

