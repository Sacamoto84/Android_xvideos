package com.client.xvideos.feature.redgifs

import java.util.Date

enum class Order(val value: String) {
    TRENDING("trending"),
    TOP("top"),
    LATEST("latest"),
    OLDEST("oldest"),
    RECENT("recent"),
    BEST("best"),
    TOP28("top28"),
    NEW("new");

    companion object {
        private val deprecatedNames = setOf(
            "trending", "top", "latest", "oldest", "recent", "best", "top28", "new"
        )

        fun from(name: String): Order? {
            val upperCaseName = name.uppercase()
            return try {
                if (name in deprecatedNames && name != upperCaseName) {
                    @Suppress("DEPRECATION")
                    println("WARNING: 'Order.$name' is deprecated, use 'Order.${upperCaseName}' instead.")
                }
                valueOf(upperCaseName)
            } catch (e: IllegalArgumentException) {
                null
            }
        }
    }
}

enum class MediaType(val value: String) {
    IMAGE("i"),
    GIF("g");

    companion object {
        fun fromValue(value: String): MediaType? {
            return entries.find { it.value == value }
        }
    }
}

data class User(

//    val status: String?,
//
//    val poster: String?,
//    val preview: String?,
//    val thumbnail: String?,
//    val likes: Int?,

    val creationTime: Long = Date().time,  //  Описание пользователя в его профиле.                     > "Collared sub addicted to XL horse dildos"
    val followers: Int = 0,                // Количество подписчиков пользователя.                      > 68214
    val following: Int = 0,                // Количество следующих за пользователем пользователей.      > 0
    val gifs: Int = 0,                     // Общее количество опубликованных пользователем GIF-файлов. > 439
    val name: String = "",                 // Имя пользователя.                                         > "lilijunex"
    val profileImageUrl: String = "",      // URL-адрес изображения профиля пользователя. > "https://userpic.redgifs.com/4/8c/48cc3668e114f878aafcc6dfd0a3d4f2.png"
    val profileUrl: String = "",           // URL-адрес профиля пользователя. Это URL, который отображается в профиле, установленном пользователем. Это НЕ URL пользователя на "redgifs.com" >"https://beacons.ai/lilijunex"
    val publishedCollections: Int = 0,     // Количество опубликованных пользователем коллекций.        > 0
    val publishedGifs: Int = 0,            // Количество опубликованных пользователем GIF-файлов.       > 421
    val socialUrl1: String? = null,        //
    val socialUrl2: String? = null,        //
    val socialUrl3: String? = null,        //
    val socialUrl4: String? = null,        //
    val socialUrl5: String? = null,        //
    val socialUrl6: String? = null,        //
    val socialUrl7: String? = null,        //
    val socialUrl8: String? = null,        //
    val socialUrl9: String? = null,        //
    val socialUrl10: String? = null,       //
    val socialUrl11: String? = null,       //
    val socialUrl12: String? = null,       //
    val socialUrl13: String? = null,       //
    val socialUrl14: String? = null,       //
    val socialUrl15: String? = null,       //
    val socialUrl16: String? = null,       //
    val socialUrl17: String? = null,       //
    val studio: Boolean = false,           //
    val subscription: Int = 0,             //
    val url: String,                       //  URL-адрес пользователя на сайте ``redgifs.com``. > "https://www.redgifs.com/users/lilijunex"
    val username: String,                  // Имя пользователя. >"lilijunex"
    val verified: Boolean = false,         //
    val views: Int = 0,                    // Общее количество просмотров всех опубликованных пользователем GIF. > 123194825
)

// Явное наследование можно опустить — CreatorInfo == UserInfo
typealias CreatorInfo = User


data class CreatorResponse(
    val gifs: List<MediaInfo>,
    val users: List<User>, //
    val niches: List<NichesInfo>,//
    val tags: List<String>,
    val page: Int,    //
    val pages: Int,   //
    val total: Int,   //
)

data class NichesInfo(
    val cover: String,
    val description: String,
    val gifs: Int,
    val id: String,
    val name: String,
    val owner: String,
    val subscribers: Int,
    val thumbnail: String,
)

sealed interface MediaInfo

data class GifInfo(
    val id: String,
    val url: String,
    // другие поля...
) : MediaInfo

data class ImageInfo(
    val id: String,
    val url: String,
    // другие поля...
) : MediaInfo
