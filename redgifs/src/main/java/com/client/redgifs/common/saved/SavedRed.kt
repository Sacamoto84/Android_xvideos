package com.client.redgifs.common.saved

import com.client.redgifs.network.api.RedApi_Tags
import com.client.redgifs.network.types.tag.TagInfo
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

object SavedRed {

    /////////////////////////////////////////////////////////////////////////////////////////////
    val likes       = SavedRed_Likes()
    val creators    = SavedRed_Creator()
    val niches      = SavedRed_Niches()
    val collections = SavedRed_Collection()

    var tagsList = listOf<TagInfo>()

    @OptIn(DelicateCoroutinesApi::class)
    fun refreshTagList() { GlobalScope.launch(Dispatchers.IO) { tagsList = RedApi_Tags.getTags().tags } }

}



