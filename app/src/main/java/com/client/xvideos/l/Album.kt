package com.client.xvideos.l

import androidx.compose.runtime.mutableStateListOf
import com.google.gson.Gson
import com.google.gson.JsonParser
import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

class Album(
    val id: Int,
    download: Boolean = false,
    val handler: KtorRequestHandler? = null,
    scope : CoroutineScope,
) {
    //private val handler: KtorRequestHandler = handler ?: KtorRequestHandler()

    var url: String = ""

    lateinit var parsed: AlbumDetails

    init {
        scope.launch {
            val info = getAlbumInfo(id)
            val res = handler?.postJson(Luscious.API, info)
            val json = JsonParser.parseString(res).asJsonObject
            val get = json["data"]?.asJsonObject?.get("album")?.asJsonObject?.get("get")?.asJsonObject
            val gson = Gson()
            parsed = gson.fromJson(get, AlbumDetails::class.java)
            url = Luscious.HOME + parsed.url
            contentUrls()
        }
    }

    /**
     * Возвращает количество фотографий в альбоме (в это число входят и gif-файлы).
     */
    val pictureCount: Int by lazy { parsed.number_of_pictures }

    /**
     * Возвращает количество анимированных картинок в альбоме
     */
    val animatedCount: Int by lazy { parsed.number_of_animated_pictures }

    /**
     * Возвращает url миниатюры альбома
     */
    val thumbnail: String by lazy { parsed.cover.url }

    /**
     * Возвращает описание альбома
     */
    val description: String by lazy { parsed.description }


    val downloadUrl: String by lazy { Luscious.HOME + parsed.download_url }


    val pics = mutableStateListOf<PicsDetails>()
    var total_pages: Int? = null

    suspend fun contentUrls() {

            val  time = System.currentTimeMillis()
            try {
                val list = mutableListOf<PicsDetails>()
                list.addAll(openPage(1))
                for (i in 2..total_pages!!) {
                    list.addAll(openPage(i))
                }
                withContext(Dispatchers.Main) {
                    pics.addAll(list)
                }
            }catch (e: Exception){
                val ee = e.message
            }
            val  timeA = System.currentTimeMillis() - time
            println("Time: $timeA")
    }

    suspend fun openPage(page: Int): List<PicsDetails> {
        val gson = Gson()
        val list = mutableListOf<PicsDetails>()
        val picsJson = handler?.postJson(Luscious.API, getPicturesJson(id, page))
        val json = JsonParser.parseString(picsJson).asJsonObject
        val get = json["data"]?.asJsonObject?.get("picture")?.asJsonObject?.get("list")?.asJsonObject
        total_pages = get?.get("info")?.asJsonObject?.get("total_pages")?.asInt
        val itemsArray = get?.get("items")?.asJsonArray
        itemsArray?.forEach { element ->
            val pic = gson.fromJson(element, PicsDetails::class.java)
            list.add(pic)
        }
        return list
    }



//
//    val genres: List<Genre> by lazy {
//        (json["genres"] as List<Map<String, Any>>).map {
//            Genre(it["id"] as String, it["title"] as String, it["url"] as String)
//        }
//    }

//    val tags: List<Tag> by lazy {
//        (json["tags"] as List<Map<String, Any>>).map {
//            Tag(it["id"] as String, it["text"] as String, it["category"] as String?, it["url"] as String)
//        }
//    }

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

//    fun downloadContent(root: Path = Paths.get("Albums"), printProgress: Boolean = true): List<Path> {
//        val downloadedPaths = mutableListOf<Path>()
//        val albumRoot = root.resolve(sanitizeFilePath(sanitizedName))
//        if (!albumRoot.exists()) albumRoot.createDirectories()
//
//        contentUrls.forEachIndexed { index, urlStr ->
//            val fileName = if (isManga) {
//                "$sanitizedName_${index.toString().padStart(pictureCount.toString().length, '0')}"
//            } else {
//                Paths.get(URL(urlStr).path).fileName.toString()
//            }
//            val filePath = albumRoot.resolve(fileName)
//
//            if (filePath.exists()) {
//                if (printProgress) println("$fileName already exists")
//                downloadedPaths.add(filePath)
//                return@forEachIndexed
//            }
//
//            try {
//                val response = handler.get(urlStr)
//                val extension = mimeTypeToExtension(response.contentType)
//                val fullPath = filePath.resolveSibling("$fileName$extension")
//                Files.write(fullPath, response.body)
//                if (printProgress) println("$fileName downloaded")
//                downloadedPaths.add(fullPath)
//            } catch (e: Exception) {
//                if (printProgress) println("$fileName skipped due to $e")
//                val skippedPath = filePath.resolveSibling("${fileName}_SKIPPED")
//                Files.createFile(skippedPath)
//                downloadedPaths.add(filePath)
//            }
//        }
//        return downloadedPaths
//    }

//    // Дополнительные функции-заглушки, реализацию под ваш RequestHandler и JSON-парсинг надо добавить:
//    private fun getAlbumInfoJson(id: Int): Map<String, Any> = TODO()
//    private fun getPicturesJson(id: Int, page: Int = 1): Map<String, Any> = TODO()
//    private fun mimeTypeToExtension(mime: String?): String = when (mime) {
//        "image/jpeg" -> ".jpg"
//        "image/png" -> ".png"
//        "image/gif" -> ".gif"
//        else -> ""
//    }
//    private fun sanitizeFilePath(input: String): String {
//        return input.replace(Regex("[^a-zA-Z0-9 _-]"), "")
//    }
}


data class PicsDetails(
    @SerializedName("url_to_original") val url_to_original: String?,
    @SerializedName("url_to_video") val url_to_video: String?,
)


data class AlbumDetails(
    @SerializedName("id") val id: String,
    @SerializedName("title") val title: String,
    @SerializedName("tags") val tags: List<Tag>,
    @SerializedName("is_manga") val is_manga: Boolean,
    @SerializedName("content") val content: Content,
    @SerializedName("genres") val genres: List<Genre>,
    @SerializedName("cover") val cover: Cover,
    @SerializedName("description") val description: String,
    @SerializedName("audiences") val audiences: List<Audience>,
    @SerializedName("number_of_pictures") val number_of_pictures: Int,
    @SerializedName("number_of_animated_pictures") val number_of_animated_pictures: Int,
    @SerializedName("url") val url: String,
    @SerializedName("download_url") val download_url: String
)

data class Tag(
    @SerializedName("id") val id: String,
    @SerializedName("category") val category: String?,
    @SerializedName("text") val text: String,
    @SerializedName("url") val url: String,
    @SerializedName("count") val count: Int
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
