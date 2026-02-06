package com.client.xvideos.redgifs.common.saved

import com.client.xvideos.common.di.ApplicationScope
import com.client.xvideos.redgifs.model.tag.TagInfo
import com.client.xvideos.redgifs.network.api.RedApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SavedRed @Inject constructor(
    val redApi: RedApi,
    @ApplicationScope val scope : CoroutineScope
) {

    /////////////////////////////////////////////////////////////////////////////////////////////
    val likes       = SavedRed_Likes()
    val creators    = SavedRed_Creator()
    val niches      = SavedRed_Niches()
    val collections = SavedRed_Collection()
    val nichesCache = SavedRed_NichesCaches(scope, redApi)

    var tagsList = listOf<TagInfo>()

    @OptIn(DelicateCoroutinesApi::class)
    fun refreshTagList() { scope.launch(Dispatchers.IO) { tagsList =
        redApi.tags.getTags().getOrNull()?.tags ?: emptyList()
    } }

}



