package com.redgifs.network.api

import com.redgifs.network.http.ApiClient
import com.redgifs.network.http.Route
import com.redgifs.model.CreatorResponse
import com.redgifs.model.CreatorsResponse
import com.redgifs.model.MediaResponse
import com.redgifs.model.MediaType
import com.redgifs.model.NicheResponse
import com.redgifs.model.NichesResponse
import com.redgifs.model.Order
import com.redgifs.model.TopCreatorsResponse
import com.redgifs.model.UserInfo
import com.redgifs.model.search.SearchItemNichesResponse
import com.redgifs.model.search.SearchItemTagsResponse
import com.redgifs.model.tag.TagSuggestion
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.annotations.SerializedName
import com.google.gson.reflect.TypeToken
import com.redgifs.db.db.dao.CacheMediaResponseDao
import com.redgifs.db.db.entity.CacheMediaResponseEntity
import com.redgifs.db.db.entity.getCurrentTimeText
import timber.log.Timber
import javax.inject.Inject
import kotlin.Int

class RedApi @Inject constructor(
   val dao: CacheMediaResponseDao
) {

    val api = ApiClient

    //--------------------------- GIF methods ---------------------------
    suspend fun getGif(id: String): MediaResponse {
        val route = Route("GET", "/v2/gifs/{id}", "id" to id)
        return cacheMediaResponse(route, this, dao)
    }


    /**
     * ## Получить топ GIF-ов за неделю.
     * Работает
     */
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
        return cacheMediaResponse(route, this, dao)
    }


    suspend fun getTopThisMonth(
        count: Int,                      // количество элементов на страницу.
        page: Int,                       // номер страницы (1-based).
        type: MediaType = MediaType.GIF, // тип медиа (GIF, image и т.д.).
    ): MediaResponse {
        val route = Route(
            method = "GET",
            path = "/v2/gifs/search?order=top28&count={count}&page={page}&type={type}",
            "count" to count,
            "page" to page,
            "type" to type.value
        )
        return cacheMediaResponse(route, this, dao)
    }


    suspend fun getTopTrending(
        count: Int,                      // количество элементов на страницу.
        page: Int,                       // номер страницы (1-based).
        type: MediaType = MediaType.GIF, // тип медиа (GIF, image и т.д.).
    ): MediaResponse {
        val route = Route(
            method = "GET",
            path = "/v2/gifs/search?order=trending&count={count}&page={page}&type={type}",
            "count" to count,
            "page" to page,
            "type" to type.value
        )
        return cacheMediaResponse(route, this, dao)
    }

    //Последние, новые посты, не нужно кешировать

    suspend fun getTopLatest(
        count: Int,                      // количество элементов на страницу.
        page: Int,                       // номер страницы (1-based).
        type: MediaType = MediaType.GIF, // тип медиа (GIF, image и т.д.).
    ): MediaResponse {
        val route = Route(
            method = "GET",
            path = "/v2/gifs/search?order=new&count={count}&page={page}&type={type}",
            "count" to count,
            "page" to page,
            "type" to type.value
        )

        Timber.i("!!! getTopLatest ${route.url}")
        // Запрос из сети
        val res: MediaResponse = api.request(route)

        return res
    }

    //--------------------------- User/Creator methods ---------------------------


    //https://api.redgifs.com/v1/users/drfunkenfootz_md
    suspend fun readCreator(
        userName: String = "lilijunex",
    ): UserInfo {
        val route = Route(
            method = "GET",
            path = "/v1/users/{username}",
            "username" to userName,
        )

        val res: UserInfo = api.request(route)
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

    suspend fun getTrendingGifs(): MediaResponse {
        val route = Route(method = "GET", path = "/v2/explore/trending-gifs")
        return cacheMediaResponse(route, this, dao)
    }


    //--------------------------- Pic methods ---------------------------

    suspend fun searchImage(
        searchText: String,
        order: Order = Order.NEW,
        count: Int = 100,
        page: Int = 1
    ): MediaResponse {
        val route = Route(
            method = "GET",
            path = "/v2/gifs/search?search_text={search_text}&order={order}&count={count}&page={page}&type=i",
            "search_text" to searchText,
            "order" to order.value,
            "count" to count,
            "page" to page
        )
        return cacheMediaResponse(route, this, dao)
    }

    /**
     * ## Получить список 10 картинок «в тренде»
     * ## ⭐ Работает ⭐
     */

    suspend fun getTrendingImages(): MediaResponse {
        val route = Route(method = "GET", path = "/v2/explore/trending-images")
        return cacheMediaResponse(route, this, dao)
    }

    //--------------------------- Tag methods ---------------------------


    //niches


    suspend fun getNiche(niches: String = "pumped-pussy"): NicheResponse {
        val route = Route(method = "GET", path = "/v2/niches/{niches}", "niches" to niches)
        return api.request(route)
    }

    //https://api.redgifs.com/v2/niches/cowgirl-pov/gifs?count=30&page=1&order=new

    suspend fun getNiches(
        niches: String = "pumped-pussy",
        page: Int = 1,
        count: Int = 100,
        order: Order = Order.NEW
    ): MediaResponse {
        val route = Route(
            method = "GET",
            path = "/v2/niches/{niches}/gifs?page={page}&count={count}&order={order}",
            "niches" to niches,
            "page" to page,
            "count" to count,
            "order" to order.value
        )
        return cacheMediaResponse(route, this, dao)
    }

    //Похожее
    //https://api.redgifs.com/v2/niches/pumped-pussy/related

    suspend fun getNichesRelated(niches: String = "pumped-pussy"): NichesResponse {
        val route = Route(method = "GET", path = "/v2/niches/{niches}/related", "niches" to niches)
        return api.request<NichesResponse>(route)
    }

    //https://api.redgifs.com/v2/niches/pumped-pussy/top-creators

    suspend fun getNichesTopCreators(niches: String = "pumped-pussy"): TopCreatorsResponse {
        val route =
            Route(method = "GET", path = "/v2/niches/{niches}/top-creators", "niches" to niches)
        return api.request<TopCreatorsResponse>(route)
    }

    data class TagsContainerGson(@SerializedName("tags") val tags: List<String>)

    //https://api.redgifs.com/v2/niches/pumped-pussy/top-tags

    suspend fun getNichesTopTags(niches: String = "pumped-pussy"): List<String> {
        val route = Route(method = "GET", path = "/v2/niches/{niches}/top-tags", "niches" to niches)
        return api.request<TagsContainerGson>(route).tags
    }


    //explorer


    //////////////////////////////////// Поиск ////////////////////////////////////


    //https://api.redgifs.com/v1/creators/search?order=trending&page=1 //По умолчанию без поиска
    //Работает
    suspend fun searchCreators(
        page: Int = 1,
        order: Order = Order.TOP,
        verified: Boolean = true,
        tags: List<String>? = null// = listOf("Teen", "Ass"),
    ): CreatorsResponse {

        var url = "/v1/creators/search?page={page}&order={order}"

        if (verified) {
            url += "&verified=yes"
        }

        if (tags != null && tags.isNotEmpty()) {
            url += "&tags={tags}"
        }

        val routeParams = mutableMapOf<String, Any>(
            "page" to page, "order" to order.value
        )

        if (tags != null && tags.isNotEmpty()) {
            routeParams["tags"] = tags.joinToString(",")
        }

        val route = Route(method = "GET", path = url, *routeParams.toList().toTypedArray())
        val res: CreatorsResponse = api.request(route)
        return res

    }

    //https://api.redgifs.com/v2/search/creators?query=ana&page=1&count=40&order=trending
    suspend fun searchCreators(
        text: String = "",
        page: Int = 1,
        order: Order = Order.TRENDING,
        verified: Boolean = true,
    ): CreatorResponse {

        var url = "/v2/search/creators?query={text}&page={page}&count={count}&order={order}"

        if (verified) {
            url += "&verified=yes"
        }

        val routeParams = mutableMapOf<String, Any>(
            "text" to text,
            "page" to page,
            "order" to order.value

        )

        val route = Route(method = "GET", path = url, *routeParams.toList().toTypedArray())
        val res: CreatorResponse = api.request(route)
        return res

    }


//    //https://api.redgifs.com/v2/search/creators?query=Ana&page=1&count=40&ord
//    @Throws(ApiException::class)
//    suspend fun searchCreatorsLong(text: String, page : Int, count : Int): SearchCreatorsResponse {
//        val route = Route(method = "GET", path = "/v2/search/creators?query={text}&page={page}&count={count}&ord", "text" to text, "page" to page, "count" to count)
//        return api.request<SearchCreatorsResponse>(route)
//    }


    //https://api.redgifs.com/v2/niches/search?query=Ana
    suspend fun searchNichesShort(text: String): List<SearchItemNichesResponse> {
        val route = Route(method = "GET", path = "/v2/niches/search?query={text}", "text" to text)
        val res = api.requestText(route)
        val listType = object : TypeToken<List<SearchItemNichesResponse>>() {}.type
        val niches: List<SearchItemNichesResponse> = Gson().fromJson(res, listType)
        return niches
    }

    //SearchItemTagsResponse
    //https://api.redgifs.com/v2/search/suggest?query=Ana
    suspend fun searchTagsShort(text: String): List<SearchItemTagsResponse> {
        val route = Route(method = "GET", path = "/v2/search/suggest?query={text}", "text" to text)
        val res = api.requestText(route)
        val listType = object : TypeToken<List<SearchItemTagsResponse>>() {}.type
        val tags: List<SearchItemTagsResponse> = Gson().fromJson(res, listType)
        return tags
    }


    /**
     * ## Получить подсказки (suggest) по тегам.
     */
    suspend fun getTagSuggestions(query: String): List<TagSuggestion> {
        val route =
            Route(method = "GET", path = "/v2/search/suggest?query={query}", "query" to query)
        return api.request(route)
    }

}

private suspend fun cacheMediaResponse(
    route: Route,
    redApi: RedApi,
    dao: CacheMediaResponseDao
): MediaResponse {

    val cachedEntity = dao.get(route.url)

    val gson = GsonBuilder()
        //.registerTypeAdapter(Trace::class.java, TraceInstanceCreator())
//        .registerTypeAdapter(UInt::class.java, UIntAdapter())
//        .registerTypeAdapter(ULong::class.java, ULongAdapter())
//        .registerTypeAdapter(Long::class.java, LongAdapter())
//        .excludeFieldsWithModifiers(Modifier.ABSTRACT)
        .create()

    if (cachedEntity != null) {
        // Десериализуем JSON из кеша
        Timber.i("!!! Берем данные из кеша ${route.url}")
        return gson.fromJson(cachedEntity.content, MediaResponse::class.java)
    } else {
        Timber.i("!!! Берем данные из Сети ${route.url}")
        // Запрос из сети
        val res: MediaResponse = redApi.api.request(route)
        // Сохраняем в кеш (с текущим временем)
        val jsonContent = gson.toJson(res)
        val entity = CacheMediaResponseEntity(
            url = route.url,
            content = jsonContent,
            timeCreate = System.currentTimeMillis(),
            timeCreateText = getCurrentTimeText()
        )
        dao.insert(entity)
        return res
    }

}

