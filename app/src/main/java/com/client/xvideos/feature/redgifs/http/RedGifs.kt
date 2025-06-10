package com.client.xvideos.feature.redgifs.http

import com.client.xvideos.App
import com.client.xvideos.feature.redgifs.db.CacheMediaResponseEntity
import com.client.xvideos.feature.redgifs.db.getCurrentTimeText
import com.client.xvideos.feature.redgifs.http.RedGifs.api
import com.client.xvideos.feature.redgifs.types.CreatorResponse
import com.client.xvideos.feature.redgifs.types.CreatorsResponse
import com.client.xvideos.feature.redgifs.types.MediaResponse
import com.client.xvideos.feature.redgifs.types.MediaType
import com.client.xvideos.feature.redgifs.types.NicheResponse
import com.client.xvideos.feature.redgifs.types.NichesInfo
import com.client.xvideos.feature.redgifs.types.NichesResponse
import com.client.xvideos.feature.redgifs.types.Order
import com.client.xvideos.feature.redgifs.types.TopCreatorsResponse
import com.client.xvideos.feature.redgifs.types.tag.TagSuggestion
import com.client.xvideos.feature.redgifs.types.tag.TagsResponse
import com.google.android.gms.common.api.ApiException
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import timber.log.Timber

object RedGifs {

    val api = ApiClient()

    //--------------------------- GIF methods ---------------------------


    // Возвращает список всех существующих тегов. 7к штук (имя, количество)
    // ⭐ Работает
    @Throws(ApiException::class)
    suspend fun getTags(vararg parameters: Pair<String, Any>): TagsResponse {return api.request(Route("GET", "/v1/tags", *parameters))}


    @Throws(ApiException::class)
    suspend fun getGif(id: String): MediaResponse {
        val route = Route("GET", "/v2/gifs/{id}", "id" to id)
        return cacheMediaResponse(route)
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
        return cacheMediaResponse(route)
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
        return cacheMediaResponse(route)
    }

    @Throws(ApiException::class)
    suspend fun getTopThisMonth(
        count: Int,                      // количество элементов на страницу.
        page: Int,                       // номер страницы (1-based).
        type: MediaType = MediaType.GIF, // тип медиа (GIF, image и т.д.).
    ): MediaResponse {
        val route = Route(method = "GET", path = "/v2/gifs/search?order=top28&count={count}&page={page}&type={type}",
            "count" to count, "page" to page, "type" to type.value)
        return cacheMediaResponse(route)
    }

    @Throws(ApiException::class)
    suspend fun getTopTrending(
        count: Int,                      // количество элементов на страницу.
        page: Int,                       // номер страницы (1-based).
        type: MediaType = MediaType.GIF, // тип медиа (GIF, image и т.д.).
    ): MediaResponse {
        val route = Route(method = "GET", path = "/v2/gifs/search?order=trending&count={count}&page={page}&type={type}",
            "count" to count, "page" to page, "type" to type.value)
        return cacheMediaResponse(route)
    }

    //Последние, новые посты, не нужно кешировать
    @Throws(ApiException::class)
    suspend fun getTopLatest(
        count: Int,                      // количество элементов на страницу.
        page: Int,                       // номер страницы (1-based).
        type: MediaType = MediaType.GIF, // тип медиа (GIF, image и т.д.).
    ): MediaResponse {
        val route = Route(method = "GET", path = "/v2/gifs/search?order=new&count={count}&page={page}&type={type}",
            "count" to count, "page" to page, "type" to type.value)

        Timber.i("!!! getTopLatest ${route.url}")
        // Запрос из сети
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
            "page" to page, "order" to order.value, "verified" to if (verified) "y" else "n"
        )

        if (tags != null && tags.isNotEmpty()) {
            routeParams["tags"] = tags.joinToString(",")
        }

        val route = Route(method = "GET", path = url, *routeParams.toList().toTypedArray())
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
     * ## Получить список «в тренде» (Trending GIFs). Возвращает 10 Gifs
     */
    @Throws(ApiException::class)
    suspend fun getTrendingGifs(): MediaResponse {
        val route = Route(method = "GET", path = "/v2/explore/trending-gifs")
        return cacheMediaResponse(route)
    }


    //--------------------------- Pic methods ---------------------------

    suspend fun searchImage(searchText: String, order: Order = Order.NEW, count: Int = 100, page: Int = 1): MediaResponse {
        val route = Route(method = "GET", path = "/v2/gifs/search?search_text={search_text}&order={order}&count={count}&page={page}&type=i",
            "search_text" to searchText, "order" to order.value, "count" to count, "page" to page)
        return cacheMediaResponse(route)
    }

    /**
     * ## Получить список 10 картинок «в тренде»
     * ## ⭐ Работает ⭐
     */
    @Throws(ApiException::class)
    suspend fun getTrendingImages(): MediaResponse {
        val route = Route(method = "GET", path = "/v2/explore/trending-images")
        return cacheMediaResponse(route)
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
        val route = Route(method = "GET", path = "/v2/search/suggest?query={query}", "query" to query)
        return api.request(route)
    }




    //niches

    @Throws(ApiException::class)
    suspend fun getNiche(niches: String = "pumped-pussy"): NichesInfo {
        val route = Route(method = "GET", path = "/v2/niches/{niches}", "niches" to niches)
        return api.request<NicheResponse>(route).niche
    }

    //https://api.redgifs.com/v2/niches/cowgirl-pov/gifs?count=30&page=1&order=new
    @Throws(ApiException::class)
    suspend fun getNiches(niches: String = "pumped-pussy", page: Int = 1, count: Int = 100, order: Order = Order.NEW): MediaResponse {
        val route = Route(method = "GET", path = "/v2/niches/{niches}/gifs?page={page}&count={count}&order={order}",
            "niches" to niches, "page" to page, "count" to count, "order" to order.value)
        return cacheMediaResponse(route)
    }

    //Похожее
    //https://api.redgifs.com/v2/niches/pumped-pussy/related
    @Throws(ApiException::class)
    suspend fun getNichesRelated(niches: String = "pumped-pussy"): NichesResponse {
        val route = Route(method = "GET", path = "/v2/niches/{niches}/related", "niches" to niches)
        return api.request<NichesResponse>(route)
    }

    //https://api.redgifs.com/v2/niches/pumped-pussy/top-creators
    @Throws(ApiException::class)
    suspend fun getNichesTopCreators(niches: String = "pumped-pussy"): TopCreatorsResponse {
        val route = Route(method = "GET", path = "/v2/niches/{niches}/top-creators", "niches" to niches)
        return api.request<TopCreatorsResponse>(route)
    }

    data class TagsContainerGson(@SerializedName("tags") val tags: List<String>)

    //https://api.redgifs.com/v2/niches/pumped-pussy/top-tags
    @Throws(ApiException::class)
    suspend fun getNichesTopTags(niches: String = "pumped-pussy"): List<String> {
        val route = Route(method = "GET", path = "/v2/niches/{niches}/top-tags", "niches" to niches)
        return api.request<TagsContainerGson>(route).tags
    }

}

private suspend fun cacheMediaResponse(route : Route) : MediaResponse {

    val cacheDao = App.instance.db.cacheMedaResponseDao()
    val cachedEntity = cacheDao.get(route.url)

    if (cachedEntity != null) {
        // Десериализуем JSON из кеша
        Timber.i("!!! Берем данные из кеша ${route.url}")
        val gson = Gson()
        return gson.fromJson(cachedEntity.content, MediaResponse::class.java)
    } else {
        Timber.i("!!! Берем данные из Сети ${route.url}")
        // Запрос из сети
        val res: MediaResponse = api.request(route)
        // Сохраняем в кеш (с текущим временем)
        val jsonContent = Gson().toJson(res)
        val entity = CacheMediaResponseEntity(
            url = route.url,
            content = jsonContent,
            timeCreate = System.currentTimeMillis(),
            timeCreateText = getCurrentTimeText()
        )
        cacheDao.insert(entity)
        return res
    }

}
