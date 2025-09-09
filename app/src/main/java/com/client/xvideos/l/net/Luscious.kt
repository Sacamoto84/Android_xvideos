package com.client.xvideos.l.net

import com.client.xvideos.l.model.Landing_page_albumType
import com.client.xvideos.l.net.graphQl.LandingPageAlbumTag
import com.client.xvideos.l.net.graphQl.refreshMediaCategories
import com.client.xvideos.l.repository.Repository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class Luscious(
    val scope : CoroutineScope,
    val repository: Repository
) {

    companion object {
        const val API = "https://members.luscious.net/graphql/nobatch/"
        const val HOME = "https://members.luscious.net"
        const val LOGIN = "https://members.luscious.net/accounts/login/"
    }

    init {
        scope.launch(Dispatchers.Main) {
            refreshMediaCategories(repository)
        }
    }

    /**
     *
     *         Return an `Album` object based on albumInput
     *
     *         albumInput can either be an integer, being the album Id
     *         Example (NSFW)<https://www.luscious.net/albums/animated-gifs_374481/>'s Id being 374481
     *         Or it can be a string, the link itself
     *
     */
    fun getAlbum(albumInput: Any, download: Boolean = false): AlbumInfo {

        val id = when (albumInput) {
            is Int -> albumInput.toString()
            is Long -> albumInput.toString()
            is String -> extractIdFromUrl(albumInput) ?: albumInput // Если URL, извлекаем ID, иначе используем как есть
            else -> throw IllegalArgumentException("albumInput must be Int or String")
        }

        return AlbumInfo(id.toInt(), download, repository, scope)
    }

    // Вспомогательная функция для извлечения ID из URL
    private fun extractIdFromUrl(url: String): String? {
        val regex = Regex("/albums/[^_]+_(\\d+)")
        val matchResult = regex.find(url)
        return matchResult?.groupValues?.get(1)
    }

    fun getAlbumList(): AlbumListImpl {
        return AlbumListImpl(repository, scope)
    }

    fun getAlbumTopHits(): AlbumTopHitsImpl {
        return AlbumTopHitsImpl(repository, scope)
    }

    suspend fun getLandingPageAlbumTag(tag : String =  "Blonde"): Result<Landing_page_albumType> {
        return LandingPageAlbumTag(tag, repository)
    }

}