package com.client.xvideos.xvideos.parcer

//https://cdn77-pic.xvideos-cdn.com/videos/thumbs169ll/6a/4f/6b/6a4f6bafe3abb03b5ea6108ab18ff1ad/6a4f6bafe3abb03b5ea6108ab18ff1ad.30.jpg
//https://cdn77-pic.xvideos-cdn.com/videos/videopreview/6a/4f/6b/6a4f6bafe3abb03b5ea6108ab18ff1ad_169.mp4

fun parserVideoPreviewFromImageUrl(s: String): String {

    if (s == "null")
        return "null"

    val l = s.split("/").toMutableList()
    l.removeAt(l.lastIndex)
    l[4] = "videopreview"
    l[l.lastIndex] += "_169.mp4"
    val a = l.joinToString("/")
    return a
}