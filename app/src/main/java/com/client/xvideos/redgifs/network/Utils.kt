package com.client.xvideos.redgifs.network

import java.net.URI
import kotlin.text.get

fun to_web_url(idOrUrl: String, useRegex: Boolean = false): String {
    if (!useRegex) {
        return "https://redgifs.com/watch/${idOrUrl.lowercase()}"
    }

    val match = REDGIFS_THUMBS_RE.matchEntire(idOrUrl) ?: return ""

    return try {
        val id = match.groups["id"]?.value ?: return ""
        "https://redgifs.com/watch/${id.lowercase()}"
    } catch (e: Exception) {
        ""
    }
}

fun build_file_url(url: String): String {
    val uri = URI(url)
    val filename = uri.path.replace("/", "").replace("-mobile.mp4", "")
    val filenameLower = filename.lowercase()
    return "https://api.redgifs.com/v2/gifs/$filenameLower/files/$filename.mp4"
}

fun to_embed_url(sdUrl: String): String {
    val uri = URI(sdUrl)
    val path = uri.path // получаем путь из URL
    val filename = path.replace("/", "").replace("-mobile.mp4", ".mp4")
    return "https://api.redgifs.com/v2/embed/discord?name=$filename"
}

val REDGIFS_THUMBS_RE = Regex(
    """https://thumbs\d+?\.redgifs\.com/(?<id>\w+)(?<type>-\w+)?\.(?<ext>\w+)(\?.+(\d|\w))?"""
)

val REDGIFS_ID_RE = Regex(
    """https://(thumbs(\d+)|api)\.redgifs\.com/(?<id>[a-zA-Z]+)"""
)

fun extractNameFromUrl(url: String): String? {
    // Регулярное выражение:
    // /            - разделитель пути (экранируем, если нужен сам символ)
    // ([^/.]+)    - захватывающая группа 1:
    //   [^/.]+    - один или более символов, которые не являются ни '/', ни '.'
    // \.mp4$       - строка должна заканчиваться на ".mp4"
    val regex = """/([^/.]+)\.mp4$""".toRegex()
    val matchResult = regex.find(url)
    return matchResult?.groups?.get(1)?.value // Получаем значение первой захватывающей группы
}