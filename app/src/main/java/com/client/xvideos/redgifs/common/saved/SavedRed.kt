package com.client.xvideos.redgifs.common.saved

import com.client.xvideos.common.di.ApplicationScope
import com.client.xvideos.common.snackBar.SnackBarEvent
import com.client.xvideos.redgifs.model.tag.TagInfo
import com.client.xvideos.redgifs.network.api.RedApi
import com.redgifs.common.saved.SavedRed_Collection
import com.redgifs.common.saved.SavedRed_Creator
import com.redgifs.common.saved.SavedRed_Likes
import com.redgifs.common.saved.SavedRed_Niches
import com.redgifs.common.saved.SavedRed_NichesCaches
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SavedRed @Inject constructor(
    val redApi: RedApi,
    snackBarEvent : SnackBarEvent,
    @ApplicationScope val scope : CoroutineScope
) {

    /////////////////////////////////////////////////////////////////////////////////////////////
    val likes       = SavedRed_Likes(snackBarEvent)
    val creators    = SavedRed_Creator(snackBarEvent)
    val niches      = SavedRed_Niches(snackBarEvent)
    val collections = SavedRed_Collection(snackBarEvent)
    val nichesCache = SavedRed_NichesCaches(scope, redApi, snackBarEvent)

    var tagsList = listOf<TagInfo>()

    @OptIn(DelicateCoroutinesApi::class)
    fun refreshTagList() { GlobalScope.launch(Dispatchers.IO) { tagsList =
        redApi.tags.getTags().getOrNull()?.tags ?: emptyList()
    } }

}



