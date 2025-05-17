package com.client.xvideos.feature.redgifs

import com.client.xvideos.feature.redgifs.ApiClient.request
import com.client.xvideos.feature.redgifs.ApiClient.requestText
import com.client.xvideos.feature.redgifs.HTTP.Route
import com.client.xvideos.feature.redgifs.types.CreatorResponse
import com.client.xvideos.feature.redgifs.types.GifInfo
import com.client.xvideos.feature.redgifs.types.GifInfoItem
import com.client.xvideos.feature.redgifs.types.ImageInfo
import com.client.xvideos.feature.redgifs.types.ImageInfoItem
import com.client.xvideos.feature.redgifs.types.MediaItem
import com.client.xvideos.feature.redgifs.types.TagInfo
import com.client.xvideos.feature.redgifs.types.TagsResponse
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
import com.google.gson.TypeAdapter
import com.google.gson.TypeAdapterFactory
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter

object API {

    suspend fun getTags(): List<TagInfo> {
        val response: TagsResponse = request("https://api.redgifs.com/v2/tags")
        return response.tags
    }


    /**
     * Найдите одного создателя/пользователя RedGifs по имени пользователя.
     */
    suspend fun search_creator(
        username: String,                // Имя пользователя/создателя.
        page: Int = 1,                   // Номер текущей страницы профиля создателя/пользователя.
        count: Int = 80,                 // Общее количество GIF-файлов для возврата.
        order: Order = Order.RECENT,     // Порядок возврата GIF-файлов создателя/пользователя.
        type: MediaType = MediaType.GIF, // Возвращать ли результаты в виде изображений или GIF. По умолчанию возвращаются GIF-файлы.
    ): CreatorResponse {
        val route = Route(
            "GET",
            "/v2/users/{username}/search?page={page}&count={count}&order={order}&type={type}",
            "username" to username,
            "page" to page,
            "count" to count,
            "order" to order.value,
            "type" to type.value
        )
        val res : CreatorResponse  = request(route)
        //val gson = GsonBuilder()
        //    .registerTypeAdapterFactory(MediaItemTypeAdapterFactory())
         //   .create()
        //val i = gson.fromJson(res, CreatorResponse::class.java)
        return res
    }







}

class MediaItemTypeAdapter : TypeAdapter<MediaItem>() {

    private val gson = Gson()

    override fun write(out: JsonWriter, value: MediaItem?) { }

    override fun read(reader: JsonReader): MediaItem {

        val jsonElement = JsonParser.parseReader(reader).asJsonObject
        val type = jsonElement.get("type").asInt

        return when (type) {
            1 -> gson.fromJson(jsonElement, ImageInfo::class.java).let { ImageInfoItem(it) }
            2 -> gson.fromJson(jsonElement, GifInfo::class.java).let { GifInfoItem(it) }
            else -> throw IllegalArgumentException("Unknown type: $type")
        }
    }

}

//class MediaItemTypeAdapterFactory : TypeAdapterFactory {
//    override fun <T> create(gson: Gson, type: TypeToken<T>): TypeAdapter<T>? {
//        return if (type.rawType == MediaItem::class.java) {
//            MediaItemTypeAdapter() as TypeAdapter<T>
//        } else {
//            null
//        }
//    }
//}