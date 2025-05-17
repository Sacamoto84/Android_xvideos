package com.client.xvideos.feature.redgifs.http

import com.client.xvideos.feature.redgifs.ApiClient
import com.client.xvideos.feature.redgifs.MediaType
import com.client.xvideos.feature.redgifs.Order
import com.client.xvideos.feature.redgifs.types.CreatorResponse
import com.client.xvideos.feature.redgifs.types.CreatorsResponse
import com.client.xvideos.feature.redgifs.types.GetGifResponse
import com.client.xvideos.feature.redgifs.types.GifResponse
import com.client.xvideos.feature.redgifs.types.ImageResponse
import com.client.xvideos.feature.redgifs.types.TagSuggestion
import com.client.xvideos.feature.redgifs.types.TagsResponse
import com.client.xvideos.feature.redgifs.types.TrendingImagesResponse
import com.google.android.gms.common.api.ApiException

class HTTP {

    val api = ApiClient()

    //--------------------------- GIF methods ---------------------------

    @Throws(ApiException::class)
    suspend fun getTags(vararg parameters: Pair<String, Any>): TagsResponse {
        val route = Route("GET", "/v1/tags", *parameters)
        val res: TagsResponse = api.request(route)
        return res
    }

    @Throws(ApiException::class)
    suspend fun get_gif(id: String): GetGifResponse {
        val route = Route("GET", "/v2/gifs/{id}", "id" to id)
        val res: GetGifResponse = api.request(route)
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
    ): GifResponse {

        val route = Route(
            method = "GET",
            path = "/v2/gifs/search?search_text={search_text}&order={order}&count={count}&page={page}",
            "search_text" to searchText,
            "order" to order.value,
            "count" to count,
            "page" to page,
            *params                     // дополнительные параметры из vararg
        )
        val res: GifResponse = api.request(route)
        return res
    }

    /**
     * ## Получить топ GIF-ов за неделю.
     */
    @Throws(ApiException::class)
    suspend fun get_top_this_week(
        count: Int,                      // количество элементов на страницу.
        page: Int,                       //номер страницы (1-based).
        type: MediaType = MediaType.GIF, //тип медиа (GIF, image и т.д.).
        vararg params: Pair<String, Any> = emptyArray(),
    ): GifResponse {

        val route = Route(
            method = "GET",
            path = "/v2/gifs/search?order=top7&count={count}&page={page}&type={type}",
            "count" to count,
            "page" to page,
            "type" to type.value,
            *params
        )

        val res: GifResponse = api.request(route)
        return res
    }


    //--------------------------- User/Creator methods ---------------------------

    suspend fun searchCreators(
        page: Int,
        order: Order,
        verified: Boolean,
        tags: List<String>? = null,
        params: Map<String, Any> = emptyMap()
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

        val route = Route( method = "GET", path = url, *routeParams.toList().toTypedArray()
        )

        val res: CreatorsResponse = api.request(route)
        return res


    }


    suspend fun search_creator(
        username: String,
        page: Int,
        count: Int,
        order: Order,
        type: MediaType,
        params: Map<String, Any> = emptyMap()
    ): CreatorResponse {
        val route = Route(
            method = "GET",
            path = "/v2/users/{username}/search?page={page}&count={count}&order={order}&type={type}",
            "username" to username,
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
    suspend fun get_trending_gifs(): GifResponse {
        val route = Route(method = "GET", path = "/v2/explore/trending-gifs")
        val res: GifResponse = api.request(route)
        return res
    }


    //--------------------------- Pic methods ---------------------------

    suspend fun search_image(
        searchText: String,
        order: Order = Order.NEW,
        count: Int = 100,
        page: Int = 1,
        vararg params: Pair<String, Any> = emptyArray(),
    ): ImageResponse {
        val route = Route(
            method = "GET",
            path = "/v2/gifs/search?search_text={search_text}&order={order}&count={count}&page={page}&type=i",
            "search_text" to searchText,
            "order" to order.value,
            "count" to count,
            "page" to page,
            *params
        )
        val res: TrendingImagesResponse = api.request(route)
        return res
    }

    /**
     * ## Получить список «в тренде» (Trending Images).
     */
    @Throws(ApiException::class)
    suspend fun get_trending_images(): TrendingImagesResponse {
        val route = Route(method = "GET", path = "/v2/explore/trending-images")
        val res: TrendingImagesResponse = api.request(route)
        return res
    }

    //--------------------------- Tag methods ---------------------------

    /**
     * ## Получить список популярных тегов (Trending Tags).
     */
    @Throws(ApiException::class)
    suspend fun get_trending_tags(): TagsResponse {
        val route = Route(method = "GET", path = "/v2/search/trending")
        val res: TagsResponse = api.request(route)
        return res
    }

    /**
     * ## Получить подсказки (suggest) по тегам.
     */
    @Throws(ApiException::class)
    suspend fun get_tag_suggestions(query: String): List<TagSuggestion> {
        val route =
            Route(method = "GET", path = "/v2/search/suggest?query={query}", "query" to query)
        return api.request(route)
    }


}