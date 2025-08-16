package com.client.xvideos.l.net

import com.client.xvideos.l.KtorRequestHandler
import com.client.xvideos.l.model.AlbumTopHitsResponse
import com.client.xvideos.l.net.graphQl.getAlbumListTopHitsQuery
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import timber.log.Timber

class AlbumTopHitsImpl(
    val handler: KtorRequestHandler? = null,
    val scope: CoroutineScope,
) {

    suspend fun getAlbumTopHits() {
        Timber.i("!!! getAlbumTopHits")
        val q = getAlbumListTopHitsQuery()
        val res = handler?.postJson(Luscious.Companion.API, q)
        val gson = Gson()
        val serverResponse = gson.fromJson(res, AlbumTopHitsResponse::class.java)
        val list = serverResponse.data.album.list_top_hits
        list
//        val a = gson.fromJson(res, AlbumResponse::class.java)
//        withContext(Dispatchers.Main) {
//            info = a.data.album.list.info
//            items.clear()
//            items.addAll(a.data.album.list.items)
//            Timber.i("!!! getAlbumList info ${info.page} ${items.toList()}")
//        }

    }

}
