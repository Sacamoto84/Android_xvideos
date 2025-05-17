package com.client.xvideos.feature.redgifs

import com.client.xvideos.feature.redgifs.http.HTTP

object RedGigsAPI {

    val http = HTTP()

}



//class MediaItemTypeAdapterFactory : TypeAdapterFactory {
//    override fun <T> create(gson: Gson, type: TypeToken<T>): TypeAdapter<T>? {
//        return if (type.rawType == MediaItem::class.java) {
//            MediaItemTypeAdapter() as TypeAdapter<T>
//        } else {
//            null
//        }
//    }
//}