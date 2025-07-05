package com.client.xvideos.redgifs.common.saved.collection.model

import com.client.xvideos.redgifs.network.types.GifsInfo
import com.google.gson.annotations.SerializedName

data class CollectionEntity(
    @SerializedName("collection") val collection: String,   // имя папки-коллекции
    @SerializedName("list") val list: List<GifsInfo>        // все объекты из *.collection внутри неё
)