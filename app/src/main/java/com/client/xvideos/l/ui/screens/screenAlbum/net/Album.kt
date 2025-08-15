package com.client.xvideos.l.ui.screens.screenAlbum.net

import com.client.xvideos.l.KtorRequestHandler
import com.client.xvideos.l.Luscious
import com.client.xvideos.l.graphQl.getAlbumInfo
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


data class PicsDetails(
    @SerializedName("height") val height: Int, //"846"
    @SerializedName("width") val width: Int, //"1280"
    @SerializedName("is_animated") val is_animated: Boolean,
    @SerializedName("url_to_original") val url_to_original: String?,
    @SerializedName("url_to_video") val url_to_video: String?,
)


data class AlbumDetails(
    @SerializedName("created") val created: Long,
    @SerializedName("id") val id: String,
    @SerializedName("title") val title: String,
    @SerializedName("tags") val tags: List<Tag>,
    @SerializedName("is_manga") val is_manga: Boolean,
    @SerializedName("content") val content: Content,
    @SerializedName("genres") val genres: List<Genre>,
    @SerializedName("cover") val cover: Cover,
    @SerializedName("description") val description: String,            //Возвращает описание альбома
    @SerializedName("audiences") val audiences: List<Audience>,
    @SerializedName("number_of_pictures") val number_of_pictures: Int, //Возвращает количество фотографий в альбоме (в это число входят и gif-файлы).
    @SerializedName("number_of_animated_pictures") val number_of_animated_pictures: Int, //
    @SerializedName("url") val url: String,
    @SerializedName("download_url") val download_url: String
)

data class Tag(
    @SerializedName("id") val id: String,
    @SerializedName("category") val category: String?,
    @SerializedName("text") val text: String,
    @SerializedName("url") val url: String,
    @SerializedName("count") val count: Long
)

data class Content(
    @SerializedName("id") val id: String,
    @SerializedName("title") val title: String,
    @SerializedName("url") val url: String
)

data class Genre(
    @SerializedName("id") val id: String,
    @SerializedName("title") val title: String,
    @SerializedName("acts_as_warning") val acts_as_warning: Boolean,
    @SerializedName("url") val url: String
)

data class Cover(
    @SerializedName("width") val width: Int,
    @SerializedName("height") val height: Int,
    @SerializedName("size") val size: String,
    @SerializedName("url") val url: String
)

data class Audience(
    @SerializedName("id") val id: String,
    @SerializedName("title") val title: String,
    @SerializedName("url") val url: String
)
