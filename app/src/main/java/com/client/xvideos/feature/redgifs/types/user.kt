package com.client.xvideos.feature.redgifs.types

import java.util.Date

// Явное наследование можно опустить — CreatorInfo == UserInfo
typealias CreatorInfo = UserInfo

data class UserInfo(

    val description: String? = null,      //??
    val status: String? = null,           //?
    val poster: String? = null,            //?
    val preview: String? = null,           //?
    val thumbnail: String? = null,         //?
    val likes: Int? = null,                //?

    val creationtime: Long = 0L,           //  Описание пользователя в его профиле.                     > "Collared sub addicted to XL horse dildos"
    val followers: Int = 0,                // Количество подписчиков пользователя.                      > 68214
    val following: Int = 0,                // Количество следующих за пользователем пользователей.      > 0
    val gifs: Int = 0,                     // Общее количество опубликованных пользователем GIF-файлов. > 439
    val name: String = "",                 // Имя пользователя.                                         > "lilijunex"
    val profileImageUrl: String? = null,      // URL-адрес изображения профиля пользователя. > "https://userpic.redgifs.com/4/8c/48cc3668e114f878aafcc6dfd0a3d4f2.png"
    val profileUrl: String = "",           // URL-адрес профиля пользователя. Это URL, который отображается в профиле, установленном пользователем. Это НЕ URL пользователя на "redgifs.com" >"https://beacons.ai/lilijunex"
    val publishedCollections: Int? = null,     // Количество опубликованных пользователем коллекций.        > 0
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


data class CreatorResponse(
    val gifs: List<MediaItem> = emptyList(), //MediaItems = emptyList(), MediaItem ImageInfo
    val users: List<UserInfo> = emptyList(), //
    val niches: List<NichesInfo> = emptyList(),//
    val tags: List<String> = emptyList(),
    val page: Int = 0,    //
    val pages: Int = 0,   //
    val total: Int = 0,   //
)


data class CreatorsResponse(
    val page: Int = 0,             //
    val pages: Int = 0,            //
    val total: Int = 0,            //
    val items: List<UserInfo> = emptyList(), //
)


sealed class MediaItem

data class GifInfoItem(val gifInfo: GifInfo) : MediaItem()
data class ImageInfoItem(val imageInfo: ImageInfo) : MediaItem()


typealias MediaItems = List<MediaItem>