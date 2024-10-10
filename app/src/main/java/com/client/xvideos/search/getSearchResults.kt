package com.client.xvideos.search

import com.client.xvideos.urlStart
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import timber.log.Timber

suspend fun getSearchResults(query: String): String? {

    val client = HttpClient(OkHttp)
    {
        install(Logging) {
            level = LogLevel.INFO
        }
        install(HttpTimeout)
        {
            requestTimeoutMillis = Long.MAX_VALUE
        }
    }

    val url = "${urlStart}/search-suggest/$query" // Замените на реальный URL

    try {
        val response = client.get(url)
        //println(response.toString())
        return response.bodyAsText()
    } catch (e: Exception) {
        Timber.e("Ошибка " + e.message)
    }

    return null
}