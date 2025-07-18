package com.redgifs.common.saved

import com.client.common.di.ApplicationScope
import com.redgifs.common.snackBar.SnackBarEvent
import com.redgifs.model.tag.TagInfo
import com.redgifs.network.api.RedApi
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
    fun refreshTagList() { GlobalScope.launch(Dispatchers.IO) { tagsList = redApi.tags.getTags().tags } }

}



