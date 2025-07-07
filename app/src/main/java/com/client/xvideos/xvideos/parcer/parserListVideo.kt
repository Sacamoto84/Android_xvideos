package com.client.xvideos.xvideos.parcer

import com.client.xvideos.feature.country.currentCountries
import com.client.xvideos.feature.country.getFlagEmoji
import com.client.xvideos.xvideos.model.GalleryItem
import org.jsoup.nodes.Document
import org.jsoup.Jsoup
import org.jsoup.nodes.Element

fun parserListVideo(html: String): List<GalleryItem> {

    val list = mutableListOf<GalleryItem>()

    // Парсим HTML-документ
    val document: Document = Jsoup.parse(html)


    val regex = Regex("""\bflag-([a-z]{2})\b""")
    val match = regex.find(document.select("#site-localisation").toString())
    val countryCode = match?.groupValues?.get(1)
    currentCountries = getFlagEmoji("flag-$countryCode")

    // Находим все видео-блоки
    val videoBlocks = document.select("div.frame-block")

    for (block: Element in videoBlocks) {
        // Извлекаем данные
        val videoId = block.attr("data-id")
        val videoTitle = block.selectFirst("p.title a")?.text() ?: "No title"
        val href = block.selectFirst("p.title a")?.attr("href") ?: "No link"
        val videoDuration = block.selectFirst("span.duration")?.text() ?: "No duration"

        // Получаем значение data-src
        val dataSrc: String = block.selectFirst("img[data-src]")?.attr("data-src") ?: "null"
        val videoPreviewUrl = parserVideoPreviewFromImageUrl(dataSrc)

        val channelName = block.selectFirst("p.metadata .name")?.text() ?: "No channel"
        val views = block.selectFirst("p.metadata")?.text()?.split("Просмотров")?.get(0)?.trim() ?: "No views"

        //val views = block.select("p.metadata span").last()?.ownText()?.trim() ?: "No views"

        val channelLink = block.selectFirst("p.metadata a")?.attr("href") ?: "No channel link"

        list.add(
            GalleryItem(
                id = videoId.toLong(),
                title = videoTitle,
                href = href,
                duration = videoDuration,
                views = views,
                channel = channelName,
                previewImage = dataSrc,
                previewVideo = videoPreviewUrl,
                nameProfile = "TODO()",
                linkProfile = channelLink
            )
        )

//        // Выводим результат
//        println("ID: $videoId")
//        println("Title: $videoTitle")
//        println("Link: $videoLink")
//        println("Duration: $videoDuration")
//        println("Channel: $channelName")
//        println("Views: $views")
//        println("Image: $dataSrc")
//        println("Preview: $videoPreviewUrl")
//        println("-------------")

    }

    return list

}