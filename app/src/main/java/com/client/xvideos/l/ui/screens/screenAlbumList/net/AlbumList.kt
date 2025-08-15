package com.client.xvideos.l.ui.screens.screenAlbumList.net

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.client.xvideos.l.KtorRequestHandler
import com.client.xvideos.l.Luscious
import com.client.xvideos.l.graphQl.getAlbumListQuery
import com.client.xvideos.l.model.Album
import com.client.xvideos.l.model.AlbumResponse
import com.client.xvideos.l.model.FacetCollectionInfo
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AlbumListImpl(
    val handler: KtorRequestHandler? = null,
    val scope: CoroutineScope,
) {

    var info by mutableStateOf( FacetCollectionInfo(page = -1, hasNextPage = false, hasPreviousPage = false, totalItems = -1, totalPages = -1, itemsPerPage = -1, urlComplete = "" ))
    var items  = mutableStateListOf<Album>()

   suspend fun getAlbumList(id : Int){

            val q = getAlbumListQuery(id)
            val res = handler?.postJson(Luscious.Companion.API, q)
            val gson = Gson()
            val a = gson.fromJson(res, AlbumResponse::class.java)
            withContext(Dispatchers.Main) {
                info = a.data.album.list.info
                items.addAll(a.data.album.list.items)
            }

    }
}