package com.client.xvideos.screens_red.use_case.network

import com.client.xvideos.feature.redgifs.http.RedGifs
import com.client.xvideos.feature.redgifs.types.CreatorResponse
import com.client.xvideos.feature.redgifs.types.MediaType
import com.client.xvideos.feature.redgifs.types.Order

suspend fun userCaseLoadGifs(
    userName: String = "lilijunex",
    items: Int = 100,
    page: Int = 1,
    ord: Order = Order.NEW,
    type: MediaType = MediaType.GIF
): CreatorResponse {
    val res = RedGifs.searchCreator(userName = userName, count = items, page = page, type = type, order = ord)
    return res
}





