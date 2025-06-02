package com.client.xvideos.feature.redgifs.http

import com.client.xvideos.feature.redgifs.types.CreatorResponse
import com.client.xvideos.feature.redgifs.types.CreatorsResponse
import com.client.xvideos.feature.redgifs.types.MediaResponse
import com.client.xvideos.feature.redgifs.types.MediaType
import com.client.xvideos.feature.redgifs.types.Order
import com.client.xvideos.feature.redgifs.types.tag.TagSuggestion
import com.client.xvideos.feature.redgifs.types.tag.TagsResponse
import com.google.android.gms.common.api.ApiException

object RedGifs {

    val api = ApiClient()

    //--------------------------- GIF methods ---------------------------


    // Возвращает список всех существующих тегов. 7к штук (имя, количество)
    // ⭐ Работает
    @Throws(ApiException::class)
    suspend fun getTags(vararg parameters: Pair<String, Any>): TagsResponse {
        val route = Route("GET", "/v1/tags", *parameters)
        val res: TagsResponse = api.request(route)
        return res
    }


    //❓❓❓Непонятно как получить номер id
    @Throws(ApiException::class)
    suspend fun getGif(id: String): MediaResponse {
        val route = Route("GET", "/v2/gifs/{id}", "id" to id)
        val res: MediaResponse = api.request(route)
        return res
    }

    /**
     * ## Поиск GIF-ов по тексту.
     */
    @Throws(ApiException::class)
    suspend fun search(
        searchText: String,             // строка поиска.
        order: Order = Order.NEW,       // порядок сортировки.
        count: Int = 100,               // сколько элементов вернуть.
        page: Int = 1,                  // номер страницы (1-based).
        vararg params: Pair<String, Any> = emptyArray(),
    ): MediaResponse {

        val route = Route(
            method = "GET",
            path = "/v2/gifs/search?search_text={search_text}&order={order}&count={count}&page={page}",
            "search_text" to searchText,
            "order" to order.value,
            "count" to count,
            "page" to page,
            *params                     // дополнительные параметры из vararg
        )
        val res: MediaResponse = api.request(route)
        return res
    }

    /**
     * ## Получить топ GIF-ов за неделю.
     * Работает
     */
    @Throws(ApiException::class)
    suspend fun getTopThisWeek(
        count: Int,                      // количество элементов на страницу.
        page: Int,                       // номер страницы (1-based).
        type: MediaType = MediaType.GIF, // тип медиа (GIF, image и т.д.).
    ): MediaResponse {

        val route = Route(
            method = "GET",
            path = "/v2/gifs/search?order=top7&count={count}&page={page}&type={type}",
            "count" to count,
            "page" to page,
            "type" to type.value,
        )

        val res: MediaResponse = api.request(route)
        return res
    }


    //--------------------------- User/Creator methods ---------------------------

    //Работает
    suspend fun searchCreators(
        page: Int = 1,
        order: Order = Order.TOP,
        verified: Boolean = true,
        tags: List<String>? = listOf("Teen", "Ass"),
    ): CreatorsResponse {

        var url = "/v1/creators/search?page={page}&order={order}"

        if (verified) {
            url += "&verified={verified}"
        }
        if (tags != null && tags.isNotEmpty()) {
            url += "&tags={tags}"
        }

        val routeParams = mutableMapOf<String, Any>(
            "page" to page,
            "order" to order.value,
            "verified" to if (verified) "y" else "n"
        )
        if (tags != null && tags.isNotEmpty()) {
            routeParams["tags"] = tags.joinToString(",")
        }

        val route = Route( method = "GET", path = url, *routeParams.toList().toTypedArray() )

        val res: CreatorsResponse = api.request(route)
        return res


    }


    suspend fun searchCreator(
        userName: String = "lilijunex",
        page: Int = 1,
        count: Int = 100,
        order: Order = Order.NEW,
        type: MediaType = MediaType.GIF,
    ): CreatorResponse {
        val route = Route(
            method = "GET",
            path = "/v2/users/{username}/search?page={page}&count={count}&order={order}&type={type}",
            "username" to userName,
            "page" to page,
            "count" to count,
            "order" to order.value,
            "type" to type.value
        )
        val res: CreatorResponse = api.request(route)
        return res
    }


    /**
     * ## Получить список «в тренде» (Trending GIFs).
     */
    @Throws(ApiException::class)
    suspend fun getTrendingGifs(): MediaResponse {
        val route = Route(method = "GET", path = "/v2/explore/trending-gifs")
        val res: MediaResponse = api.request(route)
        return res
    }


    //--------------------------- Pic methods ---------------------------

    suspend fun searchImage(
        searchText: String,
        order: Order = Order.NEW,
        count: Int = 100,
        page: Int = 1,
    ): MediaResponse {
        val route = Route(
            method = "GET",
            path = "/v2/gifs/search?search_text={search_text}&order={order}&count={count}&page={page}&type=i",
            "search_text" to searchText,
            "order" to order.value,
            "count" to count,
            "page" to page,
        )
        val res: MediaResponse = api.request(route)
        return res
    }
    /**
     * ## Получить список 10 картинок «в тренде»
     * ## ⭐ Работает ⭐
     */
    @Throws(ApiException::class)
    suspend fun getTrendingImages(): MediaResponse {
        val route = Route(method = "GET", path = "/v2/explore/trending-images")
        val res: MediaResponse = api.request(route)
        return res
    }

    //--------------------------- Tag methods ---------------------------

    /**
     * ## Получить список 20 популярных тегов (Trending Tags).
     * ## ⭐ Работает ⭐
     */
    @Throws(ApiException::class)
    suspend fun getTrendingTags(): TagsResponse {
        val route = Route(method = "GET", path = "/v2/search/trending")
        val res: TagsResponse = api.request(route)
        return res
    }

    /**
     * ## Получить подсказки (suggest) по тегам.
     */
    @Throws(ApiException::class)
    suspend fun getTagSuggestions(query: String): List<TagSuggestion> {
        val route =
            Route(method = "GET", path = "/v2/search/suggest?query={query}", "query" to query)
        return api.request(route)
    }


}