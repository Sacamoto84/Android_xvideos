package com.redgifs.network

// Результат предложения тега.
data class TagSuggestion(
    val name: String = "",    //Название тега
    val count: Int = 0        //Количество GIF-файлов с этим тегом
)