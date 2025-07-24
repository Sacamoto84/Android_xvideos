package com.client.xvideos.l

import com.google.gson.Gson
import com.google.gson.JsonParser
import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.Serializer

class isAlbum(
    val id: Int,
    download: Boolean = false,
    handler: KtorRequestHandler? = null
) {
    //private val handler: KtorRequestHandler = handler ?: KtorRequestHandler()

    var url: String = ""

    var parsed: AlbumDetails

    init {
        runBlocking {
            val info = getAlbumInfo(id)
            val res = handler?.postJson(Luscious.API, info)
            val json = JsonParser.parseString(res).asJsonObject
            val get = json["data"]?.asJsonObject?.get("album")?.asJsonObject?.get("get")?.asJsonObject
            val gson = Gson()
            parsed = gson.fromJson(get, AlbumDetails::class.java)
            url = Luscious.HOME + parsed.url
            url
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
    val thumbnail: String by lazy {  parsed.cover.url }

    /**
     * Возвращает описание альбома
     */
    val description: String by lazy { parsed.description }


    val downloadUrl: String by lazy { Luscious.HOME + parsed.download_url }

//    override fun toString(): String = name
//
//    val name: String by lazy { json["title"] as String }
//
//    val sanitizedName: String by lazy { sanitizeFilePath(name) }
//
//    val downloadUrl: String by lazy { Luscious.HOME + (json["download_url"] as? String ?: "") }

//    val contentUrls: List<String> by lazy {
//        val picsJson = handler.post(Luscious.API, getPicturesJson(id))
//            .getJSONObject("data")
//            .getJSONObject("picture")
//            .getJSONObject("list")
//        val totalPages = picsJson.getJSONObject("info").getInt("total_pages")
//        val urls = mutableListOf<String>()
//        urls.addAll(picsJson.getJSONArray("items").map { it.asJsonObject.getString("url_to_original") })
//
//        for (page in 2..totalPages) {
//            val pagePics = handler.post(Luscious.API, getPicturesJson(id, page))
//                .getJSONObject("data")
//                .getJSONObject("picture")
//                .getJSONObject("list")
//            urls.addAll(pagePics.getJSONArray("items").map { it.asJsonObject.getString("url_to_original") })
//        }
//        urls
//    }


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



data class AlbumDetails(
    @SerializedName("id")    val id: String,
    @SerializedName("title")    val title: String,
    @SerializedName("tags")    val tags: List<Tag>,
    @SerializedName("is_manga")    val is_manga: Boolean,
    @SerializedName("content")   val content: Content,
    @SerializedName("genres")    val genres: List<Genre>,
    @SerializedName("cover")  val cover: Cover,
    @SerializedName("description")   val description: String,
    @SerializedName("audiences")   val audiences: List<Audience>,
    @SerializedName("number_of_pictures")  val number_of_pictures: Int,
    @SerializedName("number_of_animated_pictures")  val number_of_animated_pictures: Int,
    @SerializedName("url")   val url: String,
    @SerializedName("download_url")   val download_url: String
)

data class Tag(
    @SerializedName("id") val id: String,
    @SerializedName("category") val category: String?,
    @SerializedName("text") val text: String,
    @SerializedName("url") val url: String,
    @SerializedName("count") val count: Int
)

data class Content(
    @SerializedName("id")  val id: String,
    @SerializedName("title") val title: String,
    @SerializedName("url")  val url: String
)

data class Genre(
    @SerializedName("id")  val id: String,
    @SerializedName("title")  val title: String,
    @SerializedName("acts_as_warning")  val acts_as_warning: Boolean,
    @SerializedName("url")  val url: String
)

data class Cover(
    @SerializedName("width")  val width: Int,
    @SerializedName("height") val height: Int,
    @SerializedName("size") val size: String,
    @SerializedName("url")val url: String
)

data class Audience(
    @SerializedName("id") val id: String,
    @SerializedName("title") val title: String,
    @SerializedName("url") val url: String
)
