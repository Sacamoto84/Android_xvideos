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


