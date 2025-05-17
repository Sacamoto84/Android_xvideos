package com.client.xvideos.feature.redgifs.types

data class UserInfo(
    val description: String? = null,       // * Описание в профиле
    val creationtime: Long = 0L,           // * Описание пользователя в его профиле.                     > "Collared sub addicted to XL horse dildos"
    val followers: Int = 0,                // * Количество подписчиков пользователя.                      > 68214
    val gifs: Int = 0,                     // * Общее количество опубликованных пользователем GIF-файлов. > 439
    val name: String = "",                 // * Имя пользователя.                                         > "lilijunex"
    val profileImageUrl: String? = null,   // * URL-адрес изображения профиля пользователя. > "https://userpic.redgifs.com/4/8c/48cc3668e114f878aafcc6dfd0a3d4f2.png"
    val profileUrl: String = "",           // * URL-адрес профиля пользователя. Это URL, который отображается в профиле, установленном пользователем. Это НЕ URL пользователя на "redgifs.com" >"https://beacons.ai/lilijunex"
    val publishedGifs: Int = 0,            // * Количество опубликованных публичных GIF-файлов.       > 421 (Отображается в профиле)
    val url: String,                       // * URL-адрес пользователя на сайте ``redgifs.com``. > "https://www.redgifs.com/users/lilijunex"
    val username: String,                  // * Имя пользователя. >"lilijunex"
    val verified: Boolean = false,         // *
    val views: Int = 0,                    // * Общее количество просмотров всех опубликованных пользователем GIF. > 123194825
)