package com.client.xvideos.feature.redgifs.api

import com.client.xvideos.feature.redgifs.http.ApiClient
import com.client.xvideos.feature.redgifs.http.Route
import com.client.xvideos.feature.redgifs.types.MediaResponse
import com.client.xvideos.feature.redgifs.types.Order
import com.client.xvideos.feature.redgifs.types.search.SearchCreatorsResponse
import com.google.android.gms.common.api.ApiException

object RedApi_Search {

    val api = ApiClient

    //https://api.redgifs.com/v2/creators/suggest?query=Ana
    //Возвращает 5 элементов
    @Throws(ApiException::class)
    suspend fun searchCreatorsShort(text: String): SearchCreatorsResponse {
        val route =
            Route(method = "GET", path = "/v2/creators/suggest?query={text}", "text" to text)
        return RedApi.api.request<SearchCreatorsResponse>(route)
    }


    /**
     * ## Поиск GIF-ов по тексту.
     * https://api.redgifs.com/v2/search/gifs?query=anal&page=2&count=40&order=top
     *
     * top, trending, latest
     */
    @Throws(ApiException::class)
    suspend fun searchGifs(
        searchText: String,             // строка поиска.
        order: Order = Order.TOP,       // порядок сортировки.
        count: Int = 100,               // сколько элементов вернуть.
        page: Int = 1,                  // номер страницы (1-based).
        verified: Boolean = false,
    ): MediaResponse {

        val route = if (!verified) {
            Route(
                method = "GET",
                path = "/v2/search/gifs?query={search_text}&order={order}&count={count}&page={page}",
                "search_text" to searchText,
                "order" to order.value,
                "count" to count,
                "page" to page,
            )
        } else {
            Route(
                method = "GET",
                path = "/v2/search/gifs?query={search_text}&order={order}&count={count}&page={page}&verified=yes",
                "search_text" to searchText,
                "order" to order.value,
                "count" to count,
                "page" to page,
            )
        }
        //return cacheMediaResponse(route)
        return RedApi.api.request(route)
    }
}