package com.client.xvideos.l

//data class Genre(
//    val id: String,
//    val title: String,
//    val url: String
//) {
//    // name возвращает часть title после двоеточия, с заглавной буквы
//    val name: String by lazy {
//        title.split(":").last().trim().replaceFirstChar { it.uppercase() }
//    }
//
//    // fullName — просто title
//    val fullName: String by lazy {
//        title
//    }
//
//    // sanitizedName — "очищенное" имя, аналог sanitize_filepath (здесь пример простой замены)
//    val sanitizedName: String by lazy {
//        sanitizeFilePath(name)
//    }
//
//    // hashtag — формат #Tag_Name
//    val hashtag: String by lazy {
//        "#" + sanitizedName.replace(' ', '_').replace('-', '_')
//    }
//
//    override fun toString(): String = name
//
//    // Пример простой реализации sanitize_filepath, можно заменить на более точную
//    private fun sanitizeFilePath(input: String): String {
//        // Убираем символы, не подходящие для имени файла (простейший пример)
//        return input.replace(Regex("[^a-zA-Z0-9 _-]"), "")
//    }
//}
