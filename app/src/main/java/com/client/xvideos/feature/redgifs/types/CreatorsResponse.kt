package com.client.xvideos.feature.redgifs.types

data class CreatorsResponse(
    val page: Int = 0,             //
    val pages: Int = 0,            //
    val total: Int = 0,            //
    val items: List<UserInfo> = emptyList(), //
)


