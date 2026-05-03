package com.client.xvideos.l.net

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.setValue
import com.client.xvideos.l.model.PicsDetails
import com.client.xvideos.l.net.graphQl.GraphQlRequest
import com.client.xvideos.l.repository.Repository
import com.client.xvideos.l.repository.RepositoryUriConfig
import com.google.gson.Gson
import com.google.gson.JsonObject
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

    private data class PageLoadResult(
        val page: Int,
        val totalPages: Int,
        val items: List<PicsDetails>
    )

    private suspend fun openPage(page: Int): Result<PageLoadResult> {
        val request = GraphQlRequest.pictureListInsideAlbum(id, page)
        val cached = repository.openURI(
            request,
            config = RepositoryUriConfig.CACHE_ROM
        ).mapCatching { parsePage(page, it) }

        if (cached.isSuccess) return cached

        Timber.w(cached.exceptionOrNull(), "!!! AlbumPicsDetails $id page $page CACHE_ROM error, retry DIRECT")
        repository.deleteCache(request, RepositoryUriConfig.CACHE_ROM)

        return repository.openURI(
            request,
            config = RepositoryUriConfig.DIRECT
        ).mapCatching { parsePage(page, it) }
    }

    private fun parsePage(page: Int, response: String): PageLoadResult {
        val list = mutableListOf<PicsDetails>()

        val json = JsonParser.parseString(response).asJsonObject
        val get = json["data"]
            ?.asJsonObjectOrNull()
            ?.get("picture")
            ?.asJsonObjectOrNull()
            ?.get("list")
            ?.asJsonObjectOrNull()
            ?: error("AlbumPicsDetails response missing data.picture.list")

        get["errors"]
            ?.takeIf { !it.isJsonNull }
            ?.let { error("AlbumPicsDetails response errors: ${it.toString().take(300)}") }

        val info = get["info"]?.asJsonObjectOrNull()
        val pages = info.readInt("total_pages")?.coerceAtLeast(1) ?: 1

        val itemsArray = get["items"]?.takeIf { it.isJsonArray }?.asJsonArray
            ?: error("AlbumPicsDetails response missing data.picture.list.items")

        itemsArray.forEachIndexed { index, element ->
            runCatching {
                gson.fromJson(element, PicsDetails::class.java)
            }.onSuccess { pic ->
                if (pic != null && pic.hasAnyMediaUrl()) {
                    list.add(pic)
                } else {
                    Timber.w("!!! AlbumPicsDetails $id page $page item $index has no media urls")
                }
            }.onFailure {
                Timber.w(it, "!!! AlbumPicsDetails $id page $page item $index parse error")
            }
        }

        return PageLoadResult(page, pages, list)
    }

    suspend fun contentUrls() = withContext(Dispatchers.Default) {
        withContext(Dispatchers.Main) {
            pics.clear()
            totalPages = null
            percentLoad = 0f
        }

        val firstPage = openPage(1).getOrElse {
            Timber.w(it, "!!! AlbumPicsDetails $id page 1 error")
            withContext(Dispatchers.Main) {
                percentLoad = 1f
            }
            return@withContext
        }

        val pages = firstPage.totalPages
        appendPage(firstPage, pages)

        for (page in 2..pages) {
            val pageResult = openPage(page).getOrElse {
                Timber.w(it, "!!! AlbumPicsDetails $id page $page error")
                PageLoadResult(page, pages, emptyList())
            }
            appendPage(pageResult, pages)
        }

        withContext(Dispatchers.Main) {
            percentLoad = 1f
        }
    }

    private suspend fun appendPage(page: PageLoadResult, pages: Int) {
        val corrected = correctionPictureUrl(page.items)
        withContext(Dispatchers.Main) {
            totalPages = pages
            percentLoad = page.page.toFloat() / pages
            pics.addAll(corrected)
        }
    }

    private fun PicsDetails.hasAnyMediaUrl(): Boolean {
        return !url_to_original.isNullOrBlank() ||
                !url_to_video.isNullOrBlank() ||
                thumbnails?.any { !it.url.isNullOrBlank() } == true
    }

    private fun com.google.gson.JsonElement.asJsonObjectOrNull(): JsonObject? {
        return takeIf { it.isJsonObject }?.asJsonObject
    }

    private fun JsonObject?.readInt(name: String): Int? {
        return runCatching {
            this?.get(name)?.takeIf { !it.isJsonNull }?.asInt
        }.getOrNull()
    }

    private fun correctionPictureUrl(l: List<PicsDetails>): List<PicsDetails> {
        val res = mutableListOf<PicsDetails>()

        l.forEach { item ->
            val thumbUrl = item.thumbnails?.firstOrNull()?.url

            if (thumbUrl != null) {
                runCatching {
                    val originalUrl = item.url_to_original?.takeIf { it.isNotBlank() }
                        ?: urlToOriginal(thumbUrl)
                    item.copy(url_to_original = originalUrl)
                }.onSuccess {
                    res.add(it)
                }.onFailure {
                    Timber.w(it, "!!! AlbumPicsDetails $id correction url error")
                    res.add(item)
                }
            } else {
                res.add(item)
            }
        }

        return res
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
