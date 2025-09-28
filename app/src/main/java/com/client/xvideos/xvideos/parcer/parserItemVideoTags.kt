package com.client.xvideos.xvideos.parcer

import com.client.xvideos.xvideos.screens.videoplayer.model.TagsMainUploaderPornstar
import com.client.xvideos.xvideos.screens.videoplayer.model.TagsModel
import org.jsoup.Jsoup
import org.jsoup.nodes.Document

fun parserItemVideoTags(html: String): TagsModel {
    // Парсим HTML-документ
    val document: Document = Jsoup.parse(html)

    // Находим все видео-блоки
    val videoBlocks = document.select("#main > div.video-metadata.video-tags-list")
    val a = videoBlocks.html()

    //#main > div.video-metadata.video-tags-list.ordered-label-list.cropped.opened

    val document1 = Jsoup.parse(html)

    val listMain = mutableListOf<TagsMainUploaderPornstar>()
    val uploader = document1.select("li.main-uploader")
    uploader.forEach {
        val href = it.selectFirst("a[href]")?.attr("href") ?: "Unknown"
        val name = it.selectFirst("span.name")?.ownText() ?: "Unknown"
        val count = it.selectFirst("span.count")?.text() ?: "0"
        listMain.add(TagsMainUploaderPornstar(href = href, name = name, count = count))
    }

    val listPornstar = mutableListOf<TagsMainUploaderPornstar>()
    val model = document1.select("li.model")
    model.forEach {
        val href = it.selectFirst("a[href]")?.attr("href") ?: "Unknown"
        val name = it.selectFirst("span.name")?.ownText() ?: "Unknown"
        val count = it.selectFirst("span.count")?.text() ?: "0"
        listPornstar.add(TagsMainUploaderPornstar(href = href, name = name, count = count))
    }

    val tags = document1.select("li a.is-keyword").map { it.text() }

    //println("!!! Uploader: $uploader")
    //println("!!! Model: $model")
    //println("!!! Tags: ${tags.joinToString(", ")}")

    return TagsModel(listMain, listPornstar, tags)
}

//<li class="main-uploader">
//<a class="btn btn-default label main uploader-tag hover-name" href="/milfed">
//<span class="name">
//<span class="icon-f icf-device-tv-v2"/>
//Milfed
//</span>
//<span class="user-subscribe" data -user-id="568100199" data -user-profile="milfed">
//<span class="count">359k</span>
//</span>
//</a>
//</li>


//data class TagsMainUploaderPornstar(val href: String, val name: String, val count: String)

//<li class="model">
//<a class="btn btn-default label profile hover-name is-pornstar" data-id="306248827" href="/pornstars/london-river">
//<span class="model-star-sub icon-f icf-star-o" data-user-id="306248827" data-user-profile="london-river"/>
//<span class="name">London River</span>
//<span class="user-subscribe" data-user-id="306248827" data-user-profile="london-river">
//<span class="count">198k</span>
//</span>
//</a>
//</li>
