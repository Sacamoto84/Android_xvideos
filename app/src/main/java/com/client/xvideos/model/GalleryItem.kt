package com.client.xvideos.model

data class GalleryItem(
    val id : Long,
    val title : String,
    val link : String,
    val duration : String,
    val views : String,
    val channel : String,
    val previewImage : String = "",
    val previewVideo : String = ""
)