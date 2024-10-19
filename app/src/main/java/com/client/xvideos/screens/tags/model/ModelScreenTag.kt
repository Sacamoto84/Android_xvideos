package com.client.xvideos.screens.tags.model

data class ModelScreenTag(val title0: String, val title1: String, val items: List<ModelTagItem>)

data class ModelTagItem(
    val title: String,       //Название видео
    val href: String,        //Путь до страницы видео
    val duration: String,    //Длинна видео
    val views: String,       //Количество просмотров
    val nameProfile: String, //Отображаемое название профиля
    val linkProfile: String, //Путь до профиля
)
