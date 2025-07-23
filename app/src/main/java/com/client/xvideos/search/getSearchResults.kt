package com.client.xvideos.search

import com.client.xvideos.urlStart
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpHeaders
import timber.log.Timber

suspend fun getSearchResults(query: String): String? {

    val client = HttpClient(OkHttp)
    {
//        install(Logging) {
//            level = LogLevel.INFO
//        }
        install(HttpTimeout)
        {
            requestTimeoutMillis = Long.MAX_VALUE
        }

        defaultRequest {
            headers.append("Referer", "https://www.redgifs.com/")
            headers.append("Origin", "https://www.redgifs.com")
            headers.append(HttpHeaders.UserAgent, "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/136.0.0.0 YaBrowser/25.6.0.0 Safari/537.36")
            headers.append(HttpHeaders.Accept, "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8")
            headers.append(HttpHeaders.AcceptEncoding, "identity")
            headers.append(HttpHeaders.AcceptLanguage, "ru,en;q=0.9")
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