package com.client.xvideos.parcer

import org.jsoup.Jsoup
import org.jsoup.nodes.Document

fun parserItemVideo(html: String): String?{
    // Парсим HTML-документ
    val document: Document = Jsoup.parse(html)

    // Находим все видео-блоки
    val videoBlocks = document.select("#video-player-bg > script:nth-child(6)")
    val a = videoBlocks.html()


    return a
}

fun parserItemVideoTags(html: String): String?{
    // Парсим HTML-документ
    val document: Document = Jsoup.parse(html)

    // Находим все видео-блоки
    val videoBlocks = document.select("#main > div.video-metadata.video-tags-list")
    val a = videoBlocks.html()

    //#main > div.video-metadata.video-tags-list.ordered-label-list.cropped.opened

    val document1 = Jsoup.parse(html)
    val uploader = document1.selectFirst("li.main-uploader .name")?.text() ?: "Неизвестный"
    val model = document1.selectFirst("li.model .name")?.text() ?: "Неизвестная модель"

    val tags = document1.select("li a.is-keyword").map { it.text() }

    println("!!! Uploader: $uploader")
    println("!!! Model: $model")
    println("!!! Tags: ${tags.joinToString(", ")}")
    return a
}
