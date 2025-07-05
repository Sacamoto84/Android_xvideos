package com.client.xvideos.redgifs.common.network

import com.client.xvideos.redgifs.network.api.RedApi
import com.client.xvideos.redgifs.network.types.CreatorResponse
import com.client.xvideos.redgifs.network.types.MediaType
import com.client.xvideos.redgifs.network.types.Order

suspend fun loadGifs(
    userName: String = "lilijunex",
    items: Int = 100,
    page: Int = 1,
    ord: Order = Order.NEW,
    type: MediaType = MediaType.GIF
): CreatorResponse {
    val res = RedApi.searchCreator(userName = userName, count = items, page = page, type = type, order = ord)
    return res
}





