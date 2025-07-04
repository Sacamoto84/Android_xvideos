package com.client.xvideos.red.common.saved.collection.model

import com.client.xvideos.feature.redgifs.types.GifsInfo
import com.google.gson.annotations.SerializedName

data class CollectionEntity(
    @SerializedName("collection") val collection: String,   // имя папки-коллекции
    @SerializedName("list") val list: List<GifsInfo>        // все объекты из *.collection внутри неё
)