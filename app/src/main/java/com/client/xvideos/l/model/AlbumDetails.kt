package com.client.xvideos.l.model

import com.google.gson.annotations.SerializedName

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


data class Content(
    @SerializedName("id") val id: String,
    @SerializedName("title") val title: String,
    @SerializedName("url") val url: String
)