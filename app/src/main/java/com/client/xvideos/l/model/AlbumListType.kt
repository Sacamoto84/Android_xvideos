package com.client.xvideos.l.model

import com.google.gson.annotations.SerializedName

data class AlbumListTopHits(
    @SerializedName("title")
    val title: String,

    @SerializedName("url")
    val url: String,

    @SerializedName("count")
    val count: Int,

    @SerializedName("item_type")
    val itemType: String,

    @SerializedName("items")
    val items: List<Album>
)

// Корневой класс для JSON
data class AlbumResponse(
    @SerializedName("data")
    val data: AlbumData
)

data class AlbumData(
    @SerializedName("album")
    val album: AlbumListWrapper
)

data class AlbumListWrapper(
    @SerializedName("list")
    val list: AlbumList
)

data class AlbumList(
    @SerializedName("info")
    val info: FacetCollectionInfo,
    @SerializedName("items")
    val items: List<Album>
)

data class FacetCollectionInfo(
    @SerializedName("page")
    val page: Int,
    @SerializedName("has_next_page")
    val hasNextPage: Boolean,
    @SerializedName("has_previous_page")
    val hasPreviousPage: Boolean,
    @SerializedName("total_items")
    val totalItems: Int,
    @SerializedName("total_pages")
    val totalPages: Int,
    @SerializedName("items_per_page")
    val itemsPerPage: Int,
    @SerializedName("url_complete")
    val urlComplete: String
)

data class Album(
    @SerializedName("__typename")          val typeName: String,
    @SerializedName("id")                  val id: String,
    @SerializedName("title")               val title: String,
    @SerializedName("description")         val description: String,
    @SerializedName("created")             val created: Long,
    @SerializedName("modified")            val modified: Long,
    @SerializedName("like_status")         val likeStatus: String,
    @SerializedName("moderation_status")   val moderationStatus: String,
    @SerializedName("number_of_favorites") val numberOfFavorites: Int,
    @SerializedName("number_of_dislikes")  val numberOfDislikes: Int,
    @SerializedName("number_of_pictures")  val numberOfPictures: Int,
    @SerializedName("number_of_animated_pictures")    val numberOfAnimatedPictures: Int,
    @SerializedName("number_of_duplicates")           val numberOfDuplicates: Int,
    @SerializedName("slug")         val slug: String,
    @SerializedName("is_manga")     val isManga: Boolean,
    @SerializedName("url")          val url: String,
    @SerializedName("download_url") val downloadUrl: String,
    @SerializedName("labels")       val labels: List<String>,
    @SerializedName("permissions")  val permissions: List<String>,
    @SerializedName("cover")        val cover: Cover,
    @SerializedName("language")     val language: Language?,
    @SerializedName("created_by")   val createdBy: User,
    @SerializedName("tags")         val tags: List<Tag>,
    @SerializedName("genres")       val genres: List<Genre>
)

data class Cover(
    @SerializedName("width")  val width: Int,
    @SerializedName("height") val height: Int,
    @SerializedName("size")   val size: String,
    @SerializedName("url")    val url: String
)

data class Language(
    @SerializedName("id")
    val id: String,
    @SerializedName("title")
    val title: String,
    @SerializedName("url")
    val url: String
)

data class User(
    @SerializedName("id")
    val id: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("display_name")
    val displayName: String,
    @SerializedName("url")
    val url: String
)

data class Tag(
    @SerializedName("id")       val id: String,
    @SerializedName("category") val category: String?,
    @SerializedName("text")     val text: String,
    @SerializedName("url")      val url: String,
    @SerializedName("count")    val count: Int
)

data class Genre(
    @SerializedName("id")              val id: String,
    @SerializedName("title")           val title: String,
    @SerializedName("acts_as_warning") val actsAsWarning: Boolean,
    @SerializedName("url")             val url: String
)

data class Audience(
    @SerializedName("id") val id: String,
    @SerializedName("title") val title: String,
    @SerializedName("url") val url: String
)
