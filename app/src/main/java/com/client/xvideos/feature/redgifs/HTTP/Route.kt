package com.client.xvideos.feature.redgifs.HTTP

class Route(method: String, path: String, vararg parameters: Pair<String, Any>) {

    companion object {
        const val BASE = "https://api.redgifs.com"
    }

    val method: String
    val path: String
    val url: String

    init {
        this.method = method
        this.path = path
        var tempUrl = BASE + path
        if (parameters.isNotEmpty()) {
            val formattedParams = parameters.associate { (k, v) ->
                k to if (v is String) encodeURIComponent(v) else v.toString()
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

    // Helper function to encode URL components (equivalent to Python's quote)
    private fun encodeURIComponent(s: String): String {
        return s.replace(" ", "%20")
            .replace("!", "%21")
            .replace("\"", "%22")
            .replace("#", "%23")
            .replace("$", "%24")
            .replace("&", "%26")
            .replace("'", "%27")
            .replace("(", "%28")
            .replace(")", "%29")
            .replace("*", "%2A")
            .replace("+", "%2B")
            .replace(",", "%2C")
            .replace("/", "%2F")
            .replace(":", "%3A")
            .replace(";", "%3B")
            .replace("=", "%3D")
            .replace("?", "%3F")
            .replace("@", "%40")
            .replace("[", "%5B")
            .replace("]", "%5D")
    }
}