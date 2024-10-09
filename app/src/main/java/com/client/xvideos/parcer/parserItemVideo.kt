package com.client.xvideos.parcer

fun parserItemVideo(html: String): String?{
    val l = html.split("\n")
    val a = l.filter { it.contains("contentUrl") }
    val regex = """"contentUrl":\s*"(https?://[^"]+)"""".toRegex()
    val matchResult = regex.find(a[0])
    val url = matchResult?.groupValues?.get(1)
    return url
}