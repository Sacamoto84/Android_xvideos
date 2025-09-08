package com.client.xvideos.l.net.graphQl

import com.client.xvideos.l.net.Luscious
import com.client.xvideos.l.repository.Repository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import timber.log.Timber

class LandingPageAlbumTag(
    val repository: Repository,
    val scope: CoroutineScope,
    val tag : String
) {


    init {
        scope.launch {
            Timber.i("!!! getAlbumTopHits")

            val q = getLandingPageAlbumTag(tag)
            val res = repository.openURI(Luscious.Companion.API, q)

            res
        }
    }


}