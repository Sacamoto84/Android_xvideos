package com.client.xvideos.model

data class HTML5PlayerConfig(
    val videoTitle: String,
    val encodedIdVideo: String,
    val sponsors: List<Sponsor>,
    val videoUrlLow: String,
    val videoUrlHigh: String,
    val videoHLS: String,
    val thumbUrl: String,
    val thumbUrl169: String,
    val relatedVideos: Any?, // Assuming video_related is a complex object
    val thumbSlide: String,
    val thumbSlideBig: String,
    val thumbSlideMinute: String,
    val idCDN: String,
    val idCdnHLS: String,
    val fakePlayer: Boolean,
    val desktopView: Boolean,
    val seekBarColor: String,
    val uploaderName: String,
    val videoURL: String,
    val staticPath: String,
    val https: Boolean = true,
    val viewData: String
)

data class Sponsor(
    val link: String,
    val desc: String,
    val records2257: String,
    val name: String
)