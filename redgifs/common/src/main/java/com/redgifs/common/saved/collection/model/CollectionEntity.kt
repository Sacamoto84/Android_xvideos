package com.redgifs.common.saved.collection.model

import com.redgifs.model.GifsInfo
import com.google.gson.annotations.SerializedName

data class CollectionEntity(
    @SerializedName("collection") val collection: String,   // имя папки-коллекции
    @SerializedName("list") val list: List<GifsInfo>        // все объекты из *.collection внутри неё
)