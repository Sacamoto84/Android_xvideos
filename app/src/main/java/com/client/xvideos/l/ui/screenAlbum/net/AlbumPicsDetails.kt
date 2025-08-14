package com.client.xvideos.l.ui.screenAlbum.net

import androidx.compose.runtime.mutableStateListOf
import com.client.xvideos.l.KtorRequestHandler
import com.client.xvideos.l.Luscious
import com.client.xvideos.l.graphQl.getPicturesJson
import com.google.gson.Gson
import com.google.gson.JsonParser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AlbumPicsDetails(
    val id: Int,
    val handler: KtorRequestHandler? = null
) {

    val pics = mutableStateListOf<PicsDetails>()

    var total_pages: Int? = null

    suspend fun openPage(page: Int): List<PicsDetails> {
        val gson = Gson()
        val list = mutableListOf<PicsDetails>()
        val picsJson = handler?.postJson(Luscious.Companion.API, getPicturesJson(id, page))
        val json = JsonParser.parseString(picsJson).asJsonObject
        val get = json["data"]?.asJsonObject?.get("picture")?.asJsonObject?.get("list")?.asJsonObject
        total_pages = get?.get("info")?.asJsonObject?.get("total_pages")?.asInt
        val itemsArray = get?.get("items")?.asJsonArray
        itemsArray?.forEach { element ->
            val pic = gson.fromJson(element, PicsDetails::class.java)
            list.add(pic)
        }
        return list
    }

    suspend fun contentUrls() {
        try {
            val list = mutableListOf<PicsDetails>()
            list.addAll(openPage(1))
            for (i in 2..total_pages!!) {
                list.addAll(openPage(i))
            }
            withContext(Dispatchers.Main) {
                pics.addAll(list)
            }
        } catch (e: Exception) {
            val ee = e.message
        }
    }



}