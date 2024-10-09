package com.client.xvideos.parcer

import com.client.xvideos.model.GalleryItem
import org.jsoup.nodes.Document
import org.jsoup.Jsoup
import org.jsoup.nodes.Element

fun parserListVideo(html: String): List<GalleryItem> {

    val list = mutableListOf<GalleryItem>()

    // Парсим HTML-документ
    val document: Document = Jsoup.parse(html)

    // Находим все видео-блоки
    val videoBlocks = document.select("div.frame-block")

    for (block: Element in videoBlocks) {
        // Извлекаем данные
        val videoId = block.attr("data-id")
        val videoTitle = block.selectFirst("p.title a")?.text() ?: "No title"
        val videoLink = block.selectFirst("p.title a")?.attr("href") ?: "No link"
        val videoDuration = block.selectFirst("span.duration")?.text() ?: "No duration"


        // Получаем значение data-src
        val dataSrc: String = block.selectFirst("img[data-src]")?.attr("data-src") ?: "null"
        val videoPreviewUrl = parserVideoPreviewFromImageUrl(dataSrc)

        val channelName = block.selectFirst("p.metadata .name")?.text() ?: "No channel"
        val views = block.selectFirst("p.metadata")?.text()?.split("Просмотров")?.get(0)?.trim()
            ?: "No views"

        list.add(GalleryItem(
            id = videoId.toLong(),
            title = videoTitle,
            link = videoLink,
            duration = videoDuration,
            views = views,
            channel = channelName,
            previewImage = dataSrc,
            previewVideo = videoPreviewUrl
        ))

        // Выводим результат
        println("ID: $videoId")
        println("Title: $videoTitle")
        println("Link: $videoLink")
        println("Duration: $videoDuration")
        println("Channel: $channelName")
        println("Views: $views")
        println("Image: $dataSrc")
        println("Preview: $videoPreviewUrl")
        println("-------------")
    }

    return list

}