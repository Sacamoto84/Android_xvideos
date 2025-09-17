package com.client.xvideos.l.net

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.client.xvideos.l.model.Album
import com.client.xvideos.l.model.AlbumListFilter
import com.client.xvideos.l.model.AlbumResponse
import com.client.xvideos.l.model.FacetCollectionInfo
import com.client.xvideos.l.net.graphQl.getAlbumListGraphQL1
import com.client.xvideos.l.net.graphQl.getAlbumListWithAggregations
import com.client.xvideos.l.repository.Repository
import com.client.xvideos.l.repository.RepositoryUriConfig
import com.google.gson.Gson
import com.google.gson.JsonParser
import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

data class AlbumListFilterGenreCountResponse(
    @SerializedName("count")
    val count: Int,

    @SerializedName("term")
    val term: String,

    @SerializedName("is_active")
    val isActive: Boolean
)

data class AlbumListFilterGenreCountResponseList(
    @SerializedName("list")
    val list: List<AlbumListFilterGenreCountResponse>
)


data class AlbumListImplInfoAndList(
    val info: FacetCollectionInfo = FacetCollectionInfo(
        page = 1,
        hasNextPage = true,
        hasPreviousPage = true,
        totalItems = 1,
        totalPages = 1,
        itemsPerPage = 1,
        urlComplete = ""
    ),
    val items: List<Album> = emptyList(),
    val filter: AlbumListFilter = AlbumListFilter(),
    val page: Int = 0
)


data class getAlbumListAggregationsResult(
    val filterGenreStateCount: List<AlbumListFilterGenreCountResponse>,
    val filterTaggedStateCount: List<AlbumListFilterGenreCountResponse>,
    val filterPictureCountStateCount: List<AlbumListFilterGenreCountResponse>,
    val id: Int,
    val filter: AlbumListFilter?
)





//    var info by mutableStateOf(
//        FacetCollectionInfo(
//            page = 1,
//            hasNextPage = false,
//            hasPreviousPage = false,
//            totalItems = 0,
//            totalPages = 1,
//            itemsPerPage = 30,
//            urlComplete = ""
//        )
//    )


    suspend fun getAlbumListAggregationsImpl(page: Int, filterIn: AlbumListFilter?, repository: Repository): Result<getAlbumListAggregationsResult> {

        val filterGenreStateCount = mutableListOf<AlbumListFilterGenreCountResponse>()
        val filterTaggedStateCount = mutableListOf<AlbumListFilterGenreCountResponse>()
        val filterPictureCountStateCount = mutableListOf<AlbumListFilterGenreCountResponse>()

        val filter = filterIn ?: AlbumListFilter()

        try {
            Timber.i("!!! getAlbumListAggregations $page")

            val q = getAlbumListWithAggregations(page, filter)

            //Timber.i("!!! getAlbumListAggregations $q")

            val result = repository.openURI(Luscious.Companion.API, q)
            if (result.isFailure) {
                Timber.i("!!! getAlbumListAggregations error ${result.exceptionOrNull()}")
                return Result.failure(result.exceptionOrNull()!!)
            }

            val res = result.getOrThrow()
            val json = JsonParser.parseString(res).asJsonObject
            val get =
                json["data"]?.asJsonObject?.get("album")?.asJsonObject?.get("list_with_aggregations")?.asJsonObject
            val activeFilters = get?.get("active_filters")?.asJsonArray
            val aggregations = get?.get("aggregations")?.asJsonArray


            ////
            val indexGenre = aggregations?.mapIndexedNotNull { i, el ->
                val obj = el.asJsonObject
                val shortName = obj.getAsJsonObject("field")?.get("short_name")?.asString
                if (shortName == "genre_ids") i else null
            }?.firstOrNull()

            if (indexGenre != null) {
                val genreValues =
                    aggregations.get(indexGenre)?.getAsJsonObject()?.get("values")?.asJsonArray
                val gson = Gson()
                val list = mutableListOf<AlbumListFilterGenreCountResponse>()
                genreValues?.forEach { element ->
                    val pic = gson.fromJson(element, AlbumListFilterGenreCountResponse::class.java)
                    list.add(pic)
                }

                filterGenreStateCount.addAll(list)
                Timber.i("!!! getAlbumListAggregations list размер : ${list.size}")
            }

            ////
            val indexTagged = aggregations?.mapIndexedNotNull { i, el ->
                val obj = el.asJsonObject
                val shortName = obj.getAsJsonObject("field")?.get("short_name")?.asString
                if (shortName == "tagged") i else null
            }?.firstOrNull()

            if (indexTagged != null) {
                val taggedValues =
                    aggregations.get(indexTagged)?.getAsJsonObject()?.get("values")?.asJsonArray
                val gson = Gson()
                val list = mutableListOf<AlbumListFilterGenreCountResponse>()
                taggedValues?.forEach { element ->
                    val pic = gson.fromJson(element, AlbumListFilterGenreCountResponse::class.java)
                    list.add(pic)
                }
                withContext(Dispatchers.Main) {
                    filterTaggedStateCount.addAll(list)
                    Timber.i(
                        "!!! getAlbumListAggregations list Tagged размер : ${list.size} ${
                            list.joinToString(
                                "\n"
                            ) { it.term }
                        }")
                }
            }
            ///
            val indexPicture = aggregations?.mapIndexedNotNull { i, el ->
                val obj = el.asJsonObject
                val shortName = obj.getAsJsonObject("field")?.get("short_name")?.asString
                if (shortName == "picture_count_rank") i else null
            }
                ?.firstOrNull()

            if (indexPicture != null) {
                val pictureValues = aggregations.get(indexPicture)?.getAsJsonObject()?.get("values")?.asJsonArray
                val gson = Gson()
                val list = mutableListOf<AlbumListFilterGenreCountResponse>()
                pictureValues?.forEach { element ->
                    val pic = gson.fromJson(element, AlbumListFilterGenreCountResponse::class.java)
                    list.add(pic)
                }

                filterPictureCountStateCount.addAll(list)
                Timber.i("!!! getAlbumListAggregations list filterPictureCountStateCount размер : ${list.size}")

            }

            filterPictureCountStateCount
        } catch (e: Exception) {
            Timber.i("!!! getAlbumListAggregations Exception $e")
            e.printStackTrace()
            return Result.failure(e)

        }

        return Result.success(
            getAlbumListAggregationsResult(
                filterGenreStateCount,
                filterTaggedStateCount,
                filterPictureCountStateCount,
                page,
                filter
            )
        )

    }


    /**
     * Получить список альбомов с учетом фильтра
     */
    suspend fun getAlbumListImpl(
        page: Int,
        filterIn: AlbumListFilter?,
        repository: Repository,
    ): Result<AlbumListImplInfoAndList>
    {
        val items = mutableListOf<Album>()
        try {
            Timber.i("!!! getAlbumList $page")
            val filter = filterIn ?: AlbumListFilter()
            val q = getAlbumListGraphQL1(page, filter)

            val result = repository.openURI( Luscious.Companion.API,  q, config = RepositoryUriConfig.CACHE_RAM )

            if (result.isFailure) {
                Timber.e("!!! getAlbumList error ${result.exceptionOrNull()}")
                return Result.failure(result.exceptionOrNull()!!)
            }
            val res = result.getOrThrow()
            val gson = Gson()
            val a = gson.fromJson(res, AlbumResponse::class.java)
            val info = a.data.album.list.info
            items.addAll(a.data.album.list.items)
            //Timber.i("!!! getAlbumList info ${info.page} ${items.toList()}")
            return Result.success(
                AlbumListImplInfoAndList(
                    info = info,
                    items = items,
                    filter = filter,
                    page = page
                )
            )
        } catch (e: Exception) {
            Timber.i("!!! getAlbumList Exception ${e.localizedMessage}")
            e.printStackTrace()
            return Result.failure(e)
        }
    }
