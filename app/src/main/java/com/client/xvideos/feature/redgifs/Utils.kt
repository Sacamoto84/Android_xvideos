package com.client.xvideos.feature.redgifs

fun toWebUrl(idOrUrl: String, useRegex: Boolean = false): String {
    if (!useRegex) return "https://redgifs.com/watch/${idOrUrl.lowercase()}"
    val regex = Regex("""thumbs\.redgifs\.com/[^/]+/(?<id>[^/]+)-mobile\.mp4""")
    val match = regex.find(idOrUrl)
    return match?.groups?.get("id")?.value?.let {
        "https://redgifs.com/watch/${it.lowercase()}"
    } ?: ""
}


fun buildFileUrl(url: String): String {
    val filename = url.substringAfterLast("/").replace("-mobile.mp4", "")
    return "https://api.redgifs.com/v2/gifs/${filename.lowercase()}/files/${filename}.mp4"
}

fun toEmbedUrl(sdUrl: String): String {
    val filename = sdUrl.substringAfterLast("/").replace("-mobile.mp4", ".mp4")
    return "https://api.redgifs.com/v2/embed/discord?name=$filename"
}

