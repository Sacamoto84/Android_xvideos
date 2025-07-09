package com.client.xvideos.redgifs.common.saved

import com.redgifs.model.tag.TagInfo
import com.redgifs.network.api.RedApi
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SavedRed @Inject constructor(
    val redApi: RedApi
) {

    /////////////////////////////////////////////////////////////////////////////////////////////
    val likes       = SavedRed_Likes()
    val creators    = SavedRed_Creator()
    val niches      = SavedRed_Niches()
    val collections = SavedRed_Collection()

    var tagsList = listOf<TagInfo>()

    @OptIn(DelicateCoroutinesApi::class)
    fun refreshTagList() { GlobalScope.launch(Dispatchers.IO) { tagsList = redApi.tags.getTags().tags } }

}



