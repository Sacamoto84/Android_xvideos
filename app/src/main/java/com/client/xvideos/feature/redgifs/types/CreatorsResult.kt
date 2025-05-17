package com.client.xvideos.feature.redgifs.types

import com.client.xvideos.feature.redgifs.types.model.User

// Результаты поиска по создателям.
data class CreatorsResult(
    val items: List<User> = emptyList(), // Список авторов (пользователей).
    val page: Int = 0,                   // Номер текущей страницы.
    val pages: Int = 0,                  // Общее количество доступных страниц.
    val total: Int = 0                   // Общее количество авторов (пользователей).
)

