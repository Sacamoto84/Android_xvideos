package com.redgifs.network.api

import com.redgifs.network.http.ApiClient
import com.redgifs.network.http.Route
import com.google.android.gms.common.api.ApiException
import com.redgifs.model.tag.TagsResponse

object RedApi_Tags {

    val api = ApiClient

    /**
     * #### Возвращает список всех существующих тегов. 7к штук (имя, количество)
     */
    @Throws(ApiException::class)
    suspend fun getTags(vararg parameters: Pair<String, Any>): TagsResponse {
        return RedApi.api.request(Route("GET", "/v1/tags", *parameters))
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