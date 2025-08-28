package com.client.xvideos.l.net

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.setValue
import com.client.xvideos.l.KtorRequestHandler
import com.client.xvideos.l.model.PicsDetails
import com.client.xvideos.l.net.graphQl.getPicturesJson
import com.client.xvideos.l.repository.Repository
import com.client.xvideos.l.repository.RepositoryUriConfig
import com.google.gson.Gson
import com.google.gson.JsonParser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AlbumPicsDetails(
    val id: Int,
    val repository: Repository
) {

    val pics = mutableStateListOf<PicsDetails>()

    var totalPages: Int? = null

    var percentLoad by mutableFloatStateOf(0f)

    suspend fun openPage(page: Int): List<PicsDetails> {
        val gson = Gson()
        val list = mutableListOf<PicsDetails>()

        val result = repository.openURI(Luscious.Companion.API, getPicturesJson(id, page), config = RepositoryUriConfig.CACHE_ROM)
        if (result.isFailure) return list
        val picsJson = result.getOrNull()
        //val picsJson = handler?.postJsonCached(Luscious.Companion.API, getPicturesJson(id, page))
        val json = JsonParser.parseString(picsJson).asJsonObject
        val get = json["data"]?.asJsonObject?.get("picture")?.asJsonObject?.get("list")?.asJsonObject
        totalPages = get?.get("info")?.asJsonObject?.get("total_pages")?.asInt
        val itemsArray = get?.get("items")?.asJsonArray
        itemsArray?.forEach { element ->
            val pic = gson.fromJson(element, PicsDetails::class.java)
            list.add(pic)
        }
        return list
    }

    suspend fun contentUrls() {
        try {
            val l = openPage(1)
            withContext(Dispatchers.Main) {
                pics.addAll(l)
                percentLoad = 1.0f/totalPages!!
            }
            for (i in 2..totalPages!!) {
                val l = openPage(i)
                withContext(Dispatchers.Main) {
                    percentLoad = i.toFloat()/totalPages!!
                    pics.addAll(l)
                }
            }
        } catch (e: Exception) {
            val ee = e.message
        }
    }



}