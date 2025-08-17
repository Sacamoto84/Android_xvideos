package com.client.xvideos.l.net

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import com.client.xvideos.l.KtorRequestHandler
import com.client.xvideos.l.model.Album
import com.client.xvideos.l.model.AlbumList
import com.client.xvideos.l.model.AlbumListTopHits
import com.client.xvideos.l.net.graphQl.getAlbumListTopHitsQuery
import com.google.common.reflect.TypeToken
import com.google.gson.Gson
import com.google.gson.JsonParser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

class AlbumTopHitsImpl(
    val handler: KtorRequestHandler? = null,
    val scope: CoroutineScope,
) {

    var items = mutableStateListOf<AlbumListTopHits>()

    init {
        scope.launch {
            Timber.i("!!! getAlbumTopHits")
            val q = getAlbumListTopHitsQuery()
            val res = handler?.postJson(Luscious.Companion.API, q)
            val json = JsonParser.parseString(res).asJsonObject
            val get =
                json["data"]?.asJsonObject?.get("album")?.asJsonObject?.get("list_top_hits")?.asJsonArray
            val gson = Gson()
            val listType = object : TypeToken<List<AlbumListTopHits>>() {}.type
            val list: List<AlbumListTopHits> = gson.fromJson(get, listType)
            withContext(Dispatchers.Main) {
                items.clear()
                items.addAll(list)
            }
        }

    }

}
