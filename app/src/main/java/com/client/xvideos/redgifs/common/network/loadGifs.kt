package com.client.xvideos.redgifs.common.network

import com.redgifs.network.api.RedApi
import com.redgifs.model.CreatorResponse
import com.redgifs.model.MediaType
import com.redgifs.model.Order

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





