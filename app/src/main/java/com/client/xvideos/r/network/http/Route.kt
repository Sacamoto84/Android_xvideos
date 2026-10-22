package com.client.xvideos.r.network.http

import io.ktor.http.encodeURLParameter

class Route(val method: String, val path: String, vararg parameters: Pair<String, Any>) {

    val BASE = "https://api.redgifs.com"

    val url: String

    init {
        var tempUrl = BASE + path
        if (parameters.isNotEmpty()) {
            val formattedParams = parameters.associate { (k, v) ->
                // Кодируем всё, кроме unreserved-символов (в т.ч. '%' и не-ASCII), пробел — %20.
                k to if (v is String) v.encodeURLParameter() else v.toString()
            }
            tempUrl = tempUrl.formatWithMap(formattedParams)
        }
        this.url = tempUrl
    }

    // Helper function to mimic Python's url formatting with map
    private fun String.formatWithMap(params: Map<String, Any>): String {
        var result = this
        params.forEach { (key, value) ->
            result = result.replace("{$key}", value.toString())
        }
        return result
    }

}