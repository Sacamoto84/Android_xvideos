package com.client.xvideos.l.net.graphQl

import com.client.xvideos.l.net.Luscious
import com.client.xvideos.l.repository.Repository
import com.google.gson.JsonParser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import timber.log.Timber

class LandingPageAlbumTag(
    val repository: Repository,
    val scope: CoroutineScope,
    val tag: String
) {

    init {
        scope.launch {
            try {
                Timber.i("!!! LandingPageAlbumTag init")

                val q = getLandingPageAlbumTag(tag)
                val res = repository.openURI(Luscious.Companion.API, q)

                val json = JsonParser.parseString(res.getOrThrow()).asJsonObject
                val get = json["data"]?.asJsonObject?.get("landing_page_album")?.asJsonObject?.get("tag")?.asJsonObject

                val title = get?.get("title")?.asString
                val sections = get?.get("sections")?.asJsonArray
                title
                sections
                res
            } catch (e: Exception) {
                Timber.i("!!! eee LandingPageAlbumTag Exception $e")
            }
        }

    }
}