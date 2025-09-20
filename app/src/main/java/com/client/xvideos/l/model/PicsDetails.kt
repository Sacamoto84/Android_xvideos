package com.client.xvideos.l.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import com.google.gson.annotations.SerializedName

@Parcelize
data class PicsDetails(
    @SerializedName("height") val height: Int, //"846"
    @SerializedName("width") val width: Int, //"1280"
    @SerializedName("is_animated") val is_animated: Boolean,
    @SerializedName("url_to_original") val url_to_original: String?,
    @SerializedName("url_to_video") val url_to_video: String?,
    @SerializedName("album") val album: String = "null",
    @SerializedName("thumbnails") val thumbnails: List<Thumbnails> = emptyList()
) : Parcelable

@Parcelize
data class Thumbnails(
    @SerializedName("width") val width: Int,    //640,
    @SerializedName("height") val height: Int,  //3779,
    @SerializedName("size") val size: String,   //"small",
    @SerializedName("url") val url: String      //"https://..."
) : Parcelable

/**
 * Thumbnail size configuration enum with display value mapping
 */
enum class ThumbnailsSize(
    val value: String,
    val displayName: String
) {
    XMAX("xMax", "Large"),
    SMALL("small", "Medium"),
    LARGE_THUMBALIST("large_thumbnail", "Small");

    companion object {
        /**
         * Find ThumbnailsSize by its value
         */
        fun fromValue(value: String): ThumbnailsSize? =
            values().find { it.value == value }

        /**
         * Find ThumbnailsSize by its display name
         */
        fun fromDisplayName(displayName: String): ThumbnailsSize? =
            values().find { it.displayName == displayName }

        /**
         * Get all available display names
         */
        val displayNames: List<String> = values().map { it.displayName }
    }
}


//[
//{
//    "width": 1680,
//    "height": 2044,
//    "size": "xMax",
//    "url": "https://ah-img.luscious.net/Senred/554756/img_20250420_143600_01K3KQKZAWZVQB2X2RQXE7CWDC.1680x0.jpg"
//},
//{
//    "width": 640,
//    "height": 779,
//    "size": "small",
//    "url": "https://ah-img.luscious.net/Senred/554756/img_20250420_143600_01K3KQKZAWZVQB2X2RQXE7CWDC.640x0.jpg"
//},
//{
//    "width": 315,
//    "height": 384,
//    "size": "large_thumbnail",
//    "url": "https://ah-img.luscious.net/Senred/554756/img_20250420_143600_01K3KQKZAWZVQB2X2RQXE7CWDC.315x0.jpg"
//}
//]