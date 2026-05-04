package com.client.xvideos.l.net

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.setValue
import com.client.xvideos.l.model.PicsDetails
import com.client.xvideos.l.model.lBestThumbnailImageUrl
import com.client.xvideos.l.net.graphQl.GraphQlRequest
import com.client.xvideos.l.repository.Repository
import com.client.xvideos.l.repository.RepositoryUriConfig
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
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
        const val PAGE_REQUEST_DELAY_MS = 250L
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

        val cachedError = cached.exceptionOrNull()
        if (cachedError.isHtmlChallengeResponse()) {
            Timber.w(cachedError, "!!! AlbumPicsDetails $id page $page HTML challenge response")
            return Result.failure(cachedError ?: IllegalStateException("Server returned HTML instead of JSON"))
        }

        Timber.w(cachedError, "!!! AlbumPicsDetails $id page $page CACHE_ROM error, retry DIRECT")
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
            delay(PAGE_REQUEST_DELAY_MS)
        }

        withContext(Dispatchers.Main) {
            percentLoad = 1f
        }
    }

    private suspend fun appendPage(page: PageLoadResult, pages: Int) {
        val corrected = normalizePictureUrls(page.items)
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

    private fun Throwable?.isHtmlChallengeResponse(): Boolean {
        val message = this?.message ?: return false
        return message.startsWith("Server returned HTML instead of JSON")
    }

    private fun normalizePictureUrls(l: List<PicsDetails>): List<PicsDetails> {
        return l.map { item ->
            val thumbnailUrl = item.lBestThumbnailImageUrl()
            if (!thumbnailUrl.isNullOrBlank()) {
                item.copy(url_to_original = thumbnailUrl)
            } else {
                item
            }
        }
    }

}
