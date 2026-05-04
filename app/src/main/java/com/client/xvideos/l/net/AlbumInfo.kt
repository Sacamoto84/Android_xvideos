package com.client.xvideos.l.net

import com.client.xvideos.l.model.AlbumDetails
import com.client.xvideos.l.model.Content
import com.client.xvideos.l.model.Cover
import com.client.xvideos.l.net.graphQl.getAlbumInfo
import com.client.xvideos.l.repository.Repository
import com.client.xvideos.l.repository.RepositoryUriConfig
import com.google.gson.Gson
import com.google.gson.JsonParser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber

class AlbumInfo(
    val id: Int,
    download: Boolean = false,
    repository: Repository,
    scope: CoroutineScope,
) {

    private companion object {
        val gson = Gson()
    }

    val albumPicsDetails = AlbumPicsDetails(id,  repository)

    val albumInfo = MutableStateFlow(
        AlbumDetails(
            id = "",
            title = "",
            tags = listOf(),
            is_manga = false,
            content = Content("", "", ""),
            genres = listOf(),
            cover = Cover(0, 0, "", ""),
            description = "",
            audiences = listOf(),
            number_of_pictures = 0,
            number_of_animated_pictures = 0,
            url = "",
            download_url = "",
            created = 0L
        )
    )

    init {
        scope.launch(Dispatchers.IO) {
            val query = getAlbumInfo(id)
            val result = repository.openURI(query, config = RepositoryUriConfig.CACHE_ROM)
            if (result.isFailure) {
                Timber.w("!!! getAlbumInfo $id error: ${result.exceptionOrNull()?.message}")
                return@launch
            }
            var parsed = parseAlbumDetails(result.getOrThrow())
            if (parsed.isFailure) {
                Timber.w("!!! getAlbumInfo $id CACHE_ROM parse error, retry DIRECT: ${parsed.exceptionOrNull()?.message}")
                repository.deleteCache(query, RepositoryUriConfig.CACHE_ROM)
                val directResult = repository.openURI(query, config = RepositoryUriConfig.DIRECT)
                if (directResult.isFailure) {
                    Timber.w("!!! getAlbumInfo $id DIRECT error: ${directResult.exceptionOrNull()?.message}")
                    return@launch
                }
                parsed = parseAlbumDetails(directResult.getOrThrow())
            }

            if (parsed.isFailure) {
                Timber.w("!!! getAlbumInfo $id parse error: ${parsed.exceptionOrNull()?.message}")
                return@launch
            }

            albumInfo.value = parsed.getOrThrow()
            //url = Luscious.HOME + albumInfo.value.url
            albumPicsDetails.contentUrls()
        }
    }

    private fun parseAlbumDetails(response: String): Result<AlbumDetails> = runCatching {
        val json = JsonParser.parseString(response).asJsonObject
        val get = json["data"]
            ?.asJsonObject
            ?.get("album")
            ?.asJsonObject
            ?.get("get")
            ?.asJsonObject
            ?: error("AlbumInfo response missing data.album.get")
        gson.fromJson(get, AlbumDetails::class.java)
            ?: error("AlbumInfo response data.album.get is empty")
    }

    /**
     * Возвращает url миниатюры альбома
     */
    val thumbnail: String by lazy { albumInfo.value.cover.url }

    val downloadUrl: String by lazy { Luscious.Companion.HOME + albumInfo.value.download_url }

//    val artists: List<String> by lazy {
//        tags.filter { it.category == "Artist" }.map { it.name }
//    }
//
//    val characters: List<String> by lazy {
//        tags.filter { it.category == "Character" }.map { it.name }
//    }
//
//    val parodies: List<String> by lazy {
//        tags.filter { it.category == "Parody" }.map { it.name }
//    }

//    val audiences: Map<String, Any> by lazy {
//        json["audiences"] as Map<String, Any>
//    }
////
////    val ongoing: Boolean by lazy {
////        tags.any { it.id == "1895669" && it.text == "ongoing" }
////    }
//
//    val isManga: Boolean by lazy {
//        json["is_manga"] as Boolean
//    }
//
//    val contentType: String by lazy {
//        (json["content"] as Map<*, *>)["title"] as String
//    }

}


