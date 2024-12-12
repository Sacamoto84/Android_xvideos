package com.client.xvideos.model






data class GalleryItem(
    val id : Long,
    val title : String,               //Название видео
    val duration : String,            //Длинна видео
    val views : String,               //Количество просмотров
    val channel : String,
    val previewImage : String = "",   //Путь до картинки превью
    val previewVideo : String = "",   //Путь до видео превью

    val href: String,                 //Путь до страницы видео

    val nameProfile: String,          //Отображаемое название профиля
    val linkProfile: String,          //Путь до профиля

)