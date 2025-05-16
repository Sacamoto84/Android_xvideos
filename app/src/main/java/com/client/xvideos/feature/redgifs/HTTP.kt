package com.client.xvideos.feature.redgifs

import com.client.xvideos.feature.redgifs.ApiClient.request
import com.client.xvideos.feature.redgifs.ApiClient.requestText
import com.client.xvideos.feature.redgifs.model.TagInfo
import com.client.xvideos.feature.redgifs.model.TagsResponse

object API {

    suspend fun getTags(): List<TagInfo> {
        val response: TagsResponse = request("https://api.redgifs.com/v2/tags")
        return response.tags
    }


    /**
     * Найдите одного создателя/пользователя RedGifs по имени пользователя.
     * Параметры:
     * username — Имя пользователя создателя/пользователя.
     * page  — Номер текущей страницы профиля создателя/пользователя.
     * count — Общее количество GIF-файлов для возврата.
     * order — Заказ на возврат GIF-файлов создателя/пользователя.
     * type  – Следует ли возвращать изображение или результаты GIF. По умолчанию возвращает GIF-файлы.
     */
    suspend fun searchCreator(
        username: String,
        page: Int = 1,
        count: Int = 80,
        order: Order = Order.RECENT,
        type: MediaType = MediaType.GIF,
    ): CreatorResponse {
        val route = Route(
            "GET",
            "/v2/users/{username}/search?page={page}&count={count}&order={order}&type={type}",
            "username" to username,
            "page" to page,
            "count" to count,
            "order" to order.value,
            "type" to type.value
        )
        val res  = requestText(route)
        res
        return CreatorResponse(
            page = 0,
            pages = 0,
            total = 0,
            gifs = emptyList(),
            users = emptyList(),
            niches = emptyList(),
            tags = emptyList()
        )
    }




}