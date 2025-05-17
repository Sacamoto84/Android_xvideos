package com.client.xvideos.feature.redgifs

import com.client.xvideos.feature.redgifs.types.UserInfo
import java.time.Instant
import java.time.ZoneOffset

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







//
fun _users_iter(i: List<UserInfo>): List<User> {

    val res = mutableListOf<User>()

    for (ui in i) {

        val user = User(
            creation_time = Instant.ofEpochSecond(ui.creationtime).atZone(ZoneOffset.UTC),
            description = ui.description,
            followers = ui.followers,
            following = ui.following,
            gifs = ui.gifs,
            name = ui.name.takeIf { it.isNotEmpty() },
            profile_image_url = ui.profileImageUrl,
            profile_url = ui.profileUrl.takeIf { it.isNotEmpty() },
            published_collections = ui.publishedCollections,
            published_gifs = ui.publishedGifs,
            status = ui.status,
            subscription = ui.subscription,
            url = ui.url,
            username = ui.username,
            verified = ui.verified,
            views = ui.views,
            poster = ui.poster,
            preview = ui.preview,
            thumbnail = ui.thumbnail,

            links = buildList<Map<String, String>> {
                listOfNotNull(
                    ui.socialUrl1, ui.socialUrl2, ui.socialUrl3, ui.socialUrl4,
                    ui.socialUrl5, ui.socialUrl6, ui.socialUrl7, ui.socialUrl8,
                    ui.socialUrl9, ui.socialUrl10, ui.socialUrl11, ui.socialUrl12,
                    ui.socialUrl13, ui.socialUrl14, ui.socialUrl15, ui.socialUrl16,
                    ui.socialUrl17
                ).forEach { add(mapOf("url" to it)) }
            }.ifEmpty { null }
        )
        res.add(user)
    }

    return res
}