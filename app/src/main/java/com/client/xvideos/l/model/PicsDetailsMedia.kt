package com.client.xvideos.l.model

fun PicsDetails.lAnimationVideoUrl(): String? {
    if (!is_animated) return null
    return url_to_video?.takeIf { it.isNotBlank() }
        ?: url_to_original?.takeIf { it.isLVideoFileUrl() }
}

fun PicsDetails.lDownloadUrl(): String? {
    return if (is_animated) {
        lAnimationVideoUrl() ?: url_to_original?.takeIf { it.isNotBlank() }
    } else {
        url_to_original?.takeIf { it.isNotBlank() }
    }
}

fun PicsDetails.lPreviewImageUrl(thumbnailsSize: String): String {
    return thumbnails
        ?.firstOrNull { it.size == thumbnailsSize }
        ?.url
        ?.takeIf { it.isNotBlank() }
        ?: url_to_original.orEmpty()
}

fun PicsDetails.lSavedFileName(): String? {
    val sourceName = lDownloadUrl()?.lUrlFileName()?.takeIf { it.isNotBlank() } ?: return null
    return "${width}_${height}_${is_animated}_${album}_$sourceName"
}

fun String.isLVideoFileUrl(): Boolean {
    val path = substringBefore('?').substringBefore('#')
    return path.endsWith(".mp4", ignoreCase = true) ||
            path.endsWith(".webm", ignoreCase = true) ||
            path.endsWith(".m3u8", ignoreCase = true) ||
            path.endsWith(".m4v", ignoreCase = true) ||
            path.endsWith(".mov", ignoreCase = true)
}

fun String.lUrlFileName(): String {
    return substringBefore('?').substringBefore('#').substringAfterLast('/')
}

fun String.lUrlExtension(): String {
    return lUrlFileName().substringAfterLast('.', missingDelimiterValue = "")
}
