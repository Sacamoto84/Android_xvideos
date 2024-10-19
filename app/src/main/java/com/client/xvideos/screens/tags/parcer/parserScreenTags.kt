package com.client.xvideos.screens.tags.parcer

import com.client.xvideos.screens.tags.model.ModelScreenTag
import com.client.xvideos.screens.tags.model.ModelTagItem
import org.jsoup.Jsoup
import org.jsoup.nodes.Document

fun parserScreenTags(html: String): ModelScreenTag {

    val listItems = mutableListOf<ModelTagItem>()

    // Парсим HTML-документ
    val document: Document = Jsoup.parse(html)


    val pageTitle =
        document.selectFirst("h2.page-title") // Текст внутри h2 без дочерних элементов //document.select("#main > h2")
    // Извлекаем текст
    val title0 = pageTitle?.ownText() ?: "?" // Текст внутри h2 без дочерних элементов
    val title1 = pageTitle?.selectFirst("span.sub")?.text() ?: "?" // Текст внутри span.sub

    val a = document.selectFirst("#content > div.mozaique.cust-nb-cols")
    val videos = a?.select("div.frame-block.thumb-block")

    if (videos != null) {
        for (video in videos) {
            val titleElement = video.selectFirst("p.title a")
            val title = titleElement?.attr("title") ?: "Без названия"
            val videoUrl = titleElement?.attr("href") ?: "Нет ссылки"
            val duration = video.selectFirst("p.title .duration")?.text() ?: "Нет информации"

            val channelName = video.selectFirst("p.metadata .name")?.text() ?: "Нет имени канала"
            val views = video.selectFirst("p.metadata .bg > span > span")?.ownText()?.trim() ?: "-"
            val profileLink = video.selectFirst("p.metadata a")?.attr("href") ?: ""

            listItems.add(
                ModelTagItem(
                    title = title,
                    href = videoUrl,
                    duration = duration,
                    views = views,
                    nameProfile = channelName,
                    linkProfile = profileLink
                )
            )

            println("Название: $title")
            println("Ссылка на видео: $videoUrl")
            println("Длительность: $duration")

            println("Просмотры: $views")

            println("Канал: $channelName")
            println("Профиль: $profileLink")
            println("---------------")
        }
    }

    val res = ModelScreenTag(title0 = title0, title1 = title1, items = listItems)
    return res
}