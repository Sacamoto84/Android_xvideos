package com.client.xvideos.l.net

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.setValue
import com.client.xvideos.l.model.PicsDetails
import com.client.xvideos.l.model.ThumbnailsSize
import com.client.xvideos.l.net.graphQl.GraphQlRequest
import com.client.xvideos.l.repository.Repository
import com.client.xvideos.l.repository.RepositoryUriConfig
import com.google.gson.Gson
import com.google.gson.JsonParser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

/**
 * Информация о картинках по id альбома
 */
class AlbumPicsDetails(
    val id: Int,
    val repository: Repository
) {

    private companion object {
        val gson = Gson()
        val thumbnailRegex = Regex("""\.\d+x\d+(?=\.\w+$)""")
    }

    val pics = mutableStateListOf<PicsDetails>()

    var totalPages: Int? = null

    var percentLoad by mutableFloatStateOf(0f)

    private suspend fun openPage(page: Int): List<PicsDetails> {
        val list = mutableListOf<PicsDetails>()
        val result = repository.openURI(
            GraphQlRequest.pictureListInsideAlbum(id, page),
            config = RepositoryUriConfig.CACHE_ROM
        )
        if (result.isFailure) return list
        val picsJson = result.getOrNull()
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

    suspend fun contentUrls() = withContext(Dispatchers.Default) {
        try {
            val page1 = openPage(1)
            val l1 = correctionPictureUrl(page1)

            withContext(Dispatchers.Main) {
                pics.addAll(l1)
                totalPages?.let {
                    if (it > 0) percentLoad = 1.0f / it
                }
            }

            val pages = totalPages ?: 0
            for (i in 2..pages) {
                val pageI = openPage(i)
                val lI = correctionPictureUrl(pageI)
                withContext(Dispatchers.Main) {
                    percentLoad = i.toFloat() / pages
                    pics.addAll(lI)
                }
            }
        } catch (e: Exception) {
            // handle exception if needed
        }
    }


    fun correctionPictureUrl(l: List<PicsDetails>): List<PicsDetails> {

        val res = mutableListOf<PicsDetails>()

        try {

            l.forEach {

                val item = it

                val thumbUrl = item.thumbnails?.firstOrNull()?.url

                if (thumbUrl != null) {
                    val b = urlToOriginal(thumbUrl) as String?
                    val a = item.copy(url_to_original = b)
                    res.add(a)
                } else {
                    res.add(item)
                }

            }
        }
        catch (e: Exception)
        {
            Timber.e(e)
            return res.toList()
        }

        return res.toList()

    }


    //https://cdni.luscious.net/venividivici2k13/603323/millie_beachside_dem_01KHBSB2THB9YFJCQT22P9NGCS.640x0.jpg?md5=sn0bj1zYPF7ziGsGKnGRQA&expires=1773900756
    fun urlToOriginal(str: String): String {
        // 1. Убираем всё после ?
        val withoutQuery = str.substringBefore('?')

        // 2. Убираем .ЧИСЛОxЧИСЛО перед .jpg / .png и т.п.
        //    Работает для .640x0, .1280x1920, .0x0 и подобных
        return withoutQuery.replace(thumbnailRegex, "")
    }


}