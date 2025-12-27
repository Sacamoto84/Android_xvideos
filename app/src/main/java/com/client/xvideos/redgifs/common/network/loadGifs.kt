package com.client.xvideos.redgifs.common.network

import com.client.xvideos.redgifs.network.api.RedApi
import com.client.xvideos.redgifs.model.CreatorResponse
import com.client.xvideos.redgifs.model.MediaType
import com.client.xvideos.redgifs.model.Order

suspend fun loadGifs(
    userName: String = "lilijunex",
    items: Int = 100,
    page: Int = 1,
    ord: Order = Order.LATEST,
    type: MediaType = MediaType.GIF,
    redApi: RedApi
): Result<CreatorResponse> {
    val res = redApi.searchCreator(userName = userName, count = items, page = page, type = type, order = ord)
    return res
}





