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
    val handler: KtorRequestHandler? = null,
    val scope: CoroutineScope,
) {

    var filter by mutableStateOf(AlbumListFilter())

    val filterGenreStateCount = mutableStateListOf<AlbumListFilterGenreCountResponse>()


    var info by mutableStateOf(
        FacetCollectionInfo(
            page = -1,
            hasNextPage = false,
            hasPreviousPage = false,
            totalItems = -1,
            totalPages = -1,
            itemsPerPage = -1,
            urlComplete = ""
        )
    )
    var items = mutableStateListOf<Album>()


    suspend fun getAlbumListAggregations(id: Int) {

        Timber.i("!!! getAlbumListAggregations $id")
        val q = getAlbumListWithAggregations(id, filter)
        val res = handler?.postJson(Luscious.Companion.API, q)
        val json = JsonParser.parseString(res).asJsonObject
        val get =
            json["data"]?.asJsonObject?.get("album")?.asJsonObject?.get("list_with_aggregations")?.asJsonObject
        val activeFilters = get?.get("active_filters")?.asJsonArray
        val aggregations = get?.get("aggregations")?.asJsonArray
        aggregations


        val indexGenre = aggregations?.mapIndexedNotNull { i, el ->
            val obj = el.asJsonObject
            val shortName = obj.getAsJsonObject("field")?.get("short_name")?.asString
            if (shortName == "genre_ids") i else null
        }
            ?.firstOrNull()

        if (indexGenre!= null) {
            val genreValues = aggregations.get(indexGenre)?.getAsJsonObject()?.get("values")?.asJsonArray
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
        }
        else
            withContext(Dispatchers.Main) {
                filterGenreStateCount.clear()
            }

    }


    suspend fun getAlbumList(id: Int) {

        Timber.i("!!! getAlbumList $id")
        //val q = getAlbumListGraphQL(id)

        val q = getAlbumListGraphQL1(id, filter)

        val res = handler?.postJsonCachedRam(Luscious.Companion.API, q)
        val gson = Gson()
        val a = gson.fromJson(res, AlbumResponse::class.java)
        withContext(Dispatchers.Main) {
            info = a.data.album.list.info
            items.clear()
            items.addAll(a.data.album.list.items)
            Timber.i("!!! getAlbumList info ${info.page} ${items.toList()}")
        }

    }
}