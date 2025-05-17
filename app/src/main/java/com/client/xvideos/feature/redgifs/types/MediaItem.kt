package com.client.xvideos.feature.redgifs.types

sealed class MediaItem

data class GifInfoItem(val gifInfo: GifInfo) : MediaItem()
data class ImageInfoItem(val imageInfo: ImageInfo) : MediaItem()

typealias MediaItems = List<MediaItem>