package com.redgifs.network.api

import com.redgifs.network.http.ApiClient
import com.redgifs.network.http.Route
import com.redgifs.model.tag.TagsResponse

class RedApi_Tags(val api: ApiClient ) {

    /**
     * #### Возвращает список всех существующих тегов. 7к штук (имя, количество)
     */
    suspend fun getTags(): TagsResponse {
        return api.request(Route("GET", "/v1/tags"))
    }

    /**
     * #### Получить список 20 популярных тегов (Trending Tags).
     */
    suspend fun getTrendingTags(): TagsResponse {
        val route = Route(method = "GET", path = "/v2/search/trending")
        val res: TagsResponse = api.request(route)
        return res
    }

}