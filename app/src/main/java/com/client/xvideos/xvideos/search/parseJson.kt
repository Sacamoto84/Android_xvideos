package com.client.xvideos.xvideos.search

import com.client.xvideos.xvideos.search.model.SearchResult
import kotlinx.serialization.json.Json

fun parseJson(json: String): SearchResult {
    return Json.decodeFromString(json)
}