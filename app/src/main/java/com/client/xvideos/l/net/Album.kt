package com.client.xvideos.l.net

import com.client.xvideos.l.KtorRequestHandler
import com.client.xvideos.l.model.AlbumDetails
import com.client.xvideos.l.model.Audience
import com.client.xvideos.l.model.Content
import com.client.xvideos.l.model.Cover
import com.client.xvideos.l.model.Genre
import com.client.xvideos.l.model.Tag
import com.client.xvideos.l.net.graphQl.getAlbumInfo
import com.google.gson.Gson
import com.google.gson.JsonParser
import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class Album(
    val id: Int,
    download: Boolean = false,
    val handler: KtorRequestHandler? = null,
    scope: CoroutineScope,
) {

    var url: String = ""

    val albumPicsDetails = AlbumPicsDetails(id,  handler)

    val parsed = MutableStateFlow(
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
        scope.launch {
            val res = handler?.postJsonCached(Luscious.Companion.API, getAlbumInfo(id))
            val json = JsonParser.parseString(res).asJsonObject
            val get = json["data"]?.asJsonObject?.get("album")?.asJsonObject?.get("get")?.asJsonObject
            val gson = Gson()
            parsed.value = gson.fromJson(get, AlbumDetails::class.java)
            url = Luscious.Companion.HOME + parsed.value.url
            albumPicsDetails.contentUrls()
        }
    }



    /**
     * Возвращает url миниатюры альбома
     */
    val thumbnail: String by lazy { parsed.value.cover.url }

    val downloadUrl: String by lazy { Luscious.Companion.HOME + parsed.value.download_url }


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


