package com.client.xvideos.l.net

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.client.xvideos.l.KtorRequestHandler
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

class AlbumListImpl(
    val repository: Repository,
    val scope: CoroutineScope,
) {

    var filter by mutableStateOf(AlbumListFilter())

    val filterGenreStateCount = mutableStateListOf<AlbumListFilterGenreCountResponse>()

    val filterTaggedStateCount = mutableStateListOf<AlbumListFilterGenreCountResponse>()

    val filterPictureCountStateCount = mutableStateListOf<AlbumListFilterGenreCountResponse>()


    var info by mutableStateOf(
        FacetCollectionInfo(
            page = 1,
            hasNextPage = false,
            hasPreviousPage = false,
            totalItems = 0,
            totalPages = 1,
            itemsPerPage = 30,
            urlComplete = ""
        )
    )
    var items = mutableStateListOf<Album>()


    suspend fun getAlbumListAggregations(id: Int) {

        try {
            Timber.i("!!! getAlbumListAggregations $id")

            val q = getAlbumListWithAggregations(id, filter)

            val result = repository.openURI(Luscious.Companion.API, q)
            if (result.isFailure) {
                Timber.i("!!! getAlbumListAggregations error ${result.exceptionOrNull()}")
                return
            }
            val res = result.getOrThrow()
            val json = JsonParser.parseString(res).asJsonObject
            val get = json["data"]?.asJsonObject?.get("album")?.asJsonObject?.get("list_with_aggregations")?.asJsonObject
            val activeFilters = get?.get("active_filters")?.asJsonArray
            val aggregations = get?.get("aggregations")?.asJsonArray


            ////
            val indexGenre = aggregations?.mapIndexedNotNull { i, el ->
                val obj = el.asJsonObject
                val shortName = obj.getAsJsonObject("field")?.get("short_name")?.asString
                if (shortName == "genre_ids") i else null
            } ?.firstOrNull()

            if (indexGenre != null) {
                val genreValues =
                    aggregations.get(indexGenre)?.getAsJsonObject()?.get("values")?.asJsonArray
                val gson = Gson()
                val list = mutableListOf<AlbumListFilterGenreCountResponse>()
                genreValues?.forEach { element ->
                    val pic = gson.fromJson(element, AlbumListFilterGenreCountResponse::class.java)
                    list.add(pic)
                }
                withContext(Dispatchers.Main) {
                    filterGenreStateCount.clear()
                    filterGenreStateCount.addAll(list)
                    Timber.i("!!! getAlbumListAggregations list размер : ${list.size}")
                }
            } else {
                withContext(Dispatchers.Main) {
                    filterGenreStateCount.clear()
                }
            }
            ////
            val indexTagged = aggregations?.mapIndexedNotNull { i, el ->
                val obj = el.asJsonObject
                val shortName = obj.getAsJsonObject("field")?.get("short_name")?.asString
                if (shortName == "tagged") i else null
            } ?.firstOrNull()

            if (indexTagged != null) {
                val taggedValues = aggregations.get(indexTagged)?.getAsJsonObject()?.get("values")?.asJsonArray
                val gson = Gson()
                val list = mutableListOf<AlbumListFilterGenreCountResponse>()
                taggedValues?.forEach { element ->
                    val pic = gson.fromJson(element, AlbumListFilterGenreCountResponse::class.java)
                    list.add(pic)
                }
                withContext(Dispatchers.Main) {
                    filterTaggedStateCount.clear()
                    filterTaggedStateCount.addAll(list)
                    Timber.i("!!! getAlbumListAggregations list Tagged размер : ${list.size}")
                }
            } else {
                withContext(Dispatchers.Main) {
                    filterTaggedStateCount.clear()
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
                val pictureValues =
                    aggregations.get(indexPicture)?.getAsJsonObject()?.get("values")?.asJsonArray
                val gson = Gson()
                val list = mutableListOf<AlbumListFilterGenreCountResponse>()
                pictureValues?.forEach { element ->
                    val pic = gson.fromJson(element, AlbumListFilterGenreCountResponse::class.java)
                    list.add(pic)
                }
                withContext(Dispatchers.Main) {
                    filterPictureCountStateCount.clear()
                    filterPictureCountStateCount.addAll(list)
                    Timber.i("!!! getAlbumListAggregations list filterPictureCountStateCount размер : ${list.size}")
                }
            } else {
                withContext(Dispatchers.Main) {
                    filterPictureCountStateCount.clear()
                }
            }

            filterPictureCountStateCount
        } catch (e: Exception) {
            Timber.i("!!! getAlbumListAggregations Exception $e")
        }

    }


    /**
     * Получить список альбомов с учетом фильтра
     */
    suspend fun getAlbumList(id: Int, filterIn : AlbumListFilter?) {

        try {

            Timber.i("!!! getAlbumList $id")
            withContext(Dispatchers.Main) {
                items.clear()
            }

            filter = filterIn ?: AlbumListFilter()

            val q = getAlbumListGraphQL1(id, filter)

            val result = repository.openURI( Luscious.Companion.API, q, config = RepositoryUriConfig.CACHE_RAM )
            if (result.isFailure) {
                Timber.e("!!! getAlbumList error ${result.exceptionOrNull()}")
                return
            }
            val res = result.getOrNull()
            val gson = Gson()

            val a = gson.fromJson(res, AlbumResponse::class.java)

            withContext(Dispatchers.Main) {
                info = a.data.album.list.info
                items.clear()
                items.addAll(a.data.album.list.items)
                Timber.i("!!! getAlbumList info ${info.page} ${items.toList()}")
            }
        } catch (e: Exception) {
            Timber.i("!!! getAlbumList Exception ${e.localizedMessage}")
            e.printStackTrace()
        }
    }
}