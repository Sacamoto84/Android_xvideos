package com.client.xvideos.feature.redgifs.types

import com.google.gson.annotations.SerializedName

data class UserInfo(
    @SerializedName("description") val description: String? = null,       // * Описание в профиле
    @SerializedName("creationtime") val creationtime: Long = 0L,           // * Описание пользователя в его профиле.                     > "Collared sub addicted to XL horse dildos"
    @SerializedName("followers") val followers: Long = 0,                // * Количество подписчиков пользователя.                      > 68214
    @SerializedName("gifs") val gifs: Long = 0,                     // * Общее количество опубликованных пользователем GIF-файлов. > 439
    @SerializedName("name") val name: String = "",                 // * Имя пользователя. Большие буквы> "lilijunex"
    @SerializedName("profileImageUrl") val profileImageUrl: String? = null,   // * URL-адрес изображения профиля пользователя. > "https://userpic.redgifs.com/4/8c/48cc3668e114f878aafcc6dfd0a3d4f2.png"
    @SerializedName("profileUrl") val profileUrl: String = "",           // * URL-адрес профиля пользователя. Это URL, который отображается в профиле, установленном пользователем. Это НЕ URL пользователя на "redgifs.com" >"https://beacons.ai/lilijunex"
    @SerializedName("publishedGifs") val publishedGifs: Long = 0,            // * Количество опубликованных публичных GIF-файлов.       > 421 (Отображается в профиле)
    @SerializedName("url") val url: String,                       // * URL-адрес пользователя на сайте ``redgifs.com``. > "https://www.redgifs.com/users/lilijunex"
    @SerializedName("username") val username: String,                  // * Имя пользователя. маленькие буквы>"lilijunex"
    @SerializedName("verified") val verified: Boolean = false,         // *
    @SerializedName("views") val views: Long  = 0L,                    // * Общее количество просмотров всех опубликованных пользователем GIF. > 123194825
)