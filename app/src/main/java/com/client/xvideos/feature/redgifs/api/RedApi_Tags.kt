package com.client.xvideos.feature.redgifs.api

import com.client.xvideos.feature.redgifs.http.ApiClient
import com.client.xvideos.feature.redgifs.http.Route
import com.client.xvideos.feature.redgifs.types.tag.TagsResponse
import com.google.android.gms.common.api.ApiException

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