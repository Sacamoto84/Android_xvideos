package com.client.xvideos.xvideos.model

data class GalleryItem(
    val id : Long,                    // - Номер не используем
    val title : String,               // - Название видео(Зависит от выбранного языка)
    val duration : String,            // * Длинна видео (11 мин.)
    val views : String,               // - Количество просмотров
    val channel : String,             // * Отображаемое название канала (Old4k)
    val previewImage : String = "",   // * Путь до картинки превью
    val previewVideo : String = "",   // * Путь до видео превью

    val href: String,                 // * Путь до страницы видео (Для открытия в экране плеера) Только оно и нужно для этого

    val nameProfile: String,          // - Отображаемое название профиля (TODO)
    val linkProfile: String,          // * Путь до профиля путь к каналу (/old4k)
)