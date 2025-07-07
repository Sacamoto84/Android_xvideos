package com.client.redgifs.network.api

import com.client.xvideos.redgifs.network.http.ApiClient
import com.client.xvideos.redgifs.network.http.Route
import com.client.xvideos.redgifs.network.types.tag.TagsResponse
import com.google.android.gms.common.api.ApiException

object RedApi_Tags {

    val api = ApiClient

    /**
     * #### Возвращает список всех существующих тегов. 7к штук (имя, количество)
     */
    @Throws(ApiException::class)
    suspend fun getTags(): TagsResponse {
        return api.request(Route("GET", "/v1/tags"))
    }

    /**
     * #### Получить список 20 популярных тегов (Trending Tags).
     */
    @Throws(ApiException::class)
    suspend fun getTrendingTags(): TagsResponse {
        val route = Route(method = "GET", path = "/v2/search/trending")
        val res: TagsResponse = RedApi.api.request(route)
        return res
    }

}